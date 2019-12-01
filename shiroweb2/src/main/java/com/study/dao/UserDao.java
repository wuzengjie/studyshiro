package com.study.dao;

import com.study.vo.User;

import java.util.List;

public interface UserDao {

    User getUserByUserName(String userName);

    List<String> getRolesByUserName(String userName);
}
