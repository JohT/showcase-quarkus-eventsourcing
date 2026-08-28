# Plan: Axon 5 Quarkus Showcase — New Root Module

**What:** New Maven module `showcase-quarkus-eventsourcing-axon-5/` alongside the existing Axon 4 module. Same domain (account creation, nickname change, SSE stream), same hexagonal architecture, custom CDI/JTA glue for Quarkus, Axon 5 API throughout. Java 21, Quarkus 3.x LTS, no Axon Server.

---

## Phase 1 — Maven Module Scaffold
1. Create `showcase-quarkus-eventsourcing-axon-5/pom.xml`: Java 21, `axon-framework-bom:5.x`, modules `axon-common`, `axon-eventsourcing`, `axon-messaging`, `axon-modelling`, `axon-test`; Quarkus BOM (3.x LTS); replace JDBC deps with `quarkus-hibernate-orm` (now used for both event store and projections)

## Phase 2 — Message Layer
2. Copy commands, events, queries (POJOs). Update boundary meta-annotations: `@CommandTargetAggregateIdentifier` wraps `@TargetEntityId` (new package), `@EventRevision` wraps `@Event(version=...)`. Add TODO on upcasters.

## Phase 3 — Domain Entity
3. Create `AccountEntity` (was `AccountAggregate`):
   - Add `@EntityCreator` on no-arg constructor
   - Remove `@CommandModelAggregateIdentifier` on field; set `accountId` in `@CommandModelEventSourcingHandler`
   - Static `createWith()` with `AggregateEventEmitterService` parameter (kept for hexagonal isolation)
   - `@CommandModelCommandHandler`/`@CommandModelEventSourcingHandler` meta-annotations updated to new Axon 5 packages

## Phase 4 — Axon 5 CDI Infrastructure (hardest part, sequential)
4. Update boundary meta-annotations in `messaging/*/boundary/` to Axon 5 packages
5. `AggregateEventEmitterAdapter` — wraps `EventAppender.append()` instead of `AggregateLifecycle.apply()`; designed per-invocation via parameter resolver
6. `CdiParameterResolverFactory` — resolves CDI beans and `AggregateEventEmitterService` as method parameters (verify Axon 5 `ParameterResolverFactory` API)
7. `CdiResourceInjector` — CDI injection into Axon-managed beans (verify API)
8. `AxonComponentDiscovery` — discover handlers via CDI BeanManager; adapt to `ApplicationConfigurer` registration API
9. `JtaTransactionManager` — wrap Narayana `UserTransaction` (verify Axon 5 `TransactionManager` interface)
10. `JsonbConverter` in `conversion/jsonb/` — implement Axon 5 Converter API with JSON-B; replaces `JsonbSerializer`
11. `AxonConfiguration` (`@ApplicationScoped`, `@PostConstruct`):
    - `ApplicationConfigurer` / `EventSourcingConfigurer`
    - `AggregateBasedJpaEventStorageEngine` with Quarkus `EntityManagerFactory`
    - `EventSourcedEntityModule.autodetected(String.class, AccountEntity.class)`
    - CDI `@Produces` for `CommandEmitterService`, `QuerySubmitterService`, etc.

## Phase 5 — Query Model
12. Copy projections and repositories; update event handler annotation imports only

## Phase 6 — REST Service Layer
13. Copy REST resources unchanged; add TODO on projection replay (not available in Axon 5.0)

## Phase 7 — Configuration and Database Migrations
14. `application.properties` — two persistence units (event store + query), Flyway for query side and for new `aggregate_event_entry` schema (Axon 5 renamed from `domain_event_entry`; column names changed)

## Phase 8 — Tests
15. `AccountEntityTest`:
   - Static `AccountEntityTestConfiguration` helper builds the test `ApplicationConfigurer`
   - `AxonTestFixture.with(configurer, c -> c.disableAxonServer())`
   - New API: `fixture.given().noPriorActivity().when().command(cmd).then().success().events(...)`
   - `@AfterEach fixture.stop()`
16. `ArchitectureRulesTest` — update package name allowlists
17. Infrastructure unit tests — updated for Axon 5 APIs

## Phase 9 — Native Image
18. `reflection-config.json` + `resources-config.json` with Axon 5 class names; `native-image-agent` Maven profile

## Phase 10 — Frontend, Docker, and Test Completeness

19. **Static frontend (copy from Axon 4, no changes)**:
    - `src/main/javascript/` → `account.js` (410 lines), `event.js`, `startup.js`, `polyfills/` (8 files)
    - `src/main/resources/META-INF/resources/` → `index.html`, `images/`, `styles/`
    - These are framework-agnostic; Axon 4 and Axon 5 modules share identical UI.

20. **Jasmine test runner (copy verbatim)**:
    - `src/test/resources/SpecRunner.html` — browser-based Jasmine runner
    - `src/test/resources/jasmine/` — jasmine.js/css, boot.js, jasmine-html.js, favicon
    - `src/test/javascript/AccountSpec.js` — JS unit tests

21. **Docker files** (`src/main/docker/`):
    - `Dockerfile.jvm` — `ubi9/openjdk-21-runtime:1.21`, copies `target/quarkus-app/` fast-jar layout
    - `Dockerfile.native` — `ubi9/ubi-minimal:9.4`, copies `target/*-runner`

22. **package-info.java** — 13 files across `message/`, `service/`, `query/`, `domain/`, `messaging/` subpackages. Copy from Axon 4; `messaging/infrastructure/axon/package-info.java` updated to describe Axon 5 specifics (EventSourcingConfigurer, InMemoryEventStorageEngine).

23. **Test files — domain/message/adapter (all 14 files)**:
    - Pure-domain tests (12 files): copy as-is from Axon 4 (no Axon imports)
    - `EventPublishingAdapterTest.java`: Axon 5 import — `org.axonframework.messaging.eventhandling.gateway.EventGateway`
    - `QueryReplayAdapterTest.java`: full rewrite — Axon 5 `QueryReplayAdapter` is a stub; tests `replayDoesNotThrow()` and `getStatusReturnsEmptyStatus()`

24. **Mockito dependency** (pom.xml): `mockito-junit-jupiter` (version from Quarkus 3.9.5 BOM)

25. **PostgreSQL query-side support**:
    - `%postgres.*` profile block in `application.properties` (db-kind, jdbc.url, credentials, flyway location, dialect)
    - `src/main/resources/db/query/postgresql/V1.0.0__create_query_tables.sql` — schema + `account` + `nickname` tables

---

## OpenRewrite Automated Migration Reference

The [AxonIQ OpenRewrite migration tool](https://docs.axoniq.io/axon-framework-reference/5.1/migration/openrewrite-code-conversion/) provides an automated starting point for Axon 4→5 migration. Run against the existing Axon 4 module (`showcase-quarkus-eventsourcing/`) as a comparison reference:

```bash
cd showcase-quarkus-eventsourcing
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.axonframework:axon-migration:5.1.1 \
  -Drewrite.activeRecipes=org.axonframework.migration.UpgradeAxon4ToAxon5
```

**Expected result:** The project will **not compile** after the run — this is intentional. The tool handles package renames, annotation rewrites (`@Aggregate`→`@EventSourced`, `@AggregateLifecycle.apply()`→`EventAppender#append()`, `AggregateTestFixture`→`AxonTestFixture`, etc.) and leaves `// TODO(axon4to5): <action>` markers for changes that require human judgment. Collect remaining work with `grep -r "TODO(axon4to5)" src/`.

The `showcase-quarkus-eventsourcing-axon-5/` module represents the completed, manually-refined migration that goes beyond what OpenRewrite automates (CDI/Quarkus integration, Axon 5 JPA event store, hexagonal adapter wiring).

---

## Relevant Reference Files

- [AxonConfiguration.java](showcase-quarkus-eventsourcing/src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/AxonConfiguration.java) — main config reference
- [CDI integration](showcase-quarkus-eventsourcing/src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/inject/cdi) — CDI resolver and component discovery reference
- [JtaTransactionManager.java](showcase-quarkus-eventsourcing/src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/transaction/jta/JtaTransactionManager.java) — JTA reference
- [JSON-B Serializer](showcase-quarkus-eventsourcing/src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/infrastructure/axon/serializer/jsonb) — serializer → converter reference
- [AccountAggregate.java](showcase-quarkus-eventsourcing/src/main/java/io/github/joht/showcase/quarkuseventsourcing/domain/model/account/AccountAggregate.java) — domain entity reference
- [Boundary interfaces](showcase-quarkus-eventsourcing/src/main/java/io/github/joht/showcase/quarkuseventsourcing/messaging/command/boundary) — hexagonal adapter reference
- [AccountAggregateTest.java](showcase-quarkus-eventsourcing/src/test/java/io/github/joht/showcase/quarkuseventsourcing/domain/model/account/AccountAggregateTest.java) — test reference
- [application.properties](showcase-quarkus-eventsourcing/src/main/resources/application.properties) — Quarkus config reference
- [pom.xml](showcase-quarkus-eventsourcing/pom.xml) — Maven structure reference

---

## Verification Checklist

1. `mvn compile` — zero errors
2. `mvn test` — `AccountEntityTest`, `ArchitectureRulesTest`, infrastructure unit tests pass
3. `mvn quarkus:dev` — H2 starts, all REST endpoints respond
4. POST → GET nickname → PUT nickname → GET SSE stream all work end-to-end
5. `mvn package -Pnative` — native binary builds (best effort)

---

## Honest Risk Assessment

- **Phase 4 is the hard unknown.** Axon 5 has no Quarkus extension. The `ParameterResolverFactory`, `ResourceInjector`, and `TransactionManager` interfaces need to be verified against Axon 5 source before writing code. The `AggregateEventEmitterAdapter` pattern (per-invocation `EventAppender` via parameter resolver) is novel and needs design validation.

- **JSON-B Converter**: The Axon 5 Converter API is a full replacement for the Serializer. The existing `JsonbSerializer` cannot be adapted — it must be rewritten against the new interface.

- **Event store schema**: The `aggregate_event_entry` table (Axon 5) is structurally different from `domain_event_entry` (Axon 4). Flyway migrations must be written fresh.

- **Missing features**: Upcasters, snapshots, projection replay — all marked TODO. The existing `NicknameChangedEventV1Upcaster` will not function; the `/nicknames/projection` DELETE endpoint will be a stub.

---

## API Cheat Sheet: Axon 4 → 5

| Axon 4 | Axon 5 |
|---|---|
| `@Aggregate` / `@AggregateRoot` | `@EventSourcedEntity` |
| `@AggregateIdentifier` on field | Removed; set in `@EventSourcingHandler` |
| Constructor `@CommandHandler` | Static method + `EventAppender` parameter |
| `AggregateLifecycle.apply(event)` | `EventAppender.append(event)` |
| `@TargetAggregateIdentifier` | `@TargetEntityId` |
| `@Revision("1.0")` | `@Event(version = "1.0")` |
| `AggregateConfigurer` | `EventSourcedEntityModule.autodetected(String.class, X.class)` |
| `JdbcEventStorageEngine` | `AggregateBasedJpaEventStorageEngine` |
| `DefaultConfigurer.defaultConfiguration()` | `ApplicationConfigurer` / `EventSourcingConfigurer` |
| `org.axonframework.serialization.*` | `org.axonframework.conversion.*` |
| `MetaData` | `Metadata` (lowercase d) |
| `AggregateTestFixture<>(X.class)` | `AxonTestFixture.with(configurer, c -> c.disableAxonServer())` |
| `fixture.givenNoPriorActivity().when(cmd).expectEvents(...)` | `fixture.given().noPriorActivity().when().command(cmd).then().success().events(...)` |
| `org.axonframework.commandhandling.*` | `org.axonframework.messaging.commandhandling.*` |
| `org.axonframework.eventhandling.*` | `org.axonframework.messaging.eventhandling.*` |
| `org.axonframework.queryhandling.*` | `org.axonframework.messaging.queryhandling.*` |
| `org.axonframework.modelling.command.*` | `org.axonframework.modelling.entity.*` |
| `org.axonframework.config.*` | `org.axonframework.common.configuration.*` |
| `axon-bom:4.x` | `axon-framework-bom:5.x` |
