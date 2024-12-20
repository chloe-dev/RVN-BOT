package bio.chloe.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);

    private static HikariDataSource hikariDataSource;

    private static volatile DatabaseManager instance;

    private DatabaseManager(String dbUrl) {
        initializeHikariDataSource(dbUrl);
    }

    private void initializeHikariDataSource(String dbUrl) {
        HikariConfig hikariConfiguration = new HikariConfig();

        hikariConfiguration.setJdbcUrl("jdbc:sqlite:" + dbUrl); // TODO: Surely this has to fail somehow... TestConnection method, perhaps?
        hikariConfiguration.setDriverClassName("org.sqlite.JDBC");

        // Connection pool settings.
        hikariConfiguration.setMaximumPoolSize(10);
        hikariConfiguration.setMinimumIdle(2);
        hikariConfiguration.setIdleTimeout(300000);
        hikariConfiguration.setConnectionTimeout(30000);

        // SQLite properties.
        Properties dataSourceProperties = new Properties();

        dataSourceProperties.setProperty("pragma.foreign_keys", "ON");
        dataSourceProperties.setProperty("pragma.journal_mode", "WAL");

        hikariConfiguration.setDataSourceProperties(dataSourceProperties);

        hikariDataSource = new HikariDataSource(hikariConfiguration);
    }

    public static DatabaseManager getInstance() {
        return instance;
    }

    public static boolean initializeDatabaseManager(String dbUrl) {
        if (instance == null) {
            instance = new DatabaseManager(dbUrl);
        }

        // Test the database connection (function).

        // Instantiate the database schema (by use of IF NOT EXISTS statements) (function).

        return true;
    }

    public static void closeDatabaseManager() {
        if (hikariDataSource != null && !(hikariDataSource.isClosed())) {
            hikariDataSource.close();
        }
    }

    // Generics for updating database information.

    public void executeQuery(String sql, PreparedStatementConsumer preparedStatementConsumer, ResultSetConsumer resultSetConsumer) {
        try (Connection dbConnection = hikariDataSource.getConnection(); PreparedStatement preparedStatement = dbConnection.prepareStatement(sql)) {
            preparedStatementConsumer.accept(preparedStatement);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSetConsumer.accept(resultSet);
            }
        } catch (SQLException sqlException) {
            LOGGER.error("SQLException occurred whilst executing query \"{}.\"", sql);

            throw new DatabaseException("SQLException occurred whilst executing a query!", sqlException);
        }
    }

    public <T> T executeQueryWithResult(String sql, PreparedStatementConsumer preparedStatementConsumer, ResultSetMapper<T> resultSetMapper) {
        try (Connection dbConnection = hikariDataSource.getConnection(); PreparedStatement preparedStatement = dbConnection.prepareStatement(sql)) {
            preparedStatementConsumer.accept(preparedStatement);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSetMapper.apply(resultSet);
            }
        } catch (SQLException sqlException) {
            LOGGER.error("SQLException occurred whilst executing query \"{}.\"", sql);

            throw new DatabaseException("SQLException occurred whilst executing a query!", sqlException);
        }
    }

    public int executeUpdate(String sql, PreparedStatementConsumer preparedStatementConsumer) {
        try (Connection dbConnection = hikariDataSource.getConnection(); PreparedStatement preparedStatement = dbConnection.prepareStatement(sql)) {
            preparedStatementConsumer.accept(preparedStatement);

            return preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            LOGGER.error("SQLException occurred whilst executing query \"{}.\"", sql);

            throw new DatabaseException("SQLException occurred whilst executing a query!", sqlException);
        }
    }

    public int[] executeBatchUpdate(String sql, PreparedStatementConsumer preparedStatementConsumer, BatchPreparer batchPreparer) {
        try (Connection dbConnection = hikariDataSource.getConnection(); PreparedStatement preparedStatement = dbConnection.prepareStatement(sql)) {
            preparedStatementConsumer.accept(preparedStatement);

            batchPreparer.accept(preparedStatement);

            return preparedStatement.executeBatch();
        } catch (SQLException sqlException) {
            LOGGER.error("SQLException occurred whilst executing query \"{}.\"", sql);

            throw new DatabaseException("SQLException occurred whilst executing a query!", sqlException);
        }
    }

    @FunctionalInterface
    public interface PreparedStatementConsumer {
        void accept(PreparedStatement preparedStatement) throws SQLException;
    }

    @FunctionalInterface
    public interface ResultSetConsumer {
        void accept(ResultSet resultSet) throws SQLException;
    }

    @FunctionalInterface
    public interface ResultSetMapper<T> {
        T apply(ResultSet resultSet) throws SQLException;
    }

    @FunctionalInterface
    public interface BatchPreparer {
        void accept(PreparedStatement preparedStatement) throws SQLException;
    }

    public static class DatabaseException extends RuntimeException {
        public DatabaseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
