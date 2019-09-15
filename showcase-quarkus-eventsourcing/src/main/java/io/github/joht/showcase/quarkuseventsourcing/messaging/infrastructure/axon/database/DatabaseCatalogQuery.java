package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Queries the database catalog using the given {@link Connection}. <br>
 * The result contains all columns that match the optionally given schema and tablename pattern.
 * <p>
 * 
 * @author JohT
 * @see DatabaseMetaData#getColumns(String, String, String, String)
 */
public class DatabaseCatalogQuery {

    private static final String MESSAGE_TABLE_NOT_CONTAINED_IN_RESULT = "The already done database catalog query does not contain table %s";

    private final Connection connection;
    private final List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
    private boolean queryFinished = false;

    private String defaultSchema;
    private final Set<DatabaseCatalogTable> tables = new HashSet<>();


    public static final DatabaseCatalogQuery forConnection(Connection connection) {
        return new DatabaseCatalogQuery(connection);
    }

    protected DatabaseCatalogQuery(Connection connection) {
        this.connection = connection;
    }

    /**
     * Sets the default database schema, that will be used for all subsequent calls of {@link #tablename(String)}.<br>
     * If the table is not given in a full qualified manner (schema + "." + table),<br>
     * the default schema will be used.
     * <p>
     * An empty schema is assumed, if it is set to <code>null</code>.<br>
     * The schema will be trimmed, removing leading and trailing spaces.
     * 
     * @param schemaToUse {@link String}
     * @return {@link DatabaseCatalogQuery}
     * @see DatabaseMetaData#getColumns(String, String, String, String)
     */
    public DatabaseCatalogQuery defaultSchema(String schemaToUse) {
        this.defaultSchema = schemaToUse;
        return this;
    }

    /**
     * Adds another table name to be queried from the database catalog.
     * <p>
     * The table name should not be empty or <code>null</code>.<br>
     * The table name will be trimmed, removing leading and trailing spaces.
     * 
     * @param tablenameToQuery {@link String}
     * @return {@link DatabaseCatalogQuery}
     * @see DatabaseMetaData#getColumns(String, String, String, String)
     */
    public DatabaseCatalogQuery tablename(String tablenameToQuery) {
        return tablename(DatabaseCatalogTable.fullQualified(tablenameToQuery));
    }

    /**
     * Adds another {@link DatabaseCatalogTable} to be queried from the database catalog.<br>
     * To make shure, that the table is found, an upper and lower case invariant is added too.
     * <p>
     * {@link DatabaseCatalogTable} should not be <code>null</code>.<br>
     * 
     * @param tablenameToQuery {@link DatabaseCatalogTable}
     * @return {@link DatabaseCatalogQuery}
     * @see DatabaseMetaData#getColumns(String, String, String, String)
     */
    public DatabaseCatalogQuery tablename(DatabaseCatalogTable tablenameToQuery) {
        DatabaseCatalogTable table = tablenameToQuery.useDefaultSchema(defaultSchema);
        this.tables.add(table);
        this.tables.add(table.toUpperCaseTablename());
        this.tables.add(table.toLowerCaseTablename());
        return this;
    }

    /**
     * Gets the (lower case) name of the column type for the given {@link DatabaseCatalogColumn}.
     * 
     * @param columnName {@link String}
     * @param columnTypeName {@link String}
     * @return <code>true</code> on a match
     */
    public String getColumnType(DatabaseCatalogColumn columnToFind) {
        if (!tables.contains(columnToFind.getTable()) && !results.isEmpty()) {
            String message = String.format(MESSAGE_TABLE_NOT_CONTAINED_IN_RESULT, columnToFind.getTable());
            throw new IllegalArgumentException(message);
        }
        return getResults().stream()
                .filter(columnToFind::matchesMap)
                .map(column -> column.get("TYPE_NAME").toString().toLowerCase())
                .findFirst().orElse("");
    }

    /**
     * Triggers database catalog query (if not already done).
     * <p>
     * Please use {@link #getResults()} to query the database catalog and get the results directly.<br>
     * This method is mean't to be used to trigger the query explicitly, <br>
     * e.g. when the {@link Connection} is only opened for this operation. <br>
     * 
     * @return {@link DatabaseCatalogQuery}
     */
    public DatabaseCatalogQuery triggerQuery() {
        if (queryFinished) {
            return this;
        }
        for (DatabaseCatalogTable tablename : tables) {
            try (ResultSet resultSet = queryCatalog(tablename)) {
                while (resultSet.next()) {
                    results.add(resultSetRowAsMap(resultSet));
                }
            } catch (SQLException e) {
                throw new IllegalStateException("error during database catalog query using " + toString(), e);
            }
        }
        this.queryFinished = true;
        return this;
    }

    /**
     * Queries the database catalog (if not already done) and returns the results in a generic way.
     * 
     * @return {@link List} of {@link Map}s with the column name as {@link String}-key and the column content as {@link Object}-value.
     */
    public List<Map<String, Object>> getResults() {
        if (!queryFinished) {
            triggerQuery();
        }
        return Collections.unmodifiableList(results);
    }

    protected ResultSet queryCatalog(DatabaseCatalogTable table) throws SQLException {
        return connection.getMetaData().getColumns(connection.getCatalog(), table.getSchema(), table.getName(), null);
    }

    private static Map<String, Object> resultSetRowAsMap(ResultSet columns) throws SQLException {
        ResultSetMetaData resultSetMetaData = columns.getMetaData();
        Map<String, Object> row = new HashMap<>();
        for (int columnIndex = 1; columnIndex <= resultSetMetaData.getColumnCount(); columnIndex++) {
            row.put(resultSetMetaData.getColumnName(columnIndex), columns.getObject(columnIndex));
        }
        return row;
    }

    @Override
    public String toString() {
        return "DatabaseCatalogQuery [connection=" + connection + ", results=" + results + ", defaultSchema=" + defaultSchema + ", tables="
                + tables + "]";
    }
}