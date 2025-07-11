package com.minis.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class ArgumentPreparedStatementSetter {
    private Object[] args;

    public ArgumentPreparedStatementSetter(Object[] args) {
        this.args = args;
    }

    public void setValues(PreparedStatement preStatement) throws SQLException {
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
    }
}
