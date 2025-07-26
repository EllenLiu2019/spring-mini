package com.minis.jdbc.core;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.app.entity.User;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Slf4j
public class JdbcTemplate {
    @Autowired
    private DataSource dataSource;

    public Object query(StatementCallback statementCallback) {
        Connection con = null;
        Statement statement = null;
        try {
            con = dataSource.getConnection();
            statement = con.createStatement();
            return statementCallback.doInStatement(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                assert statement != null;
                statement.close();
                con.close();
            } catch (Exception ignored) {
            }
        }
    }

    public List<User> query(String sql, Object[] args, PreparedStatementCallback preStatementCallback) {
        Connection con = null;
        PreparedStatement preStatement = null;
        try {
            con = dataSource.getConnection();
            preStatement = con.prepareStatement(sql);

            ArgumentPreparedStatementSetter argumentSetter = new ArgumentPreparedStatementSetter(args);
            argumentSetter.setValues(preStatement);

            log.info("preStatement: {}", preStatement);
            return preStatementCallback.doInPreparedStatement(preStatement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                assert preStatement != null;
                preStatement.close();
                con.close();
            } catch (Exception ignored) {
            }
        }
    }

    public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) {
        Connection con = null;
        PreparedStatement preStatement = null;
        RowMapperResultSetExtractor<T> extractor = new RowMapperResultSetExtractor<>(rowMapper);
        try {
            con = dataSource.getConnection();
            preStatement = con.prepareStatement(sql);

            ArgumentPreparedStatementSetter argumentSetter = new ArgumentPreparedStatementSetter(args);
            argumentSetter.setValues(preStatement);
            ResultSet resultSet = preStatement.executeQuery();

            return extractor.extractData(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                assert preStatement != null;
                preStatement.close();
                con.close();
            } catch (Exception ignored) {
            }
        }
    }
}
