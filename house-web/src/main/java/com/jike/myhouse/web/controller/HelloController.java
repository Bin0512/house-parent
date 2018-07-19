package com.jike.myhouse.web.controller;

import com.jike.myhouse.biz.service.IUserService;
import com.jike.myhouse.common.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class HelloController {

  @Resource
  private IUserService iUserService;

  @RequestMapping("hello")
  public String  hello(ModelMap modelMap){
    List<User> users = iUserService.selectUsers();
    User one = users.get(0);
    modelMap.put("user", one);
    return "hello";
  }

  @RequestMapping("index")
  public String index(){
     return "homepage/index";
  }

}
