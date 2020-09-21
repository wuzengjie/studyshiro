package com.example.ssoserver.controller;

import com.example.ssoserver.entiy.UserDao;
import com.example.ssoserver.util.JwtUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
/**  单点登录测试必须要是域名
 * 127.0.0.1 www.sso.com
 * 127.0.0.1 www.crm.com
 * 127.0.0.1 www.wms.com
 * */
@Controller
public class LoginController {

    Map<String,UserDao> map=new HashMap<>();
    {
        UserDao userDao=new UserDao("1","张三","123456");
        map.put("1",userDao);
    }

    @RequestMapping("preLogin")
    public String preLogin(String url,HttpServletRequest request,Model model){
        HttpSession session=request.getSession(false);
        if(StringUtils.isEmpty(session)){
            //保证在点击登录时可以获取到请求的url
            model.addAttribute("url",url);
            return "login";
        }else{
            String token=(String) session.getAttribute("token");
            return "redirect:"+url+"?token="+token;
        }
    }

    /**验证中心发布token*/
    @RequestMapping("login")
    public String login(HttpServletRequest request, String url,String name,String pwd){
        UserDao userDao=map.get("1");
        if(name.equals(userDao.getUserName())&&pwd.equals(userDao.getPassWord())){
            String token= JwtUtil.getToken(userDao);
            request.getSession().setAttribute("token",token);
            return "redirect:"+url+"?token="+token;
        }else{
            return "login";
        }
    }

    @ResponseBody
    @RequestMapping("test")
    public String test(HttpServletRequest request){
        System.out.println(request.getSession(false).getId());
        return request.getSession().getAttribute("token").toString();
    }

    /**验证中心校验token*/
    @ResponseBody
    @RequestMapping("checkToken")
    public String checkToken(@RequestBody Map<String,Object> map){
        int i=JwtUtil.verifyToken(JwtUtil.getClaimsBody(map.get("token").toString()));
        return i+"";
    }

}
