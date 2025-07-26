package com.minis.jdbc.core;


import com.minis.app.entity.User;

import java.sql.PreparedStatement;
import java.util.List;

public interface PreparedStatementCallback {
    List<User> doInPreparedStatement(PreparedStatement preparedStatement);
}
