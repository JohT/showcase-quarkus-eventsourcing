# Showcase for eventsourcing on quarkus using axon and standard microprofile

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
  Details see [Building a native executable](https://quarkus.io/guides/building-native-image-guide)
* The substrate runner can be used to collect configuration data for the native image:
```
$GRAALVM_HOME/bin/java -agentlib:native-image-agent=config-output-dir=native-image,caller-filter-file=native-image-caller-filter-rules.json -jar ./target/showcase-quarkus-eventsourcing-1.0-SNAPSHOT-runner.jar
```
  This is helpful to get a hint on how to configure ```reflection-config.json``` and ```resources-config.json```
  These files were configured that way.
  To automate this and to get a more complete set of entries, a build step may be a solution.
  Ideally, there would be a axon-extension for quarkus that manages these settings.
* The substrate runner can also be used to generate a trace of all reflection calls:
```
$GRAALVM_HOME/bin/java -agentlib:native-image-agent=trace-output=native-image/trace-output.json,caller-filter-file=native-image-caller-filter-rules.json -jar ./target/showcase-quarkus-eventsourcing-1.0-SNAPSHOT-runner.jar
``` 
* More informations abount the `native-image-agent` can be found here:
[CONFIGURE.md](https://github.com/oracle/graal/blob/master/substratevm/CONFIGURE.md)

## Features
* "Reactive" example using server sent events (tested with safari browser) and axon subscription query
* Replay example. Use REST DELETE ```/nicknames/projection```
* Contains an axon upcaster example
* Works with H2 and PostgreSql. Just switch the regarding comments in ```application.properties``` and ```persistence.xml```.
* Uses flyway for database schema migration. It is configured to work with H2 and PostgreSql.
* Uses JSON-B to stay inside the microprofile standard
* Uses meta-annotations to fully decouple axon from the message api and business code.
* Full-Stack Build configuration including JavaScript Unit Tests with Jasmine, JavaScript minify, ... 

## Notes
* Code comments containing the marker ```Note:``` describes thoughts, background information, documented decisions and hints to problems. 
* ```ArchitectureRulesTest``` defines rules to assure low coupling between the business core, axon and the microprofile features.
* These rules might seem a bit extreme. Some may even find them to be impractical. After all, this examples shows that it can be done.
* This is just a simple show case, not an full application. 

## What is axon?

"Axon Framework is a framework for building evolutionary, event-driven microservice systems,
 based on the principles of Domain Driven Design, Command-Query Responsibility Segregation (CQRS) and Event Sourcing." 
<br>For more details please visit [Axon](http://axoniq.io).

## What is microprofile?

"The Eclipse MicroProfile project is aimed at optimizing Enterprise Java for the microservices architecture."
<br>For more details please visit [Eclipse MicroProfile]((https://projects.eclipse.org/projects/technology.microprofile)

## What is quarkus?

"Supersonic Subatomic Java"
"A Kubernetes Native Java stack tailored for GraalVM & OpenJDK HotSpot, crafted from the best of breed Java libraries and standards."
<br>For more details please visit [Quarkus](https://quarkus.io)
