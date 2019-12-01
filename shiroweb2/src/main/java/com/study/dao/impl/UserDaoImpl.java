package com.study.dao.impl;

import com.study.dao.UserDao;
import com.study.vo.User;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class UserDaoImpl implements UserDao {


    @Resource
    private JdbcTemplate jdbcTemplate;

    public User getUserByUserName(String userName) {

        String sql="select username,password from users where username=?";
        List<User> users=jdbcTemplate.query(sql, new String[]{userName}, new RowMapper<User>() {

            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user=new User();
                user.setPassWord(resultSet.getString("username"));
                user.setPassWord(resultSet.getString("password"));
                return user;
            }
        });

        if (CollectionUtils.isEmpty(users)){
            return null;
        }
        return users.get(0);
    }

    @Override
    public List<String> getRolesByUserName(String userName) {

        String sql="select role_name from user_roles where username=?";
        return jdbcTemplate.query(sql, new String[]{userName}, new RowMapper<String>() {
            @Nullable
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {

                return resultSet.getString("role_name");
            }
        });
    }
}
