# Showcase for eventsourcing on quarkus using axon and standard microprofile

## Getting started
* Clone or download this repositor.y 
* Open a terminal/command window.
* Locate the h2 jar in your maven repository, e.g. ```repository/com/h2database/h2/1.4.197/```.
* Start the h2 database server using ```java -cp h2-1.4.197.jar org.h2.tools.Server -tcpAllowOthers```.
* Open another terminal/command window. Don't close the one where the h2 server is running.
* Open the directory where this README.md is located.
* Run the application by using the following command: ```mvn compile quarkus:dev```.
* Open [events.html](http://localhost:8080/events.html) in a browser to see server sent events and axon subscription query in action.
* Use the postman collection "showcase-quarkus-eventsourcing.postman_collection.json" for service call examples.
* Use the unit tests inside the service package to replay nicknames, create new ones or create further accounts.
* Build a native image with ```mvn package -Pnative```. A started database server is needed (see above).
  Details see [Building a native executable](https://quarkus.io/guides/building-native-image-guide)

## Features
* "Reactive" example using server sent events (may not work for IE and Edge) and axon subscription query
* Replay example. Use REST DELETE ```/nicknames/projection```
* Contains an axon upcaster example
* Works with H2 and PostgreSql. Just switch the regarding comments in ```application.properties``` and ```persistence.xml```.
* Uses flyway for database schema migration. It is configured to work with H2 and PostgreSql.
* Uses JSON-B to stay inside the microprofile standard
* Uses meta-annotations to fully decouple axon from the message api and business code.

## Notes
* Code comments containing the marker ```Note:``` describes thoughts, background information, documented decisions and hints to problems. 
* ```ArchitectureRulesTest``` defines rules to assure low coupling between the business core, axon and the microprofile features.
* These rules might seem a bit extreme. Some may even find them to be impractical. After all, this examples shows that it can be done.
* This is just a simple show case, not an fully thought through application. 

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
