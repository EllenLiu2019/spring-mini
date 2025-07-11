package com.minis.jdbc.core;

import com.minis.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;

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

    public Object query(String sql, Object[] args, PreparedStatementCallback preStatementCallback) {
        Connection con = null;
        PreparedStatement preStatement = null;
        try {
            con = dataSource.getConnection();
            preStatement = con.prepareStatement(sql);
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];
                    if (arg instanceof String argStr) {
                        preStatement.setString(i + 1, argStr);
                    } else if (arg instanceof Integer argInt) {
                        preStatement.setInt(i + 1, argInt);
                    } else if (arg instanceof Date argDate) {
                        preStatement.setDate(i + 1, new java.sql.Date((argDate).getTime()));
                    }
                }
            }
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
}
