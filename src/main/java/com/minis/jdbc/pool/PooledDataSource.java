package com.minis.jdbc.pool;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Slf4j
public class PooledDataSource implements DataSource {
    @Setter
    private String driverClassName;
    @Setter
    @Getter
    private String url = "jdbc:mysql://localhost:3306/spring-mini";
    @Setter
    private String username = "deepblue";
    @Setter
    private String password = "deepblue123456";
    @Getter
    private Properties connectionProperties;
    private List<PooledConnection> connections;
    @Setter
    private int initialSize = 2;

    private void initPool() {
        this.connections = new ArrayList<>(initialSize);
        for (int i = 0; i < initialSize; i++) {
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                PooledConnection pooledConnection = new PooledConnection(connection, false);
                this.connections.add(pooledConnection);
                log.debug("*****************init pooled connection {}", i);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnectionFromDriver(username, password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnectionFromDriver(username, password);
    }

    private Connection getConnectionFromDriver(String username, String password) throws SQLException {
        Properties mergedProps = new Properties();
        Properties connProps = getConnectionProperties();
        if (connProps != null) {
            mergedProps.putAll(connProps);
        }
        if (username != null) {
            mergedProps.setProperty("user", username);
        }
        if (password != null) {
            mergedProps.setProperty("password", password);
        }

        if (this.connections == null) {
            initPool();
        }

        PooledConnection availableConnection = getAvailableConnection();
        while (availableConnection == null) {
            availableConnection = getAvailableConnection();
            if (availableConnection == null) {
                try {
                    TimeUnit.MILLISECONDS.sleep(30);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return availableConnection;
    }

    private PooledConnection getAvailableConnection() {
        for (PooledConnection pooledConnection : this.connections) {
            if (!pooledConnection.isActive()) {
                pooledConnection.setActive(true);
            }
            return pooledConnection;
        }
        return null;
    }

    private Connection getConnectionFromDriverManager(String url, Properties mergedProps) throws SQLException {
        return DriverManager.getConnection(url, mergedProps);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
