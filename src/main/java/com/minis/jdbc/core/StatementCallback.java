package com.minis.jdbc.core;

import java.sql.Statement;

public interface StatementCallback {
    Object doInStatement(Statement statement);
}
