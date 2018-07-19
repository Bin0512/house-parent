package com.jike.myhouse.biz.service;


import com.jike.myhouse.common.model.User;

import java.util.List;

public interface IUserService {

    User getUserById(Long userId);

    List<User> selectUsers();

    Boolean addUser(User user);

    Boolean enable(String key);

    User auth(String username, String password);

    List<User> getUserByQuery(User user);

    void updateUser(User updateUser, String email);

    void resetNotify(String email);

    String getResetEmail(String key);

    User reset(String key,String password);

    User getUserByEmail(String email);
}
