package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database.DatabaseCatalogColumn;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database.DatabaseCatalogTable;

class DatabaseCatalogColumnTest {

    private static final DatabaseCatalogTable TABLE = DatabaseCatalogTable.schemaAndTable("schema", "table");
    private static final DatabaseCatalogTable DIFFERENT_TABLE = DatabaseCatalogTable.schemaAndTable("schema", "differenttable");
    /**
     * class under test.
     */
    private DatabaseCatalogColumn column;

    @Test
    void shouldContainTable() {
        column = DatabaseCatalogColumn.columnIn(TABLE, "something");
        assertEquals(TABLE, column.getTable());
    }

    @Test
    void shouldContainColumn() {
        String columnname = "column";
        column = DatabaseCatalogColumn.columnIn(TABLE, columnname);
        assertEquals(columnname, column.getName());
    }

    @Test
    void matchesMapOnMatchingSchemaAndTable() {
        String columnname = "mapcolumn";
        column = DatabaseCatalogColumn.columnIn(TABLE, columnname);
        assertTrue(column.matchesMap(mapWithTableAndColumn(TABLE, columnname)));
    }

    @Test
    void shouldntMatchMapWhenTableIsDifferent() {
        String columnname = "mapcolumn";
        column = DatabaseCatalogColumn.columnIn(TABLE, columnname);
        assertFalse(column.matchesMap(mapWithTableAndColumn(DIFFERENT_TABLE, columnname)));
    }

    @Test
    void shouldntMatchMapWhenColumnIsDifferent() {
        String columnname = "mapcolumn";
        column = DatabaseCatalogColumn.columnIn(TABLE, columnname);
        assertFalse(column.matchesMap(mapWithTableAndColumn(TABLE, "different" + columnname)));
    }

    @Test
    void shouldFailIfDatabaseCatalogColumnMapDoesNotContainTheNecessaryFields() {
        column = DatabaseCatalogColumn.columnIn(TABLE, "something");
        assertThrows(IllegalArgumentException.class, () -> column.matchesMap(Collections.emptyMap()));
    }

    @Test
    void shouldThrowAnExceptionOnAnEmptyColumnName() {
        assertThrows(RuntimeException.class, () -> DatabaseCatalogColumn.columnIn(TABLE, ""));
    }

    @Test
    void shouldThrowAnExceptionWhenTableIsNull() {
        assertThrows(RuntimeException.class, () -> DatabaseCatalogColumn.columnIn(null, "column"));
    }

    private static Map<String, Object> mapWithTableAndColumn(DatabaseCatalogTable table, String columnName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("TABLE_SCHEM", table.getSchema());
        map.put("TABLE_NAME", table.getName());
        map.put("COLUMN_NAME", columnName);
        return map;
    }

}