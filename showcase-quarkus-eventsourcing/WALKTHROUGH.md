# Walkthrough

The following topics are meant to lead you through the code and highlight most important code pieces from different angles. 

### Topics

* [Connecting CDI to AxonFramework](#Connecting-CDI-to-AxonFramework)
* [Connecting JTA Transactions to AxonFramework](#Connecting-JTA-Transactions-to-AxonFramework)
* [Connecting JSON Binding to AxonFramework](#Connecting-JSON-Binding-to-AxonFramework)

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

## References

* [Axon Framework CDI Support][AxonFrameworkCDI]
* [AxonFramework Parameter Resolver][AxonFrameworkParameterResolver]
* [Building a native executable][QuarkusNativeExecutable]
* [CDI Portable Extension][CDIExtension]
* [Eclipse MicroProfile][MicroProfile]
* [Flyway Version control for your database][Flyway]
* [Jakarta JSON Binding][JSONBinding]
* [Jakarta Transactions (JTA)][JakartaTransaction]
* [Java ServiceLoader][ServiceLoader]
* [Quarkus Context and Dependency Injection (CDI)][QuarkusCDI]
* [Quarkus Application Initialization and Termination][QuarkusLivecycle]

[AxonFrameworkCDI]: https://github.com/AxonFramework/extension-cdi
[AxonFrameworkParameterResolver]: https://axoniq.io/blog-overview/parameter-resolvers-axon
[CDIExtension]: https://docs.jboss.org/weld/reference/latest/en-US/html/extend.html
[Flyway]: https://flywaydb.org
[JakartaTransaction]: https://jakarta.ee/specifications/transactions/
[JSONBinding]: https://jakarta.ee/specifications/jsonb/2.0/jakarta-jsonb-spec-2.0.html
[MicroProfile]: https://projects.eclipse.org/projects/technology.microprofile
[QuarkusCDI]: https://quarkus.io/guides/cdi-reference
[QuarkusNativeExecutable]: https://quarkus.io/guides/building-native-image-guide
[QuarkusLivecycle]: https://quarkus.io/guides/lifecycle
[ServiceLoader]: https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html