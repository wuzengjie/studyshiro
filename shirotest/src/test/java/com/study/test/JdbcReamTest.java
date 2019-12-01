package com.study.test;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

public class JdbcReamTest {

    DruidDataSource dataSource=new DruidDataSource();
    {
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/shirotest?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword("");

    }

    @Test
    public void testJdbcReam(){

        JdbcRealm jdbcRealm=new JdbcRealm();
        //设置jdbcReam数据源
        jdbcRealm.setDataSource(dataSource);
        //默认查询权限的开关为false,只有设置为true的时候，才可以进行查询权限
        jdbcRealm.setPermissionsLookupEnabled(true);

        // protected String authenticationQuery = "select password from users where username = ?";
        //以上为源码的查询方式
        //自定义sql查询语句
        //权限以及其他的都是一样的，具体查询源码的sql是如何编写的
        String sql="select psw from test_user where username=?";
        jdbcRealm.setAuthenticationQuery(sql);

        //构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager=new DefaultSecurityManager();
        defaultSecurityManager.setRealm(jdbcRealm);



        //主体提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject= SecurityUtils.getSubject();

        UsernamePasswordToken token=new UsernamePasswordToken("wzj","654321");
        System.out.println(token);
        subject.login(token);
        System.out.println("打印是否认证："+subject.isAuthenticated());

        //检验角色
       subject.checkRole("admin");
        //检验权限
        subject.checkPermission("user:select");
    }

}
