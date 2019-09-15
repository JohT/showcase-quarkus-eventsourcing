package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database;

import java.util.Map;
import java.util.Objects;

public class DatabaseCatalogColumn {

    private static final String MESSAGE_COLUMN_CONTAINS_UNEXPECTED_CONTENT = "Database catalog column %s contains unexpected %s";

    private final DatabaseCatalogTable table;
    private final String name;

    /**
     * Creates a {@link DatabaseCatalogColumn} for the given {@link DatabaseCatalogTable} and {@link String} "column".
     * 
     * @param table {@link DatabaseCatalogTable}
     * @param column {@link String}
     * @return {@link DatabaseCatalogColumn}
     */
    public static final DatabaseCatalogColumn columnIn(DatabaseCatalogTable table, String columnname) {
        return new DatabaseCatalogColumn(table, columnname);
    }

    DatabaseCatalogColumn(DatabaseCatalogTable table, String columnname) {
        this.table = Objects.requireNonNull(table, "table may not be null");
        this.name = notEmpty(trimmedOrEmpty(columnname), "columnname may not be empty");
    }

    public DatabaseCatalogTable getTable() {
        return table;
    }

    public String getName() {
        return name;
    }

    public boolean matchesMap(Map<String, Object> catalogRowFields) {
        return nameMatchesMap(catalogRowFields) && getTable().matchesMap(catalogRowFields);
    }

    private boolean nameMatchesMap(Map<String, Object> catalogRowFields) {
        return getStringField("COLUMN_NAME", catalogRowFields).equalsIgnoreCase(getName());
    }

    private static String getStringField(String fieldname, Map<String, Object> catalogRowFields) {
        Object fieldContent = catalogRowFields.get(fieldname.toUpperCase());
        if (!(fieldContent instanceof String)) {
            throw new IllegalArgumentException(String.format(MESSAGE_COLUMN_CONTAINS_UNEXPECTED_CONTENT, fieldname, fieldContent));
        }
        return (String) fieldContent;
    }

    @Override
    public boolean equals(Object other) {
        if ((other == null) || (!getClass().equals(other.getClass()))) {
            return false;
        }
        DatabaseCatalogColumn castOther = (DatabaseCatalogColumn) other;
        return Objects.equals(table, castOther.table) && Objects.equals(name, castOther.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, name);
    }

    @Override
    public String toString() {
        return "TableColumn [table=" + table + ", name=" + name + "]";
    }

    private static String trimmedOrEmpty(String value) {
        return Objects.toString(value, "").trim();
    }

    private static String notEmpty(String value, String errormessage) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException(errormessage);
        }
        return value;
    }
}