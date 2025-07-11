package com.minis.test.jdbc;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.jdbc.core.JdbcTemplate;
import com.minis.test.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@Slf4j
public class UserService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User getUser(int userId) {
        final String sql = "select id,name,birthday from user where id=" + userId;
        return (User) jdbcTemplate.query(statement -> {
            try {
                User user = null;
                ResultSet resultSet = statement.executeQuery(sql);
                if (resultSet.next()) {
                    user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setName(resultSet.getString("name"));
                    user.setBirthday(new Date(resultSet.getDate("birthday").getTime()));
                }
                return user;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public User getUserPre(int userId) {
        final String sql = "select id, name, birthday from user where `id` = ?;";
        log.debug("sql: {}", sql);
        return (User) jdbcTemplate.query(sql, new Object[]{userId}, statement -> {
            try {
                User user = null;
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setName(resultSet.getString("name"));
                    user.setBirthday(new Date(resultSet.getDate("birthday").getTime()));
                }
                return user;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
