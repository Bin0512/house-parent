package com.jike.myhouse.biz.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.jike.myhouse.biz.mapper.UserMapper;
import com.jike.myhouse.common.model.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Resource
    private UserMapper userMapper;

    @Value("${domain.name}")
    private String domainName;

    @Value("${spring.mail.username}")
    private String from;

    //removalListener 当用户在15分钟之内没有激活就将缓存中的数据删除
    private final Cache<String,String> registerCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(15, TimeUnit.MINUTES)
            .removalListener(new RemovalListener<String, String>() {
                @Override
                public void onRemoval(RemovalNotification<String, String> notification) {
                    String email = notification.getValue();
                    User user = new User();
                    user.setEmail(email);
                    List<User> targetUser = userMapper.selectUsersByQuery(user);
                    if (!targetUser.isEmpty() && Objects.equals(targetUser.get(0).getEnable(),0)){
                        userMapper.deleteByEmail(email);
                    }
                }
            }).build();

    private final Cache<String,String> resetCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(15,TimeUnit.MINUTES).build();
    /**
     * 异步发送邮箱
     * 1、缓存key-email的关系 2、借助spring mail发送邮件 3、借助异步框架进行异步操作
     * @param email
     */
    @Async
    public void registerNotify(String email){
        //生成10位长度的字符串（a-z）,包含大小写
        String randomKey = RandomStringUtils.randomAlphabetic(10);
        registerCache.put(randomKey,email);
        //构建邮箱激活链接
        String url = "http://" + domainName + "/accounts/verify?key=" + randomKey;
        sendMail("房产平台激活邮件",url,email);
    }

    @Async
    public void sendMail(String title,String url,String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setSubject(title);
        message.setTo(email);
        message.setText(url);
        mailSender.send(message);
    }

    public boolean enable(String key){
        String email = registerCache.getIfPresent(key);
        if (StringUtils.isBlank(email)){
            return false;
        }
        User user = new User();
        user.setEmail(email);
        user.setEnable(1);
        userMapper.updateByEmailSelective(user);
        //移除缓存
        registerCache.invalidate(key);
        return true;
    }

    @Async
    public void resetNotify(String email) {
        //生成10位长度的字符串（a-z）,包含大小写
        String randomKey = RandomStringUtils.randomAlphabetic(10);
        resetCache.put(randomKey,email);
        //构建邮箱激活链接
        String url = "http://" + domainName + "/accounts/reset?key=" + randomKey;
        sendMail("房产平台重置邮件",url,email);
    }

    public String getResetEmail(String key) {
        return resetCache.getIfPresent(key);
    }

    public void invalidateRestKey(String key) {
        resetCache.invalidate(key);
    }




    public static void main(String[] args) {
        String str = RandomStringUtils.randomAlphabetic(10);
        String str1 = RandomStringUtils.randomAlphabetic(5);
        String str2 = RandomStringUtils.randomAlphabetic(3);
        System.out.println(str);
        System.out.println(str1);
        System.out.println(str2);

        //产生5位长度的随机字符串，中文环境下是乱码
        RandomStringUtils.random(5);

        //使用指定的字符生成5位长度的随机字符串
        RandomStringUtils.random(5, new char[]{'a','b','c','d','e','f', '1', '2', '3'});

        //生成指定长度的字母和数字的随机组合字符串
        RandomStringUtils.randomAlphanumeric(5);

        //生成随机数字字符串
        RandomStringUtils.randomNumeric(5);

        //生成随机[a-z]字符串，包含大小写
        RandomStringUtils.randomAlphabetic(5);

        //生成从ASCII 32到126组成的随机字符串
        RandomStringUtils.randomAscii(4);
    }



}
