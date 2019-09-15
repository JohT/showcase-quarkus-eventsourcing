package io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database.DatabaseCatalogQuery;
import io.github.joht.showcase.quarkuseventsourcing.messaging.infrastructure.axon.database.DatabaseCatalogTable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseCatalogQueryTest {

    private static final String TABLE_ONE = "schema.table1";
    private static final String TABLE_TWO = "schema.table2";

    @Mock
    private Connection connection;

    @Mock
    private ResultSet queryResult;

    @Mock
    private ResultSetMetaData resultSetMetaData;

    @Captor
    private ArgumentCaptor<DatabaseCatalogTable> queriedTable;

    /**
     * class under test.
     */
    @Spy
    @InjectMocks
    private DatabaseCatalogQuery query;

    @Test
    void queryShouldReturnAnEmptyListWhenThereAreNoTablesToQuery() {
        assertTrue(query.getResults().isEmpty());
    }

    @Test
    void queryShouldReturnAnEmptyListWhenThereAreNoQueryResults() throws SQLException {
        query.tablename(TABLE_ONE);
        doReturn(queryResult).when(query).queryCatalog(queriedTable.capture());

        assertTrue(query.getResults().isEmpty());
    }

    @Test
    void queryShouldReturnResultsForResultsForTwoTables() throws SQLException {
        query.tablename(TABLE_ONE).tablename(TABLE_TWO);
        doReturn(queryResult).when(query).queryCatalog(queriedTable.capture());
        when(queryResult.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(queryResult.getMetaData()).thenReturn(resultSetMetaData);

        assertEquals(2, query.getResults().size());
    }

}