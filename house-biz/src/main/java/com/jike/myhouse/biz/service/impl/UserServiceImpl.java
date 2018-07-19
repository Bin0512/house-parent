package com.jike.myhouse.biz.service.impl;

import com.google.common.collect.Lists;
import com.jike.myhouse.biz.mapper.UserMapper;
import com.jike.myhouse.biz.service.FileService;
import com.jike.myhouse.biz.service.IUserService;
import com.jike.myhouse.biz.service.MailService;
import com.jike.myhouse.common.model.User;
import com.jike.myhouse.common.utils.BeanHelper;
import com.jike.myhouse.common.utils.HashUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private FileService fileService;

    @Resource
    private MailService mailService;

    @Value("${file.prefix}")
    private String imgPrefix;

    public User getUserById(Long userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    public List<User> selectUsers(){
        return userMapper.selectUsers();
    }

    /**
     *1、插入数据，非激活；密码加盐；保存头像到本地
     * 2、生成key，绑定email
     * 3、发送邮件给用户
     * @param user
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addUser(User user) {
        //密码加盐
        user.setPasswd(HashUtils.encryPasswor(user.getPasswd()));
        //将用户头像保存到本地
        List<String> imgList =  fileService.getImgPaths(Lists.newArrayList(user.getAvatarFile()));
        if (CollectionUtils.isNotEmpty(imgList)){
            user.setAvatar(imgList.get(0));
        }
        //数据表中的字段均为NOT NULL,由于对象存在未设置的属性，为避免插入数据时报错，将为空的属性都设置默认值
        BeanHelper.setDefaultProp(user,User.class);
        //设置创建时间和更新时间为当前系统时间
        BeanHelper.onInsert(user);
        //设置非激活状态
        user.setEnable(0);
        //数据持久化
        userMapper.insert(user);
        //发送邮件 todo
        mailService.registerNotify(user.getEmail());
        return true;
    }

    @Override
    public Boolean enable(String key) {

        return mailService.enable(key);
    }

    @Override
    public User auth(String username, String password) {
        User user = new User();
        user.setEmail(username);
        user.setPasswd(HashUtils.encryPasswor(password));
        user.setEnable(1);
        List<User> list = getUserByQuery(user);
        if (CollectionUtils.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }

    @Override
    public void updateUser(User updateUser, String email) {
        updateUser.setEmail(email);
        BeanHelper.onUpdate(updateUser);
        userMapper.update(updateUser);
    }

    @Override
    public void resetNotify(String email) {
        mailService.resetNotify(email);
    }

    @Override
    public String getResetEmail(String key) {
        String email = "";
        try {
            email = mailService.getResetEmail(key);
        }catch (Exception e){

        }
        return email;
    }

    @Override
    public User reset(String key, String password) {
        String email = getResetEmail(key);
        User updateUser = new User();
        updateUser.setEmail(email);
        updateUser.setPasswd(HashUtils.encryPasswor(password));
        userMapper.update(updateUser);
        mailService.invalidateRestKey(key);
        return getUserByEmail(email);
    }

    @Override
    public User getUserByEmail(String email) {
        User queryUser = new User();
        queryUser.setEmail(email);
        List<User> users = getUserByQuery(queryUser);
        if (!users.isEmpty()){
            return users.get(0);
        }
        return null;
    }

    public List<User> getUserByQuery(User user) {
        List<User> list = userMapper.selectUsersByQuery(user);
        list.forEach(u -> {
            u.setAvatar(imgPrefix + u.getAvatar());
        });
        return list;
    }


}

