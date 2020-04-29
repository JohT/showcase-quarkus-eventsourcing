package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon;

import static io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database.DatabaseCatalogColumn.columnIn;
import static io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database.DatabaseCatalogTable.schemaAndTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;
import javax.validation.ValidatorFactory;

import org.axonframework.common.Priority;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.eventhandling.tokenstore.jdbc.JdbcTokenStore;
import org.axonframework.eventhandling.tokenstore.jdbc.TokenSchema;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jdbc.EventSchema;
import org.axonframework.eventsourcing.eventstore.jdbc.JdbcEventStorageEngine;
import org.axonframework.messaging.annotation.MultiParameterResolverFactory;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.serialization.RevisionResolver;
import org.axonframework.serialization.Serializer;

import io.github.joht.showcase.quarkuseventsourcing.messaging.command.axon.CommandEmitterAdapter;
import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.CommandEmitterService;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database.DatabaseCatalogQuery;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database.DatabaseCatalogTable;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database.postgresql.PostgreSqlBytesToJsonbConverter;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database.postgresql.PostgreSqlJsonbToBytesConverter;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi.AxonComponentDiscovery;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi.AxonComponentDiscoveryContext;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.inject.cdi.CdiParameterResolverFactory;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.serializer.jsonb.axon.JsonbSerializer;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.transaction.jta.JtaTransactionManager;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.upcaster.AnnotationEventRevisionResolver;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon.QueryReplayAdapter;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon.QuerySubmitterAdapter;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.axon.QueryUpdateEmitterAdapter;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryModelProjection;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryProcessor;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryProjectionManagementService;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QuerySubmitterService;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryUpdateEmitterService;

/**
 * Axon Configuration.
 * 
 * @author JohT
 */
@Typed()
@ApplicationScoped
public class AxonConfiguration {

    private static final Logger LOGGER = Logger.getLogger(AxonConfiguration.class.getName());

    private static final String DATABASE_POSTGRESQL_JSON_BINARY_TYPE = "jsonb";
    private static final String DATABASE_SCHEMA_COMMAND_SIDE = "axon_on_microprofile_tryout";
    private static final String DATABASE_SCHEMA_QUERY_SIDE = "axon_on_microprofile_query_tryout";
    private static final String DATABASE_TABLE_SNAPSHOTS = "snapshotevententry";
    private static final String DATABASE_TABLE_DOMAIN_EVENTS = "domainevententry";
    private static final String DATABASE_TABLE_TOKEN = "tokenentry";

    @Inject
    @Named("messaging")
    DataSource dataSource;

    @Inject
    ValidatorFactory validatorFactory;

    @Inject
    UserTransaction userTransaction;

    @Inject
    TransactionSynchronizationRegistry transactionRegistry;

    @Inject
    AxonComponentDiscovery componentDiscovery;

    private DatabaseCatalogQuery databaseCatalogQuery;

    private Configuration configuration;

    @PostConstruct
    protected void startUp() {
        Configurer configurer = DefaultConfigurer.defaultConfiguration();
        configureEventProcessing(configurer);
        addDiscoveredComponentsTo(configurer);
        configuration = configurer
                .registerComponent(RevisionResolver.class, config -> new AnnotationEventRevisionResolver())
                .configureSerializer(this::flexibleSerializer)
                .configureMessageSerializer(this::messageSerializer)
                .configureEmbeddedEventStore(this::eventStorageEngine)
                .configureTransactionManager(c -> transactionManager())
                .buildConfiguration();
        configurer.registerComponent(ParameterResolverFactory.class, this::parameterResolvers);
        enableBeanValidationForCommandMessages();
        configuration.start();
    }

    /**
     * Registers <code>CdiParameterResolverFactory</code> on top of the default {@link ParameterResolverFactory}. <br>
     * For best compatibility, this is done without using ServiceLoader, test dependencies and annotations.
     * <p>
     * Note: ClasspathParameterResolverFactory doesn't see "CdiParameterResolverFactory" <br>
     * when running "mvn compile quarkus:dev" (quarkus in development mode). <br>
     * This method assures, that "CdiParameterResolverFactory" gets registered, regardless of which classloader is used.
     * <p>
     * Note: "FixtureResourceParameterResolverFactory" gets registered, integration tests and axon aggregate tests run inside the same
     * module and therefore share the same test dependencies. To remove them for the the "real" configuration, this method also removes test
     * parameter resolvers.
     * <p>
     * Note: {@link MultiParameterResolverFactory#ordered(List)} is not used, since it uses the {@link Priority} annotation, which seems to
     * have a problem running in quarkus native mode.
     * 
     * @param config {@link Configuration}
     * @return {@link ParameterResolverFactory}
     */
    private ParameterResolverFactory parameterResolvers(Configuration config) {
        Configuration defaultConfig = DefaultConfigurer.defaultConfiguration().buildConfiguration();
        List<ParameterResolverFactory> factories = allFactoriesOf(defaultConfig.getComponent(ParameterResolverFactory.class));
        factories.add(new CdiParameterResolverFactory()); // add with lowest priority (without using an annotation)
        factories.removeIf(factory -> factory.getClass().getName().contains(".test.")); // remove test factories
        return new MultiParameterResolverFactory(factories);
    }

    private static List<ParameterResolverFactory> allFactoriesOf(ParameterResolverFactory parameterResolverFactory) {
        return new ArrayList<>(MultiParameterResolverFactory.ordered(parameterResolverFactory).getDelegates());
    }

    @PreDestroy
    protected void shutdown() throws InterruptedException, ExecutionException {
        configuration.shutdown();
    }

    @Produces
    @ApplicationScoped
    public CommandEmitterService getCommandEmitterService() {
        return new CommandEmitterAdapter(configuration.commandGateway());
    }

    @Produces
    @ApplicationScoped
    public QuerySubmitterService getQuerySubmitterService() {
        return new QuerySubmitterAdapter(configuration.queryGateway());
    }

    @Produces
    @ApplicationScoped
    public QueryUpdateEmitterService getQueryUpdateEmitterService() {
        return new QueryUpdateEmitterAdapter(configuration.queryUpdateEmitter());
    }

    @Produces
    @ApplicationScoped
    public QueryProjectionManagementService getQueryReplayService() {
        return new QueryReplayAdapter(configuration.eventProcessingConfiguration());
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private TransactionManager transactionManager() {
        return JtaTransactionManager.using(userTransaction, transactionRegistry);
    }

    private Serializer flexibleSerializer(Configuration config) {
        return postgreSqlJsonbTypeConverter(JsonbSerializer.fieldAccess())
                .revisionResolver(config.getComponent(RevisionResolver.class))
                .build();
    }

    private Serializer messageSerializer(Configuration config) {
        return postgreSqlJsonbTypeConverter(JsonbSerializer.defaultSerializer())
                .revisionResolver(config.getComponent(RevisionResolver.class))
                .build();
    }

    private JsonbSerializer.Builder postgreSqlJsonbTypeConverter(JsonbSerializer.Builder builder) {
        if (isPostgreSqlDatabaseUsingJsonbForBinaryDataOnCommandSide()) {
            builder.addContentTypeConverter(PostgreSqlBytesToJsonbConverter.class);
            builder.addContentTypeConverter(PostgreSqlJsonbToBytesConverter.class);
        }
        return builder;
    }

    private EventStorageEngine eventStorageEngine(Configuration config) {
        EventSchema schema = EventSchema.builder()
                .eventTable(DATABASE_SCHEMA_COMMAND_SIDE + "." + DATABASE_TABLE_DOMAIN_EVENTS)
                .snapshotTable(DATABASE_SCHEMA_COMMAND_SIDE + "." + DATABASE_TABLE_SNAPSHOTS)
                .build();
        return JdbcEventStorageEngine.builder()
                .schema(schema)
                .eventSerializer(config.eventSerializer())
                .snapshotSerializer(config.serializer())
                .connectionProvider(this::getConnection)
                .dataType(isPostgreSqlDatabaseUsingJsonbForBinaryDataOnCommandSide() ? String.class : byte[].class)
                .transactionManager(transactionManager())
                .upcasterChain(config.upcasterChain())
                .build();
    }

    private Configurer addDiscoveredComponentsTo(Configurer configurer) {
        AxonComponentDiscoveryContext context = AxonComponentDiscoveryContext.builder()
                .configurer(configurer)
                .onAggregateConfiguration(AxonAggregateConfiguration::update)
                .onDiscoveredType(ProcessingGroup.class, type -> assignAnnotatedProcessingGroup(configurer, type))
                .build();
        componentDiscovery.addDiscoveredComponentsTo(context);
        return configurer;
    }

    // Note Query-Side
    private void assignAnnotatedProcessingGroup(Configurer configurer, Class<?> type) {
        QueryModelProjection.ProcessorAssignment.forType(type, assignUsing(configurer.eventProcessing()));
    }

    // Note Query-Side
    private static Consumer<QueryModelProjection> assignUsing(EventProcessingConfigurer eventProcessing) {
        return assignment -> eventProcessing.assignProcessingGroup(logged(assignment).processingGroup(), assignment.processor().getName());
    }

    private static QueryModelProjection logged(QueryModelProjection assignment) {
        LOGGER.fine(
                () -> String.format("Processor %s assigned to group %s", assignment.processor().getName(), assignment.processingGroup()));
        return assignment;
    }

    // Note Query-Side
    private Configurer configureEventProcessing(Configurer configurer) {
        EventProcessingConfigurer eventProcessing = configurer.eventProcessing();
        // Note: The main projection (event handler) is notified synchronously
        // within the publishing thread
        // and within the publishing transaction. If something goes wrong, everything
        // inside the transaction including the event is rolled back.
        eventProcessing.registerSubscribingEventProcessor(QueryProcessor.SUBSCRIBING.getName());

        // Note: Other projections (e.g. for materialized reports) are notified
        // asynchronously. They shouldn't influence the main thread and aren't needed to
        // react immediately ("eventual consistency").
        eventProcessing.registerTrackingEventProcessorConfiguration(this::trackingEventProcessorConfig);
        eventProcessing.registerTrackingEventProcessor(QueryProcessor.TRACKING.getName());
        eventProcessing.registerTokenStore(this::jdbcTokenStore);
        eventProcessing.assignProcessingGroup(this::logDefaultAssignment);
        return configurer;
    }

    private String logDefaultAssignment(String processingGroup) {
        LOGGER.fine(() -> "Default assignment for processingGroup <" + processingGroup + ">");
        return ((processingGroup != null) && !processingGroup.trim().isEmpty()) ? processingGroup
                : QueryProcessor.TRACKING.getName();
    }

    // Note Query-Side
    private JdbcTokenStore jdbcTokenStore(Configuration config) {
        TokenSchema schema = TokenSchema.builder().setTokenTable(DATABASE_SCHEMA_QUERY_SIDE + "." + DATABASE_TABLE_TOKEN).build();
        return JdbcTokenStore.builder()
                .connectionProvider(this::getConnection)
                .contentType(isPostgreSqlDatabaseUsingJsonbForBinaryDataOnQuerySide() ? postgreSqlObjectType() : byte[].class)
                .serializer(config.serializer())
                .schema(schema).build();
    }

    // Note Query-Side
    private TrackingEventProcessorConfiguration trackingEventProcessorConfig(Configuration config) {
        // Note: CDI @RequestScoped and other scopes are not available for manually started threads.
        // Therefore, only @ApplicationScoped and @Dependent can be used for tracking event handler.
        // If any other scope is needed, have a look at "deltaspike-cdictrl-api".
        return TrackingEventProcessorConfiguration.forSingleThreadedProcessing();
    }

    private boolean isPostgreSqlDatabaseUsingJsonbForBinaryDataOnCommandSide() {
        DatabaseCatalogQuery catalogQuery = getDatabaseCatalogQuery();
        DatabaseCatalogTable domainEventTable = schemaAndTable(DATABASE_SCHEMA_COMMAND_SIDE, DATABASE_TABLE_DOMAIN_EVENTS);
        String columnType = catalogQuery.getColumnType(columnIn(domainEventTable, "payload"));
        return DATABASE_POSTGRESQL_JSON_BINARY_TYPE.equals(columnType);
    }

    private boolean isPostgreSqlDatabaseUsingJsonbForBinaryDataOnQuerySide() {
        DatabaseCatalogQuery catalogQuery = getDatabaseCatalogQuery();
        DatabaseCatalogTable tokenTable = schemaAndTable(DATABASE_SCHEMA_QUERY_SIDE, DATABASE_TABLE_TOKEN);
        String columnType = catalogQuery.getColumnType(columnIn(tokenTable, "token"));
        return DATABASE_POSTGRESQL_JSON_BINARY_TYPE.equals(columnType);
    }

    private synchronized DatabaseCatalogQuery getDatabaseCatalogQuery() {
        if (databaseCatalogQuery == null) {
            try (Connection connection = getConnection()) {
                this.databaseCatalogQuery = DatabaseCatalogQuery.forConnection(connection)
                        .defaultSchema(DATABASE_SCHEMA_COMMAND_SIDE)
                        .tablename(DATABASE_TABLE_DOMAIN_EVENTS)
                        .tablename(DATABASE_TABLE_SNAPSHOTS)
                        .tablename(DATABASE_SCHEMA_QUERY_SIDE + "." + DATABASE_TABLE_TOKEN)
                        .triggerQuery();
            } catch (SQLException e) {
                throw new IllegalStateException("error during database catalog query", e);
            }
        }
        return this.databaseCatalogQuery;
    }

    private Class<?> postgreSqlObjectType() {
        String className = "org.postgresql.util.PGobject";
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("Cannot load PostgreSql specific data type class " + className, e);
        }
    }

    // Note Command-Side
    private void enableBeanValidationForCommandMessages() {
        configuration.commandBus().registerDispatchInterceptor(new BeanValidationInterceptor<>(validatorFactory));
    }
}