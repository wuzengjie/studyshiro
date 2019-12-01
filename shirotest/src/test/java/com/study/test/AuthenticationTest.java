package com.study.test;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;

/**用户登录*/
public class AuthenticationTest {

    SimpleAccountRealm simpleAccountRealm=new SimpleAccountRealm();

    @Before
    public void addUser(){
        //admin,user为角色
        simpleAccountRealm.addAccount("zhangsan","123456","admin","user");
    }


    @Test
    public void testAuthenticationTest(){

        //构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager=new DefaultSecurityManager();

        defaultSecurityManager.setRealm(simpleAccountRealm);


        //主体提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject= SecurityUtils.getSubject();

        UsernamePasswordToken token=new UsernamePasswordToken("zhangsan","123456");
        System.out.println(token);
        subject.login(token);
        System.out.println("打印是否认证："+subject.isAuthenticated());
        //subject.logout();

        subject.checkRoles("admin");


    }
}
