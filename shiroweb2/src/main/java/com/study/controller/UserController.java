package com.study.controller;

import com.study.vo.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户登录
 */
@Controller
public class UserController {

    @RequestMapping(value = "subLogin",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    //@ResponseBody
    public String subLogin(User user){
    //参数通过对象来接收
        //主体提交请求
        //获取主体
       Subject subject= SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken=new UsernamePasswordToken(user.getUserName(),user.getPassWord());
        System.out.println("token:"+usernamePasswordToken);
        try {
            subject.login(usernamePasswordToken);
        }catch (Exception e){
            //e.printStackTrace();
            System.out.println("错误信息-----------"+e.getMessage());
            return e.getMessage();
        }

        if(subject.hasRole("admin")){
            System.out.println("用户有管理员权限");
        }

        return "success";
    }

    @RequiresRoles("admin")
    @ResponseBody
    @RequestMapping(value="/testRole",method=RequestMethod.GET)
    public String testRole(){
        return "testRole success";
    }

    @RequiresRoles("admin1")
    @ResponseBody
    @RequestMapping(value="/testRole2",method=RequestMethod.GET)
    public String testRole2(){
        return "testRole2 success";
    }
    /*shiro的过滤器与这里所有的注解有着相同的效果,也可以自己自定义Filter*/
    /*这里的注解生效，跟CustomRealm有关系，里面的某个方法设置相关用户的权限*/
    /*该注解表示只有该权限的时候才可以访问下面的方法*/
    @RequiresPermissions({"user:test"})
    @ResponseBody
    @RequestMapping(value="/testPermission",method=RequestMethod.GET)
    public String testPermission(){
        return "testRole3 success";
    }
}
