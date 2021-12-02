# Showcase for Event Sourcing on Quarkus using AxonFramework and MicroProfile

## Getting started
* Clone or download this repository. 
* Open a terminal/command window.
* Locate the h2 jar in your maven repository, e.g. ```repository/com/h2database/h2/1.4.197/```.
* Start the h2 database server using ```java -cp h2-1.4.197.jar org.h2.tools.Server -tcpAllowOthers```.
* Open another terminal/command window. Don't close the one where the h2 server is running.
* Open the directory where this README.md is located.
* Run the application by using the following command: ```mvn compile quarkus:dev```.
* Open the UI [http://localhost:8080](http://localhost:8080)
* (Optional) Use the postman collection "showcase-quarkus-eventsourcing.postman_collection.json" for service call examples.
* (Optional) Use the unit tests inside the service package to replay nicknames, create new ones or create further accounts.

## Native image
* Build a native image with ```mvn package -Pnative```.
  Details see [Building a native executable][QuarkusNativeExecutable].
* The substrate runner can be used to collect configuration data for the native image:

```shell
$GRAALVM_HOME/bin/java -agentlib:native-image-agent=config-output-dir=native-image,caller-filter-file=native-image-caller-filter-rules.json -jar ./target/quarkus-app/quarkus-run.jar
```
  This is helpful to get a hint on how to configure ```reflection-config.json``` and ```resources-config.json```
  These files were created that way.
  To automate this and to get a more complete set of entries, a build step could be a solution.
  Ideally, there would be an axon-extension for Quarkus that manages these settings.
  
* The substrate runner can also be used to generate a trace of all reflection calls:

```shell
$GRAALVM_HOME/bin/java -agentlib:native-image-agent=trace-output=native-image/trace-output.json,caller-filter-file=native-image-caller-filter-rules.json -jar ./target/quarkus-app/quarkus-run.jar
```

* More informations abount the `native-image-agent` can be found here:
[Assisted Configuration with Tracing Agent][NativeImageTracingAgent]

## Features
* MicroProfile Standard
* "Reactive" example using server sent events (tested with safari & chrome browser) and axon subscription query
* Replay example. Use REST DELETE ```/nicknames/projection```
* Contains an axon upcaster example
* Works with H2 and PostgreSql. Just switch the regarding comments in ```application.properties``` and ```persistence.xml```.
* Uses flyway for database schema creation and migration. It is configured to work with H2 and PostgreSql.
* Uses [JSON Binding][JSONBinding] to serialize JSON (MicroProfile Standard)
* Uses meta-annotations to decouple [AxonFramework][AxonFramework] from the message api and business code.
* Full-Stack Build configuration including JavaScript Unit Tests with Jasmine, JavaScript minify, ... 
* Continuous Integration using [GitHub Actions][GitHubActions] for a fully automated build including Java and JavaScript Unit Tests, Web Packaging, native image and integration-tests.

## Notes
* Code comments containing the marker ```Note:``` describes thoughts, background information, documented decisions and hints to problems. 
* ```ArchitectureRulesTest``` defines rules to assure low coupling between the business core, axon and microprofile features.
* These rules might seem a bit extreme. Some may even find them to be impractical. After all, this examples shows that it can be done.
* This is just a simple show case, not an full application. 

## What is [AxonFramework][AxonFramework]?

> Open source framework for event-driven microservices and domain-driven design

For more details please visit [AxonFramework][AxonFramework].

## What is [MicroProfile][MicroProfile]?

> The MicroProfile® project is aimed at optimizing Enterprise Java for the microservices architecture.

For more details please visit [MicroProfile][MicroProfile]

## What is [Quarkus][Quarkus]?

> A Kubernetes Native Java stack tailored for OpenJDK HotSpot and GraalVM, crafted from the best of breed Java libraries and standards.

For more details please visit [Quarkus][Quarkus].

## References

* [Assisted Configuration with Tracing Agent][NativeImageTracingAgent]
* [AxonFramework][AxonFramework]
* [Building a native executable][QuarkusNativeExecutable]
* [Eclipse MicroProfile][MicroProfile]
* [GitHub Actions][GitHubActions]
* [Jakarta JSON Binding][JSONBinding]
* [Quarkus][Quarkus]

[AxonFramework]: https://axoniq.io/product-overview/axon-framework
[GitHubActions]: https://docs.github.com/en/actions
[MicroProfile]: https://projects.eclipse.org/projects/technology.microprofile
[Quarkus]: https://quarkus.io
[QuarkusNativeExecutable]: https://quarkus.io/guides/building-native-image-guide
[NativeImageTracingAgent]: https://github.com/oracle/graal/blob/master/docs/reference-manual/native-image/Agent.md
[JSONBinding]: https://jakarta.ee/specifications/jsonb/2.0/jakarta-jsonb-spec-2.0.html