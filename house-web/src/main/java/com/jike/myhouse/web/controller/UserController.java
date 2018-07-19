package com.jike.myhouse.web.controller;

import com.jike.myhouse.biz.service.IUserService;
import com.jike.myhouse.common.constants.CommonConstants;
import com.jike.myhouse.common.model.User;
import com.jike.myhouse.common.result.ResultMsg;
import com.jike.myhouse.common.utils.HashUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UserController {

    @Resource
    private IUserService iUserService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public List<User> selectUsers() {
//        if(true){
//            throw new IllegalArgumentException();
//        }
        return iUserService.selectUsers();
    }

    /**
     * 注册页获取：user对象为空，代表是获取注册页面的请求
     * 注册提交：1、注册验证。2、发送邮件。3、验证失败重定向到注册页面
     *
     * @param user
     * @param modelMap
     * @return
     */
    @RequestMapping("/accounts/register")
    public String accountsRegister(User user, ModelMap modelMap) {
        if (user == null || user.getName() == null) {
            //获取注册页面请求
            return "/user/accounts/register";
        }
        //注册提交
        System.out.println("1");
        //1、注册验证
        ResultMsg resultMsg = UserHelper.validate(user);
        if (resultMsg.isSuccess() && iUserService.addUser(user)) {
            modelMap.put("email", user.getEmail());
            System.out.println("2");
            return "/user/accounts/registerSubmit";
        } else {
            System.out.println("3");
            return "redirect:/accounts/register?" + resultMsg.asUrlParams();
        }
    }

    @RequestMapping("/accounts/verify")
    public String verify(String key) {
        boolean result = iUserService.enable(key);
        if (result) {
            return "redirect:/index?" + ResultMsg.successMsg("激活成功").asUrlParams();
        } else {
            return "redirect:/accounts/register?" + ResultMsg.errorMsg("激活失败，请确认链接是否过期");
        }
    }

    // -------------------------登录流程----------------------

    /**
     * 登录
     *
     * @param request
     * @return
     */
    @RequestMapping("/accounts/signin")
    public String signin(HttpServletRequest request) {
        //1、获取参数，用于判断是登录页请求还是登录操作
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        //在拦截器中会有target设置，目标页
        String target = request.getParameter("target");
        if (username == null || password == null) {
            //登录页请求
            request.setAttribute("target", target);
            return "/user/accounts/signin";
        }
        //2、验证用户名和密码是否匹配
        User user = iUserService.auth(username, password);
        if (user == null) {
            //验证失败，重定向到登录页
            return "redirect:/accounts/signin?target" + target + "&username" + username + "&"
                    + ResultMsg.errorMsg("用户名或密码错误").asUrlParams();
        } else {
            //验证成功，将用户信息存入session
            HttpSession session = request.getSession(true);
            session.setAttribute(CommonConstants.USER_ATTRIBUTE, user);
            //当用户第一次登录时，target为null,默认重定向到首页，否则重定向到用户之前在访问的页面
            //比方说未登录用户正在浏览房产信息，当点击购买时，会先跳转到登录页，让用户登录，当登录成功后，再返回浏览房产信息的页面，而不是首页
            return StringUtils.isNoneBlank(target) ? "redirect:" + target : "redirect:/index";
        }
    }

    @RequestMapping("accounts/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/index";
    }

    //----------------个人信息页--------------------

    @RequestMapping("accounts/profile")
    public String profile(HttpServletRequest request, User updateUser, ModelMap modelMap) {
        if (updateUser.getEmail() == null) {
            return "/user/accounts/profile";
        }
        //根据邮箱去更新用户信息
        iUserService.updateUser(updateUser, updateUser.getEmail());
        User query = new User();
        query.setEmail(updateUser.getEmail());
        List<User> users = iUserService.getUserByQuery(query);
        request.getSession(true).setAttribute(CommonConstants.USER_ATTRIBUTE, users.get(0));
        return "redirect:/accounts/profile?" + ResultMsg.successMsg("更新成功").asUrlParams();
    }

    /**
     * 修改密码
     *
     * @param email
     * @param password
     * @param newPassword
     * @param confirmPassword
     * @param modelMap
     * @return
     */
    @RequestMapping("accounts/changePassword")
    public String changePassword(String email, String password, String newPassword, String confirmPassword, ModelMap modelMap) {
        User user = iUserService.auth(email, password);
        if (user == null || !confirmPassword.equals(newPassword)) {
            return "redirect:/accounts/profile?" + ResultMsg.errorMsg("密码错误").asUrlParams();
        }
        User updateUser = new User();
        updateUser.setPasswd(HashUtils.encryPasswor(newPassword));
        iUserService.updateUser(updateUser, email);
        return "redirect:/accounts/profile?" + ResultMsg.successMsg("更新成功").asUrlParams();
    }

    /**
     * 忘记密码：
     * 1、在登录页面signin.ftl,点击忘记密码
     * 2、进入邮件发送页面,remember.ftl
     *      UserController->UserService->MailService
     *      生成缓存key，value为email
     *      拼接密码重置地址，带上key。
     * 3、用户点击链接，进入密码重置页面,reset.ftl
     *      从缓存中获取key的值，判断链接是否已过期
     *          若过期，重定向到登录页面signin.ftl
     *      否则转发到密码重置页面，此时已经为email何success_key赋值了的
     * 4、
     */

    /**
     * 忘记密码
     * @param username
     * @param modelMap
     * @return
     */
    @RequestMapping("accounts/remember")
    public String remember(String username,ModelMap modelMap){
        if (StringUtils.isBlank(username)){
            return "redirect:/accounts/signin?" + ResultMsg.errorMsg("邮箱不能为空").asUrlParams();
        }
        //通过邮箱重置密码
        iUserService.resetNotify(username);
        modelMap.put("email",username);
        return "/user/accounts/remember";
    }

    @RequestMapping("accounts/reset")
    public String reset(String key,ModelMap modelMap){
        //从缓存中获取邮箱
        String email = iUserService.getResetEmail(key);
        if (StringUtils.isBlank(email)){
            return "redirect:/accounts/signin?" + ResultMsg.errorMsg("重置链接已过期").asUrlParams();
        }
        modelMap.put("email",email);
        //设置成功凭证
        modelMap.put("success_key",key);
        return "/user/accounts/reset";
    }

    @RequestMapping("accounts/resetSubmit")
    public String resetSubmit(HttpServletRequest request,User user){
        //进行参数校验，以success_key为重置凭证（相当于token）,没有这个token就不能进行操作
        //ResultMsg resultMsg = UserHelper.validateResetEmail(user.getKey(),user.getPasswd(),user.getConfirmPasswd());
        System.out.println(user.getKey());
        System.out.println(user.getPasswd());
        System.out.println(user.getConfirmPasswd());
        ResultMsg resultMsg = UserHelper.validateResetPassword(user.getKey(),user.getPasswd(),user.getConfirmPasswd());
        if (!resultMsg.isSuccess()){
            String suffix = "";
            if (StringUtils.isNotBlank(user.getKey())){
                suffix = "email = " + iUserService.getResetEmail(user.getKey()) + "&key =" + user.getKey() + "&";
            }
            return "redirect:/accounts/reset?" + suffix + resultMsg.asUrlParams();
        }
        User updateUser = iUserService.reset(user.getKey(),user.getPasswd());
        request.getSession(true).setAttribute(CommonConstants.USER_ATTRIBUTE,updateUser);
        return "redirect:/index?" + resultMsg.asUrlParams();
    }

}
