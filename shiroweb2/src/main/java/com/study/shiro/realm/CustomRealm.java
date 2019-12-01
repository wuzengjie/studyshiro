package com.study.shiro.realm;

import com.study.dao.UserDao;
import com.study.vo.User;
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

import javax.annotation.Resource;
import java.util.*;

/**
 * 自定义的权限管理
 */
public class CustomRealm extends AuthorizingRealm{

    @Resource
    private UserDao userDao;

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
        System.out.println("完成授权"+simpleAuthorizationInfo);
        return simpleAuthorizationInfo;
    }

    //可以修改为从数据库或者缓存中取出数据
    private Set<String> getRolesByUserName(String userName) {
        System.out.println("从数据库取出角色数据");
        //查询角色的方法
        List<String> roles=userDao.getRolesByUserName(userName);
        Set<String> set=new HashSet<String>(roles);
        return set;
    }
    //此处一样可以修改为从数据库获取缓存中取出数据
    private Set<String> getPermissionsByUserName(String userName) {
        Set<String> set=new HashSet<String>();

        //这里设置的用户权限，从而使@RequiresPermissions生效，表示有当前权限才进行访问那个方法
        set.add("user:delete");
        set.add("user:select");
        set.add("user:test");
        return set;
    }

    //用户信息的验证
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //1.从主体传过来的认证信息中，获取用户名
        String userName=(String)authenticationToken.getPrincipal();
        //通过用户名到数据库中获取凭证
        String passWord=getPassWordByUserName(userName);
        System.out.println("密码："+passWord);
        if(passWord==null){
            return null;
        }
        //创建一个返回对象
        SimpleAuthenticationInfo authenticationInfo=new SimpleAuthenticationInfo
                (userName,passWord,"customRealm");

        //在返回对象之间将盐添加进去,盐可以是随机数，也可以是username
        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(userName));
        System.out.println("返回authenticationInfo："+authenticationInfo);
        return authenticationInfo;
    }

    //模拟数据库访问，返回密码
    private String getPassWordByUserName(String userName) {

        User user=userDao.getUserByUserName(userName);
        if(user!=null){
            return user.getPassWord();
        }

        return null;
    }

    public static void main(String[] args){
        //使用md5进行加密，还需要进行加盐，而使用盐进行加密一般是使用随机数
        Md5Hash md5Hash=new Md5Hash("123456","Mark");
        System.out.println(md5Hash);
    }
}
