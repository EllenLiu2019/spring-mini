package com.minis.jdbc.core;

import java.sql.*;

public abstract class OldJdbcTemplate {
    public Object query(String sql) {
        Connection con = null;
        PreparedStatement preStatement = null;
        ResultSet resultSet = null;
        Object resultObj;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/spring-mini", "deepblue", "deepblue123456");
            preStatement = con.prepareStatement(sql);
            resultSet = preStatement.executeQuery();
            resultObj = doInStatement(resultSet);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                resultSet.close();
                preStatement.close();
                con.close();
            } catch (Exception ignored) {
            }
        }
        return resultObj;
    }

    public abstract Object doInStatement(ResultSet resultSet);

}
