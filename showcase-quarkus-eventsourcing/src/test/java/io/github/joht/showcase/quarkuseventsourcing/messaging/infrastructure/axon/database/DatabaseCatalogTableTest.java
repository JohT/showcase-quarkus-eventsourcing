package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database.DatabaseCatalogTable;

class DatabaseCatalogTableTest {

    /*
     * class under test.
     */
    private DatabaseCatalogTable table;

    @Test
    void shouldExtractSchemaFromFullQualifiedTableName() {
        table = DatabaseCatalogTable.fullQualified("schema.tablename");
        assertEquals("schema", table.getSchema());
    }

    @Test
    void shouldExtractTablenameFromFullQualifiedTableName() {
        table = DatabaseCatalogTable.fullQualified("schema.tablename");
        assertEquals("tablename", table.getName());
    }

    @Test
    void shouldUseTablenameDirectlyFromFullQualifiedTableNameWithoutSchema() {
        table = DatabaseCatalogTable.fullQualified("directfulltablename");
        assertEquals("directfulltablename", table.getName());
    }

    @Test
    void noSchemaCreatorShouldLeadToAnEmptySchema() {
        table = DatabaseCatalogTable.noSchema("tablename");
        assertEquals("", table.getSchema());
    }

    @Test
    void schemaAndTableCreatorShouldUseSchemaDirectly() {
        String schemaName = "separateschema";
        table = DatabaseCatalogTable.schemaAndTable(schemaName, "something");
        assertEquals(schemaName, table.getSchema());
    }

    @Test
    void schemaAndTableCreatorShouldUseTablenameDirectly() {
        String tableName = "separatetablename";
        table = DatabaseCatalogTable.schemaAndTable("something", tableName);
        assertEquals(tableName, table.getName());
    }

    @Test
    void shouldTakeDefaultSchemaIfItIsValidAndThePreviousSchemaWasUndefined() {
        String tableName = "something";
        String defaultSchema = "default_schema";
        table = DatabaseCatalogTable.schemaAndTable("", tableName).useDefaultSchema(defaultSchema);
        assertEquals(tableName, table.getName());
        assertEquals(defaultSchema, table.getSchema());
    }

    @Test
    void shouldDoNothingIfTheDefaultSchemaItselfIsUndefined() {
        String tableName = "something";
        table = DatabaseCatalogTable.schemaAndTable("", tableName);
        assertSame(table, table.useDefaultSchema(""));
    }

    @Test
    void theDefaultSchemaShouldNotOverwriteAnAlreadySetSchema() {
        String tableName = "something";
        table = DatabaseCatalogTable.schemaAndTable("alread_set_schema", tableName);
        assertSame(table, table.useDefaultSchema("default_schema_to_be_ignored"));
    }

    @Test
    void matchesMapOnMatchingSchemaAndTable() {
        String schemaName = "mapschema";
        String tableName = "maptable";
        table = DatabaseCatalogTable.schemaAndTable(schemaName, tableName);
        assertTrue(table.matchesMap(mapWithSchemaAndTable(schemaName, tableName)));
    }

    @Test
    void shouldntMatchMapWhenSchemaIsDiferent() {
        String schemaName = "mapschema";
        String tableName = "maptable";
        table = DatabaseCatalogTable.schemaAndTable(schemaName, tableName);
        assertFalse(table.matchesMap(mapWithSchemaAndTable(schemaName + "_different", tableName)));
    }

    @Test
    void shouldntMatchMapWhenTableIsDiferent() {
        String schemaName = "mapschema";
        String tableName = "maptable";
        table = DatabaseCatalogTable.schemaAndTable(schemaName, tableName);
        assertFalse(table.matchesMap(mapWithSchemaAndTable(schemaName, tableName + "_different")));
    }

    private static Map<String, Object> mapWithSchemaAndTable(String schemaName, String tableName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("TABLE_SCHEM", schemaName);
        map.put("TABLE_NAME", tableName);
        return map;
    }

    @Test
    void shouldThrowAnExceptionOnAnEmptyTableName() {
        assertThrows(IllegalArgumentException.class, () -> DatabaseCatalogTable.schemaAndTable("something", ""));
    }
}