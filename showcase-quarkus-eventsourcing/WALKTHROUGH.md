# Walkthrough

The following topics are meant to lead through the code and highlight most important code pieces from different angles. 

## Topics

* [Main Structure](#Main-Structure)
* [Connecting CDI to AxonFramework](#Connecting-CDI-to-AxonFramework)
* [Connecting JTA Transactions to AxonFramework](#Connecting-JTA-Transactions-to-AxonFramework)
* [Connecting JSON Binding to AxonFramework](#Connecting-JSON-Binding-to-AxonFramework)
* [Connecting Bean Validation to AxonFramework](#Connecting-Bean-Validation-to-AxonFramework)
* [Mitigate Core API dependencies](#Mitigate-Core-API-dependencies)
* [AxonFramework behind the boundary](#AxonFramework-behind-the-boundary)
* [ArchUnit in action](#ArchUnit-in-action)
* [Flyway in action](#Flyway-in-action)
* [Vanilla JavaScript UI](#Vanilla-JavaScript-UI)
* [Server-Sent Events (SSE) for Subscription Queries](#Server-Sent-Events-SSE-for-Subscription-Queries)
* [Native Image](#Native-Image)
* [References](#References)

## Main Structure

* [message](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/message) contains the core API with all event-, command- and query-message types.
* [messaging](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging) connects AxonFramework to the application and provides interfaces (boundary) and adapters to AxonFrameworks as well as it's configuration. 
* [domain/model](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/domain/model) contains the command-side model with the aggregate(s).
* [query/model](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/query/model) contains the read-side model with the projections.
* [service](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/service) contains the REST services that are used by the user interface.

## Connecting CDI to AxonFramework

[Axon Framework CDI Support][AxonFrameworkCDI] already provides a CDI (Context and Dependency Injection) integration by providing a [CDI Portable Extension][CDIExtension].
Unfortunately this doesn't work for Quarkus since [Quarkus CDI][QuarkusCDI]
only implements a subset of the CDI specification and doesn't support [CDI Portable Extensions][CDIExtension].

### Bean Discovery

The most important part is discovering all building blocks (beans) of AxonFramework. This includes e.g.  finding all aggregates, event handlers, command handlers, query handlers, ...

This is done inside [AxonComponentDiscovery.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/inject/cdi/AxonComponentDiscovery.java), whereas the most important line is probably the following:

```java
Set<Bean<?>> beans = beanManager.getBeans(Object.class, AnnotationLiteralAny.ANY);
```

This line seems to work for pretty any CDI implementation including [Quarkus CDI][QuarkusCDI]
and returns all beans (of any type) that CDI discovers (in respect of its configuration). 

### Axon Configuration

[AxonConfiguration.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/AxonConfiguration.java) takes all discovered beans and registers them accordingly. It also wires up custom settings (e.g. database schema) and global settings (e.g. bean validation). 

### Parameter Resolver

[CdiParameterResolverFactory.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/inject/cdi/CdiParameterResolverFactory.java) resolves dependencies using CDI. 

[AxonFramework Parameter Resolver][AxonFrameworkParameterResolver] would normally be registered using [ServiceLoader][ServiceLoader]. This leads to issues in Quarkus dev mode, so it was solved programmatically inside the method "parameterResolvers" in [AxonConfiguration.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/AxonConfiguration.java).

### Application startup

Axon configuration is created the first time it is used. The disadvantage of this is, that the application might benefit from a warm up. The advantage is, that this works for all CDI containers including Quarkus in native mode. Even if `@Observes @Initialized(ApplicationScope.class)` is standardized in  CDI and could be used to call a method on server start, Quarkus would run this e.g. in native mode during build. For details see [Quarkus Application Initialization and Termination][QuarkusLivecycle].

## Connecting JTA Transactions to AxonFramework

[Jakarta Transactions (JTA)][JakartaTransaction] are connected with
[JtaTransactionManager.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/transaction/jta/JtaTransactionManager.java), which is then configured as transaction manager for axon in [AxonConfiguration.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/AxonConfiguration.java). 

This is pretty similar to [AxonFramework/cdi JtaTransactionManager.java](https://github.com/AxonFramework/cdi/blob/master/extension/src/main/java/org/axonframework/cdi/transaction/JtaTransactionManager.java).

## Connecting JSON Binding to AxonFramework

[JsonbSerializer.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/serializer/jsonb/axon/JsonbSerializer.java) implements [JSON Binding][JSONBinding] as `Serializer` for AxonFramework. As an alternative Jackson could be used as well. [JSON Binding][JSONBinding] had been chosen here to stay within the libraries that are included in [MicroProfile][MicroProfile].

AxonFramework has build-in support for Jackson JSON serializer. For [JSON Binding][JSONBinding], some of Axon's internal serializable data types need to be adapted. 
These are registered in [JsonbAxonAdapterRegister.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/serializer/jsonb/axon/adapter/JsonbAxonAdapterRegister.java). Except for [JsonbMetaDataAdapter.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/serializer/jsonb/axon/adapter/JsonbMetaDataAdapter.java) and [JsonbReplayTokenAdapter.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/serializer/jsonb/axon/adapter/JsonbReplayTokenAdapter.java) the remaining ones shouldn't be needed any more since [PullRequest #1163](https://github.com/AxonFramework/AxonFramework/pull/1163).

## Connecting Bean Validation to AxonFramework

[Jakarta Bean Validation][JakartaBeanValidation] lets you express constraints on object models via annotations. As described in [Axon Framework Message Intercepting][AxonFrameworkMessageIntercepting], there is a build-in dispatch interceptor to validate messages using Bean Validation.
The following code snippet shows how to attach it to the command bus: 

```java
configuration.commandBus().registerDispatchInterceptor(new BeanValidationInterceptor<>(validatorFactory));
```

Bean Validation had been removed from this showcase in favor to dependency free message types.
It still can be found in version V1.4 and earlier e.g. in [AxonConfiguration.java](https://github.com/JohT/showcase-quarkus-eventsourcing/blob/6ebae39aba2e0828f5d1e93d82cd3caeedcb7541/showcase-quarkus-eventsourcing/src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/AxonConfiguration.java#L342) and [CreateAccountCommand.java](https://github.com/JohT/showcase-quarkus-eventsourcing/blob/6ebae39aba2e0828f5d1e93d82cd3caeedcb7541/showcase-quarkus-eventsourcing/src/main/java/io/github/joht/showcase/quarkuseventsourcing/message/command/account/CreateAccountCommand.java#L11)


## Mitigate Core API dependencies

The core API contains value objects of all messages (events, commands, queries). These can be found in the package [message](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/message).

As a reference [AxonFramework Giftcard Example][AxonFrameworkGiftcardExample] shows how these could be defined using Kotlin (see "coreapi" package"). 

The core API might become a separate module and will be shared upon command- and query-side implementations. Shared libraries need to be treated with special care. Changes might introduce changes in dependent modules (coupling). They may also introduce version conflicts, if the dependent module needs a library in a different version. To mitigate that it is advantageous when the shared API is self contained and only depends on java itself. 

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
Their properties should only be set once during creation and should then remain unchanged. 
This is usually done by using constructor parameters. To simplify creating complex objects, builders can be provided as well. 

When it comes to immutable value objects using constructor parameters, serialization libraries need to know which field should be mapped to which constructor argument. If there are a couple of constructors, it gets even more complicated. 

Therefore, serialization libraries provide annotations for constructors. This introduces additional dependencies. These can be avoided by using the build-in [java.beans.ConstructorProperties Annotation][ConstructorProperties], that is now widely supported (Jackson, Yasson, ...). 

As an example have a look at the constructor in [ChangeNicknameCommand.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/message/command/account/ChangeNicknameCommand.java).

## AxonFramework behind the boundary

Following "Entity Control Boundary" or "Ports'n'Adapters", an extreme but interesting experiment is to define interfaces between the core (domain) of the application and AxonFramework. This is not meant to be an advice to do so. It has a couple of pros and cons as listed below. But it is for sure an interesting experiment.

### Services and Adapters

As an example, AxonFramework's `CommandGateway` is represented inside the application by the boundary interface [CommandEmitterService.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/command/boundary/CommandEmitterService.java) and implemented by the [CommandEmitterAdapter.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/command/axon/CommandEmitterAdapter.java). The adapter is created in [AxonConfiguration.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/AxonConfiguration.java)  which also contains the CDI producer for the application.
[AccountResource.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/service/account/AccountResource.java) uses the [CommandEmitterService.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/command/boundary/CommandEmitterService.java) to send commands as it would be done with the `CommandGateway`.

The core of the application only uses the interface [CommandEmitterService.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/command/boundary/CommandEmitterService.java) which only contains those methods of the command gateway that are actually used by the application. It could also be further adapted to perfectly fit the needs of the application.

The implementation and connection to AxonFramework all happens in the [messaging](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging) package in the two classes mentioned above. So if anything changes inside AxonFramework, these two classes are the only ones that would be affected in most cases.

### Meta-Annotations

AxonFramework not only comes with interfaces like the `CommandGateway`, it also provides annotations to identify building blocks like a `CommandHandler`.
To take the approach of an abstraction between application core code and AxonFramework to an extreme level, the annotations can also be replaced by own ones. These own/custom meta-annotations are annotated  in their definition with the original framework annotations.

Here is an example:
[CommandModelCommandHandler.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/command/boundary/CommandModelCommandHandler.java).

### Pros
 * It could be easier to update to a newer version of AxonFramework, especially when there are breaking changes. 
 * It provides additional possibilities to extend/customize functionality provided by AxonFramework dedicated to the application.
 * It defines and documents exactly those framework features that are used and hides the rest.
 * The additional abstraction could be used for integration tests, that shouldn't produce side-effects via AxonFramework, e.g. by replacing those parts by mocks. 
 
### Cons
 * It introduces additional complexity. 
 * It is harder to compare the code to other applications, especially when different names are used.
 * It is harder to discuss AxonFramework related topics because of the customization in-between.
 * Before using a new feature of the framework, the abstraction/boundary needs to be extended first.
 * Axon Aggregate tests are harder to setup because they also need to be aware of the customized parts.
 
### Lessons learned

Updating minor versions of 4.x did not lead to changes in core domain code. But it is also very likely,
that this would also have been the case without the boundary.

The extreme approach to even define meta annotations lead to [a code change](https://github.com/JohT/showcase-quarkus-eventsourcing/commit/d0d3e623f3ef8aea8b75162416372f0b44be87d0#diff-38a240ea3c3ebfc9e839fa2220fa1935d5bcc49ba1bcb58c000e9d97cda2ccb3) in 
[QueryModelResetHandler.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/query/boundary/QueryModelResetHandler.java) that wouldn't have been necessary otherwise. This special case doen't prove that the abstraction leads to more coupling, but is a good example on how Meta-Annotations tend to be more dependent on their originals (if they get changed) than it might be expected.

### Summary

To summarize, it could be beneficial to applications with a big or fast growing core domain to put some effort in designing a boundary (e.g. interfaces) to frameworks like Axon, to be able to adapt future updates fast, customize the structure and even the behavior to perfectly fit the application and to have code that documents which parts of the frameworks are used.

This is by no means at no cost. It introduces additional complexity, makes it harder to move code between different applications with different boundaries and also needs to be maintained. For small Microservices it is likely to be less effort to adapt framework changes in the whole application instead of maintaining an abstraction.

## ArchUnit in action

> [ArchUnit][ArchUnit] is a free, simple and extensible library for checking the architecture of your Java code using any plain Java unit test framework. 

[ArchitectureRulesTest.java](./src/test/java/io/github/joht/showcase/quarkuseventsourcing/ArchitectureRulesTest.java) shows how [ArchUnit][ArchUnit] can be utilized to specify and ensure  application (micro) architecture rules. Here are some test case name examples:

* "there should be no Quarkus specific dependencies"
* "command/domain model should not depend on query model"
* "boundary should not use axon directly"

[ArchUnit][ArchUnit] can also be used to test equals- and hashCode methods 
as described in [Testing all equals and hashCode methods][TestingEqualsHashcode].

## Flyway in action

> Version control for your database

[Flyway][Flyway] 
takes database script files inside the folder [resources/db](./src/main/resources/db) and executes them while the application is starting. If the database hadn't been setup yet, all of them will be applied in order. Every database change is written into a new script file using a new version number. [Flyway][Flyway] will keep track of the current database version and will only apply those scripts that are needed to keep it up to date. 

The configuration is specific to Quarkus and can be found in the [application.properties](./src/main/resources/application.properties).

### Similarities to Event Sourcing

* Any database change ("event") is represented by a separate script file ("event payload").
* Existing script files won't be changed ("immutable").
* The order of the script files are represented by their version number ("sequence number").
* They are applied ("replayed") on the database ("projection") until it is up to date ("tracking token").

## Vanilla JavaScript UI

The user interface is made with plain/vanilla JavaScript, CSS and HTML.

### UI Structure

* [src/main/javascript](./src/main/javascript) contains JavaScript sources 
* [src/test/javascript](./src/test/javascript) contains JavaScript [Jasmine][Jasmine] Unit-Tests
* [src/main/javascript/polyfills](./src/main/javascript/polyfills) contains JavaScript sources that provide functions, that are missing in older browsers.
* [META-INF/resources](./src/main/resources/META-INF/resources) contains static CSS and HTML sources
* [startup.js](./src/main/javascript/startup.js) registers JavaScript functions on page load
* [account.js](./src/main/javascript/account.js) contains the part of the application, that deals with the "account" (domain name)
* [event.js](./src/main/javascript/event.js) contains the Server-Sent Events (SSE) client
* [Maven POM](./pom.xml) also contains plugins to build the UI without Node.js.

### UI Modules

[account.js](./src/main/javascript/account.js) consists of these modules in the namespace `eventsourcing_showcase`: 

 * **AccountUI** - Represents the user interface elements, changes their properties and reads their values
 * **AccountRepository** - Represents the data source(s) (here REST service calls)
 * **AccountController** - Coordinates the modules AccountUI and AccountRepository and provides the "load" function that is called by [startup.js](./src/main/javascript/startup.js).

[event.js](./src/main/javascript/event.js) consists of one module in the namespace `eventsourcing_showcase`: 
 * **EventController** - Contains the Server-Sent Events (SSE) client, which updates the UI without refresh in a reactive manner when a new nickname is created (backed by a axon subscription query).

### UI Build

The whole application is build with [Maven][Maven], including the user interface. [Maven][Maven] is usually used for Java applications. [Node.js®][NodeJS] is popular for JavaScript applications. [Maven][Maven] might not be suitable for a large JavaScript applications. However in this case it simplifies the build, since all steps are done within one build tool.

These Maven-Plugins are used to build the user interface:

 * [jasmine-maven-plugin][jasmine-maven-plugin] runs all [Jasmine][Jasmine] unit tests on [PhantomJS], a headless browser
 * [saga-maven-plugin][saga-maven-plugin] measures JavaScript unit test coverage
 * [yuicompressor-maven-plugin][yuicompressor-maven-plugin] aggregates/copies all JavaScript sources into one `application.js` and compresses it to make it smaller and therefore faster to load.

## Server-Sent Events (SSE) for Subscription Queries

As shown in [Introducing Subscription Queries][AxonSubscriptionQueries], an Event Sourced system brings new possibilities when it comes to queries. Additionally to sending a request and displaying the results,  it is possible to "subscribe" to query result changes. This enables a reactive approach where it isn't necessary to refresh the browser or to repeatedly execute the query to get new results.

After the initial response the query updates need to be pushed to the browser. Using [MicroProfile][MicroProfile] without any additional libraries this can be done as described in
[Server-Sent Events (SSE) in JAX-RS][ServerSentEvents].

### Structure

* [event.js](./src/main/javascript/event.js) contains the SSE client. 
* [NicknameEventStreamResource.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/service/nickname/NicknameEventStreamResource.java) contains the server-side SSE endpoint.
* [NicknameEventSubscriber.java](./src/main/java/io/github/joht/showcase/quarkuseventsourcing/service/nickname/NicknameEventSubscriber.java) uses `javax.ws.rs.sse.Sse` and `javax.ws.rs.sse.SseEventSink` to notify changes

## Native Image

Detailed informations on how to build the native image and commands that come with it are documented in  [README.md](./README.md).

### Native Image Maven Profiles

These Native Image [Maven][Maven] profiles are defined in the [pom.xml](./pom.xml):

 * **native**: Builds the native image and runs the integration tests as described in [Quarkus Building a native executable][QuarkusNativeImage]
 * **native-image-agent-config**: Needs to be started with GraalVM JDK. Runs integration tests and generates native image configuration JSON files based on the runtime informations collected by the [NativeImageAgent][NativeImageAssistedConfiguration].
 * **native-image-agent-trace**: Needs to be started with GraalVM JDK. Runs integration tests and generates the native image trace JSON files based on the runtime informations collected by the [NativeImageAgent][NativeImageAssistedConfiguration]. The trace file can also be used to extract native image configuration JSON files with the GraalVM command "native-image-configure".

### Native Image Configurations

* [reflection-config.json](./reflection-config.json) contains the reflection calls that need to be included in the native image
* [resources-config.json](./resources-config.json) contains the resources that need to be included in the native image
* [native-image-caller-filter-rules.json](./native-image-caller-filter-rules.json) contains the filter rules for all things Quarkus already takes care of (so they don't need to be configured).

### GitHub Workflows:

* [native-image.yml](./../.github/workflows/native-image.yml) contains the native image build.
* [native-image-agent.yml](./../.github/workflows/native-image-agent.yml) contains the trace and configuration file generation using the native image agent.

### Results:

The results of the native image agent are pushed back into the repository by the continuous integration pipeline and can be found in [native-image-agent-results](./native-image-agent-results). A new directory is created for every new Java-, AxonFramework- and Quarkus-Version. This makes it easier to compare them without the need to switch between GIT tags. 

If all use cases of the application are covered by integration tests or in other words the native image agent can collect all necessary informations during runtime, it is theoretically possible to use the generated configuration files to automatically build the native image. Taking these configuration files to get insights to the application and then finding a way to statically generate the configuration may lead to better results though.

## References

* [ArchUnit][ArchUnit]
* [Assisted Configuration with Tracing Agent][NativeImageAssistedConfiguration]
* [Axon Framework CDI Support][AxonFrameworkCDI]
* [Axon Framework Giftcard Example][AxonFrameworkGiftcardExample]
* [Axon Framework Parameter Resolver][AxonFrameworkParameterResolver]
* [Axon Framework Message Intercepting][AxonFrameworkMessageIntercepting]
* [Building a native executable][QuarkusNativeExecutable]
* [CDI Portable Extension][CDIExtension]
* [Eclipse MicroProfile][MicroProfile]
* [Flyway Version control for your database][Flyway]
* [Introducing Subscription Queries][AxonSubscriptionQueries]
* [Jakarta Bean Validation][JakartaBeanValidation]
* [Jakarta JSON Binding][JSONBinding]
* [Jakarta Transactions (JTA)][JakartaTransaction]
* [Java Beans ConstructorProperties Annotation][ConstructorProperties]
* [Java ServiceLoader][ServiceLoader]
* [Jasmine Behavior-Driven JavaScript][Jasmine]
* [Maven][Maven]
* [Maven jasmine-maven-plugin][jasmine-maven-plugin]
* [Maven saga-maven-plugin][saga-maven-plugin]
* [Maven yuicompressor-maven-plugin][yuicompressor-maven-plugin]
* [Node.js®][NodeJS]
* [PhantomJS][PhantomJS]
* [Quarkus Application Initialization and Termination][QuarkusLivecycle]
* [Quarkus Building a native executable][QuarkusNativeImage]
* [Quarkus Context and Dependency Injection (CDI)][QuarkusCDI]
* [Server-Sent Events (SSE) in JAX-RS][ServerSentEvents]
* [ServiceLoader][ServiceLoader]
* [Testing all equals and hashCode methods][TestingEqualsHashcode]

[ArchUnit]: https://www.archunit.org
[NativeImageAssistedConfiguration]: https://www.graalvm.org/reference-manual/native-image/Agent
[AxonFrameworkCDI]: https://github.com/AxonFramework/extension-cdi
[AxonFrameworkGiftcardExample]: https://github.com/AxonFramework/extension-springcloud-sample
[AxonFrameworkParameterResolver]: https://axoniq.io/blog-overview/parameter-resolvers-axon
[AxonFrameworkMessageIntercepting]: https://docs.axoniq.io/reference-guide/v/master/axon-framework/messaging-concepts/message-intercepting#structural-validation
[AxonSubscriptionQueries]: https://axoniq.io/blog-overview/introducing-subscription-queries
[CDIExtension]: https://docs.jboss.org/weld/reference/latest/en-US/html/extend.html
[ConstructorProperties]: https://docs.oracle.com/javase/8/docs/api/java/beans/ConstructorProperties.html
[Flyway]: https://flywaydb.org
[JakartaBeanValidation]: https://beanvalidation.org
[JakartaTransaction]: https://jakarta.ee/specifications/transactions/
[Jasmine]: https://jasmine.github.io
[jasmine-maven-plugin]: https://searls.github.io/jasmine-maven-plugin/
[JSONBinding]: https://jakarta.ee/specifications/jsonb/2.0/jakarta-jsonb-spec-2.0.html
[Maven]: https://maven.apache.org
[MicroProfile]: https://microprofile.io
[NodeJS]: https://nodejs.org/en/
[PhantomJS]: https://phantomjs.org
[QuarkusCDI]: https://quarkus.io/guides/cdi-reference
[QuarkusNativeExecutable]: https://quarkus.io/guides/building-native-image-guide
[QuarkusNativeImage]: https://quarkus.io/guides/building-native-image
[QuarkusLivecycle]: https://quarkus.io/guides/lifecycle
[saga-maven-plugin]: https://timurstrekalov.github.io/saga
[ServerSentEvents]: https://www.baeldung.com/java-ee-jax-rs-sse
[ServiceLoader]: https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html
[TestingEqualsHashcode]: https://joht.github.io/johtizen/testing/2020/03/08/test-all-equal-and-hashcode-methods.html
[yuicompressor-maven-plugin]: http://davidb.github.io/yuicompressor-maven-plugin/