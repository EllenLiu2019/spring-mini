package com.minis.app.service;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.jdbc.core.JdbcTemplate;
import com.minis.jdbc.core.RowMapper;
import com.minis.stereotype.Component;
import com.minis.app.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
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

    public List<User> getUserByName(String name) {
        final String sql = "select id, name, birthday from user where `name` = ?;";
        log.debug("sql: {}", sql);
        List<User> Users = new ArrayList<>();
        return jdbcTemplate.query(sql, new Object[]{name}, statement -> {
            try {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setName(resultSet.getString("name"));
                    user.setBirthday(new Date(resultSet.getDate("birthday").getTime()));
                    Users.add(user);
                }
                return Users;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<User> getAllUsersByName(String name) {
        final String sql = "select id, name, birthday from user where `name` = ?;";
        log.debug("sql: {}", sql);
        return jdbcTemplate.query(sql, new Object[]{name}, userMapper());
    }

    private RowMapper<User> userMapper() {
        return (rs, i) -> {
            User user = new User();
            int id = rs.getInt("id");
            String name = rs.getString("name");
            Date birthday = new Date(rs.getDate("birthday").getTime());
            user.setId(id);
            user.setName(name);
            user.setBirthday(birthday);
            return user;
        };
    }
}
