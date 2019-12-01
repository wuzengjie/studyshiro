package com.study.test;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 自定义的权限管理
 */
public class CustomReam extends AuthorizingRealm{

    //模拟数据库，为方便使用集合进行代替
    Map<String,String> map=new HashMap<String, String>();
    {
        //未进行加密
        //map.put("wzj","123456");
        //使用md5加密，同时添加盐
        map.put("wzj","73bea81c6c06bacab41a995495239545");
        super.setName("CustomRealm");
    }

    //进行授权
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        String userName=(String)principalCollection.getPrimaryPrincipal();

        //从数据库或者缓存中获取角色数据
        Set<String> roles=getRolesByUserName(userName);
        //获取权限
        Set<String> permissions=getPermissionsByUserName(userName);
        //创建对象
        SimpleAuthorizationInfo simpleAuthorizationInfo=new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(permissions);
        simpleAuthorizationInfo.setRoles(roles);

        return simpleAuthorizationInfo;
    }

    //可以修改为从数据库或者缓存中取出数据
    private Set<String> getRolesByUserName(String userName) {
        Set<String> set=new HashSet<String>();
        set.add("admin");
        set.add("user");
        return set;
    }
    //此处一样可以修改为从数据库获取缓存中取出数据
    private Set<String> getPermissionsByUserName(String userName) {
        Set<String> set=new HashSet<String>();
        set.add("user:delete");
        set.add("user:select");
        return set;
    }

    //用户信息的验证
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //1.从主体传过来的认证信息中，获取用户名
        String userName=(String)authenticationToken.getPrincipal();
        //通过用户名到数据库中获取凭证
        String passWord=getPassWordByUserName(userName);
        if(passWord==null){
            return null;
        }
        //创建一个返回对象
        SimpleAuthenticationInfo authenticationInfo=new SimpleAuthenticationInfo(userName,passWord,"CustomeRealm");
        System.out.println("返回authenticationInfo："+authenticationInfo);
        //在返回对象之间将盐添加进去
        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes("mark"));
        return authenticationInfo;
    }

    //模拟数据库访问，返回密码
    private String getPassWordByUserName(String userName) {

        return map.get(userName);
    }

    public static void main(String[] args){
        //使用md5进行加密，还需要进行加盐，而使用盐进行加密一般是使用随机数
        Md5Hash md5Hash=new Md5Hash("123456","mark");
        System.out.println(md5Hash);
    }
}
