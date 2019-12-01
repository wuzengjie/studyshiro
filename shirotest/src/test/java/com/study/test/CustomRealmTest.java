package com.study.test;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/**
 * 测试自定义的权限验证
 */
public class CustomRealmTest {
    @Test
    public void testAuthentication(){

       //自定义权限验证器
        CustomReam customReam=new CustomReam();

        //构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager=new DefaultSecurityManager();

        defaultSecurityManager.setRealm(customReam);

        HashedCredentialsMatcher hashedCredentialsMatcher=new HashedCredentialsMatcher();
        //设置加密的名称
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        //设置加密多少次
        hashedCredentialsMatcher.setHashIterations(1);
        customReam.setCredentialsMatcher(hashedCredentialsMatcher);


        //主体提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject= SecurityUtils.getSubject();

        UsernamePasswordToken token=new UsernamePasswordToken("wzj","123456");
        System.out.println(token);
        subject.login(token);
        System.out.println("打印是否认证："+subject.isAuthenticated());

       subject.checkPermission("user:delete");

        subject.checkRole("admin");
    }
}
