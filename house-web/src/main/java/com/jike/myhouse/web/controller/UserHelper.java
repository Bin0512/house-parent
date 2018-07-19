package com.jike.myhouse.web.controller;

import com.google.common.base.Objects;
import com.jike.myhouse.common.model.User;
import com.jike.myhouse.common.result.ResultMsg;
import org.apache.commons.lang3.StringUtils;

public class UserHelper {

    public static ResultMsg validate(User user){
        //验证邮箱（）
        if (StringUtils.isBlank(user.getEmail())){
            return ResultMsg.errorMsg("Email有误");
        }
        //验证用户名
        if (StringUtils.isBlank(user.getName())){
            return ResultMsg.successMsg("用户名有误");
        }
        //验证两次输入的密码是否一致
        if (StringUtils.isBlank(user.getConfirmPasswd()) || StringUtils.isBlank(user.getPasswd())
                || !user.getPasswd().equals(user.getConfirmPasswd())){
            return ResultMsg.errorMsg("两次输入的密码不一致且不能为空");
        }
        //验证密码位数是否满足要求
        if (user.getPasswd().length() < 5){
            return ResultMsg.errorMsg("密码长度不能小于5位数");
        }
        //验证通过后返回空字符串
        return ResultMsg.successMsg("");
    }


/*    public static ResultMsg validateResetEmail(String key, String password, String confirmPassword) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword)) {
            return ResultMsg.errorMsg("参数有误");
        }
        if (!Objects.equal(password, confirmPassword)) {
            return ResultMsg.errorMsg("密码必须与确认密码一致");
        }
        return ResultMsg.successMsg("");
    }*/

    public static ResultMsg validateResetPassword(String key, String passwd, String confirmPasswd) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(passwd) || StringUtils.isBlank(confirmPasswd)) {
            return ResultMsg.errorMsg("参数有误");
        }
        if (!Objects.equal(passwd, confirmPasswd)) {
            return ResultMsg.errorMsg("密码必须与确认密码一致");
        }
        return ResultMsg.successMsg("");
    }
}
