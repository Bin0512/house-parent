package com.jike.myhouse.biz.mapper;

import com.jike.myhouse.common.model.User;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    List<User> selectUsers();

    int deleteByEmail(String email);

    int updateByEmailSelective(User user);

    List<User> selectUsersByQuery(User user);

    void update(User updateUser);
}