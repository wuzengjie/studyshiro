package com.example.ssoserver.configurer;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    //这个方法是在访问接口之前执行的，我们只需要在这里写验证登陆状态的业务逻辑，就可以在用户调用指定接口之前验证登陆状态了
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //每一个项目对于登陆的实现逻辑都有所区别，我这里使用最简单的Session提取User来验证登陆。
        HttpSession session = request.getSession(false);
        //如果session中没有user，表示没登陆
        if (session!=null&&session.getAttribute("login")!=null){
            /*如果会话不为空，user不为空那么就直接进行跳转页面*/
            return true;
        }
        RestTemplate restTemplate=new RestTemplate();
        String token=request.getParameter("token");
        if(!StringUtils.isEmpty(token)){
            String url="http://www.sso.com:8080/checkToken";
            Map<String,String> map=new HashMap<>();
            map.put("token",token);
            String i=restTemplate.postForObject(url,map,String.class);
            if("1".equals(i)||"-1".equals(i)){
                request.getSession().setAttribute("login","login");
                //放行跳转页面
                return true;
            }else{
                //不允许跳转页面
                return false;
            }
        }
        String url="http://www.sso.com:8080/preLogin";
        System.out.println(request.getRequestURL());
        response.sendRedirect(url+"?url="+request.getRequestURL());
        return false;
    }


    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}