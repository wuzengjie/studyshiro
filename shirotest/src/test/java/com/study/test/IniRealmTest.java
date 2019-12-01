package com.study.test;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/***
 * 权限管理
 */
public class IniRealmTest {

    @Test
    public void testAuthentication(){

        //权限管理
        IniRealm iniRealm=new IniRealm("classpath:user.ini");

        //构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager=new DefaultSecurityManager();

        defaultSecurityManager.setRealm(iniRealm);

        //主体提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject= SecurityUtils.getSubject();

        UsernamePasswordToken token=new UsernamePasswordToken("Mark","123456");
        System.out.println(token);
        subject.login(token);
        System.out.println("打印是否认证："+subject.isAuthenticated());

        //检查当前的角色
        subject.checkRoles("admin");

        //以下两种写法都合适
        //检查是否有相关权限
        subject.checkPermission("user:delete");

        subject.checkPermission("update");
    }
}
