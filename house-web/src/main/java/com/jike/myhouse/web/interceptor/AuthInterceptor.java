package com.jike.myhouse.web.interceptor;

import com.google.common.base.Joiner;
import com.jike.myhouse.common.constants.CommonConstants;
import com.jike.myhouse.common.model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor{

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        Map<String,String[]> map = request.getParameterMap();
        map.forEach((k,v) ->{
            if (k.equals("errorMsg") || k.equals("successMsg") || k.equals("target")){
                request.setAttribute(k, Joiner.on(",").join(v));
            }
        });

        String reqUri = request.getRequestURI();
        if (reqUri.startsWith("/static") || reqUri.startsWith("/error")){
            return true;
        }
        HttpSession session = request.getSession(true);
        User user = (User) session.getAttribute(CommonConstants.USER_ATTRIBUTE);
        if (user != null){
            //存入线程，使之成为该线程的共享变量
            UserContext.setUser(user);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        UserContext.remove();
    }
}
