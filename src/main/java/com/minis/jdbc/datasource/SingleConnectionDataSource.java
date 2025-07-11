package com.minis.jdbc.datasource;

import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class SingleConnectionDataSource implements DataSource {
    @Setter
    private String driverClassName;
    @Setter
    @Getter
    private String url;
    @Setter
    private String username;
    @Setter
    private String password;
    @Getter
    private Properties connectionProperties;
    private Connection connection;
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
        if(password != null) {
            mergedProps.setProperty("password", password);
        }
        this.connection = getConnectionFromDriverManager(getUrl(), mergedProps);
        return this.connection;
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
