package com.minis.test.jdbc;

import com.minis.jdbc.core.OldJdbcTemplate;
import com.minis.app.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class UserJdbcImpl extends OldJdbcTemplate {
    @Override
    public Object doInStatement(ResultSet resultSet) {
        // TODO: 可将结果处理逻辑改写为 callBack 作为 JdbcTemplate 的入参，
        //  所以，新增了 StatementCallback 和 PreparedStatementCallback Functional 接口
        //  这样就不需要每个实体对象创建一个JdbcTemplate的实现类了
        User user = null;
        try {
            if (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setBirthday(new Date(resultSet.getDate("birthday").getTime()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public static void main(String[] args) {
        UserJdbcImpl userJdbc = new UserJdbcImpl();
        User result = (User) userJdbc.query("select * from user");
        System.out.println(result);
    }
}
