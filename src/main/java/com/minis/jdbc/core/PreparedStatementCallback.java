package com.minis.jdbc.core;

import java.sql.PreparedStatement;

public interface PreparedStatementCallback {
    Object doInPreparedStatement(PreparedStatement preparedStatement);
}
