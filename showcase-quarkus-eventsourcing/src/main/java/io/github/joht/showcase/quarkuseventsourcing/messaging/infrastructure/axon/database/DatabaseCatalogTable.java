package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class DatabaseCatalogTable {

    private static final String SCHEMA_SEPARATOR = ".";
    private static final String NO_SCHEMA = "";
    private static final Pattern REGEX_SCHEMA_SEPARATOR = Pattern.compile("\\" + SCHEMA_SEPARATOR);
    private static final String MESSAGE_COLUMN_CONTAINS_UNEXPECTED_CONTENT = "Database catalog column %s contains unexpected %s";

    private final String schema;
    private final String name;

    public static final DatabaseCatalogTable fullQualified(String fullQualifiedTableName) {
        return schemaAndTable(extractSchema(fullQualifiedTableName), extractTablename(fullQualifiedTableName));
    }

    public static final DatabaseCatalogTable noSchema(String tablename) {
        return schemaAndTable(NO_SCHEMA, tablename);
    }

    public static final DatabaseCatalogTable schemaAndTable(String schemaName, String tableName) {
        return new DatabaseCatalogTable(schemaName, tableName);
    }

    protected DatabaseCatalogTable(String schema, String tablename) {
        this.schema = trimmedOrEmpty(schema);
        this.name = notEmpty(trimmedOrEmpty(tablename), "the table name may not be empty");
    }

    public String getSchema() {
        return schema;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns a new {@link DatabaseCatalogTable} using the given default schema, <br>
     * if this {@link DatabaseCatalogTable} has no schema and the given default schema is valid.<br>
     * Otherwise returns this (same) object.
     * 
     * @param defaultSchema {@link String}
     * @return {@link DatabaseCatalogTable}
     */
    public DatabaseCatalogTable useDefaultSchema(String defaultSchema) {
        if (isUndefinedSchema(defaultSchema)) {
            return this;
        }
        if (isUndefinedSchema(getSchema())) {
            return schemaAndTable(defaultSchema, getName());
        }
        return this;
    }

    public DatabaseCatalogTable toUpperCaseTablename() {
        return new DatabaseCatalogTable(getSchema(), getName().toUpperCase());
    }

    public DatabaseCatalogTable toLowerCaseTablename() {
        return new DatabaseCatalogTable(getSchema(), getName().toLowerCase());
    }

    public boolean matchesMap(Map<String, Object> catalogRowFields) {
        return schemaMatchesMap(catalogRowFields) && tableMatchesMap(catalogRowFields);
    }

    private boolean schemaMatchesMap(Map<String, Object> catalogRowFields) {
        return getStringField("TABLE_SCHEM", catalogRowFields).equalsIgnoreCase(getSchema());
    }

    private boolean tableMatchesMap(Map<String, Object> catalogRowFields) {
        return getStringField("TABLE_NAME", catalogRowFields).equalsIgnoreCase(getName());
    }

    private static String getStringField(String fieldname, Map<String, Object> catalogRowFields) {
        Object fieldContent = catalogRowFields.get(fieldname.toUpperCase());
        if (!(fieldContent instanceof String)) {
            throw new IllegalArgumentException(String.format(MESSAGE_COLUMN_CONTAINS_UNEXPECTED_CONTENT, fieldname, fieldContent));
        }
        return (String) fieldContent;
    }

    @Override
    public boolean equals(final Object other) {
        if ((other == null) || (!getClass().equals(other.getClass()))) {
            return false;
        }
        DatabaseCatalogTable castOther = (DatabaseCatalogTable) other;
        return Objects.equals(schema, castOther.schema) && Objects.equals(name, castOther.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schema, name);
    }

    @Override
    public String toString() {
        return "DatabaseCatalogTable [schema=" + schema + ", name=" + name + "]";
    }

    private static String extractSchema(String fullQualifiedTablename) {
        return splitFullQualifiedTablename(fullQualifiedTablename)[0];
    }

    private static String extractTablename(String fullQualifiedTablename) {
        return splitFullQualifiedTablename(fullQualifiedTablename)[1];
    }

    private static String[] splitFullQualifiedTablename(String fullQualifiedTablename) {
        String[] splitted = REGEX_SCHEMA_SEPARATOR.split(fullQualifiedTablename);
        return (splitted.length < 2) ? new String[] { NO_SCHEMA, fullQualifiedTablename } : splitted;
    }

    private static boolean isUndefinedSchema(String schemaToCheck) {
        return Objects.equals(NO_SCHEMA, trimmedOrEmpty(schemaToCheck));
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