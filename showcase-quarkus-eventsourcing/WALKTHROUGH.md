# Walkthrough

The following topics are meant to lead you through the code and highlight most important code pieces from different angles. 

## Topics

* [Structure](#Structure)
* [Connecting CDI to AxonFramework](#Connecting-CDI-to-AxonFramework)
* [Connecting JTA Transactions to AxonFramework](#Connecting-JTA-Transactions-to-AxonFramework)
* [Connecting JSON Binding to AxonFramework](#Connecting-JSON-Binding-to-AxonFramework)
* [Mitigate Core API dependencies](#Mitigate-Core-API-dependencies)
* [AxonFramework behind the boundary](#AxonFramework-behind-the-boundary)
* [ArchUnit in action](#ArchUnit-in-action)
* [Flyway in action](#Flyway-in-action)
* [Vanilla JavaScript UI](#Vanilla-JavaScript-UI)

## Structure

* [message](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/message) contains the core API with all event-, command- and query-message types.
* [messaging](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging) connects AxonFramework to the application and provides interfaces (boundary) and adapters to AxonFrameworks as well as it's configuration. 
* [domain/model](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/domain/model) contains the command-side model with the aggregate(s).
* [domain/model](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/query/model) contains the read-side model with the projections.
* [service](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/service) contains the REST services that are used by the user interface.

## Connecting CDI to AxonFramework

[Axon Framework CDI Support][AxonFrameworkCDI] already provides a CDI (Context and Dependency Injection) integration by providing a [CDI Portable Extension][CDIExtension].
Unfortunately, this doesn't work for Quarkus, since [Quarkus CDI][QuarkusCDI]
only implements a subset of the CDI specification and doesn't support [CDI Portable Extensions][CDIExtension].

### Bean Discovery

To most important part is the discovery of all building blocks (beans) of AxonFramework. This includes for example finding all aggregates, event handlers, command handlers, query handlers, ...

This is done inside [AxonComponentDiscovery.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/inject/cdi/AxonComponentDiscovery.java), whereas the most important line is probably the following:

```java
Set<Bean<?>> beans = beanManager.getBeans(Object.class, AnnotationLiteralAny.ANY);
```

This line seems to work for pretty any CDI implementation including [Quarkus CDI][QuarkusCDI]
and returns all beans (of any type) that CDI can discover (in respect of its configuration). 

### Axon Configuration

[AxonConfiguration.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/AxonConfiguration.java) takes all discovered beans and registers them accordingly. It also wires up custom settings (e.g. database schema) and global settings (e.g. bean validation). 

### Parameter Resolver

[CdiParameterResolverFactory.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/inject/cdi/CdiParameterResolverFactory.java) resolves dependencies with CDI. 

[AxonFramework Parameter Resolver][AxonFrameworkParameterResolver] would normally be registered using [ServiceLoader][ServiceLoader]. This lead to issues in Quarkus dev mode, so it was solved programmatically inside the method "parameterResolvers" in [AxonConfiguration.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/AxonConfiguration.java).

### Application startup

Axon configuration is all done the first time it is used. The disadvantage of it is, that the application might benefit from a warm up. The advantage is, that this can be done strongly within CDI standard. Even if `@Observes @Initialized(ApplicationScope.class)` is standard and could be used to call a method on server start, Quarkus would run this e.g. in native mode during build. For details see [Quarkus Application Initialization and Termination][QuarkusLivecycle].

## Connecting JTA Transactions to AxonFramework

[Jakarta Transactions (JTA)][JakartaTransaction] are connected with
[JtaTransactionManager.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/transaction/jta/JtaTransactionManager.java), which is then configured as transaction manager for axon in [AxonConfiguration.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/AxonConfiguration.java). 

This is pretty similar to [AxonFramework/cdi JtaTransactionManager.java](https://github.com/AxonFramework/cdi/blob/master/extension/src/main/java/org/axonframework/cdi/transaction/JtaTransactionManager.java).

## Connecting JSON Binding to AxonFramework

[JsonbSerializer.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/serializer/jsonb/axon/JsonbSerializer.java) implements [JSON Binding][JSONBinding] as Serializer for AxonFramework. As an alternative, Jackson could also be used with Quarkus and AxonFramework. [JSON Binding][JSONBinding] had be chosen here to be fully compliant to the MicroProfile Standard.

AxonFramework has build-in support for Jackson JSON serializer. Some of its internal serializable data types need to be adapted to be used with [JSON Binding][JSONBinding]. 
The ones needed here are internally registered in [JsonbAxonAdapterRegister.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/serializer/jsonb/axon/adapter/JsonbAxonAdapterRegister.java). Except for the generic [JsonbMetaDataAdapter.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/serializer/jsonb/axon/adapter/JsonbMetaDataAdapter.java) and [JsonbReplayTokenAdapter.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/serializer/jsonb/axon/adapter/JsonbReplayTokenAdapter.java) they shouldn't be needed any more since [AxonFramework PullRequest #1163](https://github.com/AxonFramework/AxonFramework/pull/1163).

## Mitigate Core API dependencies

The core API contains value objects of all messages (events, commands, queries). These can be found in the package [message](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/message).

As a reference [AxonFramework Giftcard Example][AxonFrameworkGiftcardExample] shows how these could be defined using Kotlin (see "coreapi" package"). 

The core API might become a separate module and will be shared upon command- and query-side implementations. Shared libraries (e.g. APIs) need to be treated with special care. Changes might introduce changes in dependent modules (coupling). They may also introduce version conflicts, if the dependent module needs a library in a different version. To mitigate that, the shared API should ideally be self contained and only depend on java itself. 

**&#8505;** Currently (2021) there is a dependency to the bean validation API. This could also be replaced.

### Command message types without axon dependency

Command messages need a property that matches the id of the aggregate they belong to.
Since they may also contain other properties, the property with the aggregate identifier needs to be annotated. There is a way to get this done without depending on axon (in the API):
* Provide a own annotation (that itself depends only on java) within the API like 
[CommandTargetAggregateIdentifier.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/message/command/CommandTargetAggregateIdentifier.java)
* Annotate the command type with the co-located annotation like in [ChangeNicknameCommand.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/message/command/account/ChangeNicknameCommand.java)
* Configure AxonFramework to use the custom annotation like in [AxonAggregateConfiguration.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/AxonAggregateConfiguration.java)

The following code shows the essential part of the configuration:

```java
AnnotationCommandTargetResolver.builder()
				.targetAggregateIdentifierAnnotation(CommandTargetAggregateIdentifier.class)
				.targetAggregateVersionAnnotation(CommandTargetAggregateVersion.class).build();
```

### Message Value Objects without JSON library dependencies

Plain old java objects (POJO) are widely supported by almost any serialization (JSON, XML,..) library.
When Domain Driven Design is applied, value objects should be immutable. 
Their properties should only be set once during creation and should then remain unchangeable. 
This is usually done by using constructor parameters. To simplify creating complex objects, builders can be provided as well. 

When it comes to immutable value objects using constructor parameters, serialization libraries need to know which field should be mapped to which constructor argument in the right order. If there are a couple of constructors, it gets even more complicated. 

Therefore, serialization libraries provide annotations for constructors. This introduces additional dependencies. These can be avoided by using the build-in [java.beans.ConstructorProperties Annotation][ConstructorProperties], that is now widely supported (Jackson, Yasson, ...). 

As an example have a look at [ChangeNicknameCommand.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/message/command/account/ChangeNicknameCommand.java).

### AxonFramework behind the boundary

Following "Entity Control Boundary" or "Ports'n'Adapters", an extreme but interesting experiment is to define interfaces between the core (domain) of the application and AxonFramework. This is not meant to be an advice to do so. It has a couple of pros and cons as listed below. But it is for sure an interesting experiment.

#### Services and Adapters

As an example, AxonFramework's `CommandGateway` is represented inside the application by the boundary interface [CommandEmitterService.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/command/boundary/CommandEmitterService.java) and implemented by the [CommandEmitterAdapter.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/command/axon/CommandEmitterAdapter.java). The adapter is created in [AxonConfiguration.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/AxonConfiguration.java)  which also contains the CDI producer for the application.
[AccountResource.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/service/account/AccountResource.java) uses the [CommandEmitterService.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/command/boundary/CommandEmitterService.java) uses [CommandEmitterService.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/command/boundary/CommandEmitterService.java) to send commands as it would be done with the `CommandGateway`.

The core of the application only uses on the interface [CommandEmitterService.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/command/boundary/CommandEmitterService.java) which only contains those methods of an command gateway that are actually used by the application. It could also be further adapted (e.g. other reactive framework) to perfectly fit the needs of the application.

The implementation and connection to AxonFramework all happens in the [messaging](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging) package in the two classes mentioned above. So if anything changes inside AxonFramework, these two classes are the only one the could be affected in most cases.

#### Meta-Annotations

AxonFramework not only comes with interfaces like the `CommandGateway`, it also provides annotations to identify building blocks like a `CommandHandler`.
To take the approach of an abstraction between application core code and AxonFramework to an extreme level, the annotations can also be replaced by own ones. These own/custom meta-annotations are annotated  in their definition with the original framework annotations.

Here is an example:
[CommandModelCommandHandler.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/command/boundary/CommandModelCommandHandler.java).

#### Pros
 * It could be easier to update to a newer version of AxonFramework, especially when there are breaking changes. 
 * It provides additional possibilities to extend/customize functionality provided by AxonFramework dedicated to the application.
 * It defines and documents exactly those framework features that are used and hides the rest.
 * The additional abstraction could be used for integration tests, that shouldn't produce side-effects via AxonFramework, e.g. by replacing those parts by mocks. 
 
#### Cons
 * It introduces additional complexity. 
 * It is harder to compare the code to other applications, especially when different names are used.
 * It is harder to discuss AxonFramework related topics because of the customization in between.
 * Before using a new feature of the framework, the abstraction/boundary needs to be extended first.
 * Axon Aggregate tests are harder to setup because they also need to be aware of the customized parts.
 
#### Lessons learned

Updating minor version of 4.x did not lead to changes in core domain code. But it is also very likely,
that this would also have been the case without the boundary.

The extreme approach to even define meta annotations lead to [a code change](https://github.com/JohT/showcase-quarkus-eventsourcing/commit/d0d3e623f3ef8aea8b75162416372f0b44be87d0#diff-38a240ea3c3ebfc9e839fa2220fa1935d5bcc49ba1bcb58c000e9d97cda2ccb3) in 
[QueryModelResetHandler.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/query/boundary/QueryModelResetHandler.java) that wouldn't have been necessary otherwise. A transparent change inside an annotation of the framework needed also to be done on its meta annotation. This special case shows, that this can lead to an other form of coupling, even if this is might be less likely.

#### Summary

To summarize, it could be beneficial to applications with a big or fast growing core domain to put some effort in designing a boundary (e.g. interfaces) to frameworks like Axon, to be able to adapt future versions fast, customize the structure and even the behavior to perfectly fit the application and to have code that documents which parts of the frameworks are used.

This is by no means at no cost. It introduces additional complexity, makes it harder to move code between different applications with different boundaries and needs also to be maintained, especially when also replacing the annotations. For small Microservices it is likely to be less effort to adapt framework changes in the core domain code instead of maintaining its abstraction.

### ArchUnit in action

### Flyway in action

### Vanilla JavaScript UI

## References

* [Axon Framework CDI Support][AxonFrameworkCDI]
* [AxonFramework Giftcard Example][AxonFrameworkGiftcardExample]
* [AxonFramework Parameter Resolver][AxonFrameworkParameterResolver]
* [Building a native executable][QuarkusNativeExecutable]
* [CDI Portable Extension][CDIExtension]
* [java.beans.ConstructorProperties Annotation][ConstructorProperties]
* [Eclipse MicroProfile][MicroProfile]
* [Flyway Version control for your database][Flyway]
* [Jakarta JSON Binding][JSONBinding]
* [Jakarta Transactions (JTA)][JakartaTransaction]
* [Java ServiceLoader][ServiceLoader]
* [Quarkus Context and Dependency Injection (CDI)][QuarkusCDI]
* [Quarkus Application Initialization and Termination][QuarkusLivecycle]

[AxonFrameworkCDI]: https://github.com/AxonFramework/extension-cdi
[AxonFrameworkParameterResolver]: https://axoniq.io/blog-overview/parameter-resolvers-axon
[AxonFrameworkGiftcardExample]: https://github.com/AxonFramework/extension-springcloud-sample
[CDIExtension]: https://docs.jboss.org/weld/reference/latest/en-US/html/extend.html
[ConstructorProperties]: https://docs.oracle.com/javase/8/docs/api/java/beans/ConstructorProperties.html
[Flyway]: https://flywaydb.org
[JakartaTransaction]: https://jakarta.ee/specifications/transactions/
[JSONBinding]: https://jakarta.ee/specifications/jsonb/2.0/jakarta-jsonb-spec-2.0.html
[MicroProfile]: https://projects.eclipse.org/projects/technology.microprofile
[QuarkusCDI]: https://quarkus.io/guides/cdi-reference
[QuarkusNativeExecutable]: https://quarkus.io/guides/building-native-image-guide
[QuarkusLivecycle]: https://quarkus.io/guides/lifecycle
[ServiceLoader]: https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html