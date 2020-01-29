package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database;

import static io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database.DatabaseCatalogColumn.columnIn;
import static io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database.DatabaseCatalogTable.schemaAndTable;

import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.DisabledOnNativeImage;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@DisabledOnNativeImage
public class DatabaseCatalogQueryIntegrationTest {

    @Inject
    @Named("messaging")
    DataSource dataSource;

    /**
     * Assures, that all binary data columns for the messaging/command-side use the same type.
     * <p>
     * This tests also shows how to use {@link DatabaseCatalogQuery}.
     * 
     * @throws SQLException
     */
    @Test
    public void binaryColumnsShouldAllHaveTheSameType() throws SQLException {
        String schema = "axon_on_microprofile_tryout";

        try (Connection connection = dataSource.getConnection()) {
            DatabaseCatalogQuery catalogQuery = DatabaseCatalogQuery
                    .forConnection(connection)
                    .defaultSchema(schema)
                    .tablename("domainevententry")
                    .tablename("snapshotevententry")
                    .tablename("sagaentry");
            String binaryType = catalogQuery.getColumnType(columnIn(schemaAndTable(schema, "domainevententry"), "metadata"));
            assertEquals(binaryType, catalogQuery.getColumnType(columnIn(schemaAndTable(schema, "domainevententry"), "payload")));
            assertEquals(binaryType, catalogQuery.getColumnType(columnIn(schemaAndTable(schema, "snapshotevententry"), "metadata")));
            assertEquals(binaryType, catalogQuery.getColumnType(columnIn(schemaAndTable(schema, "snapshotevententry"), "payload")));
            assertEquals(binaryType, catalogQuery.getColumnType(columnIn(schemaAndTable(schema, "sagaentry"), "serializedsaga")));
        }
    }
}