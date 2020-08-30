package com.study.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;

import javax.servlet.ServletRequest;
import java.io.Serializable;
/***
 * shiro使用redis进行session进行共享登录
 */

/**
 * 编写该方法是为了解决我们将session写入到了redis中，如果不进行重写的话那么一次请求将产生查询多次内存库
 * 此时对于内存库的压力比较大
 * 重写sessionManger,原因每次访问链接都读取多次session,导致访问redis数据库的次数过多，
 * 给redis服务造成了压力
 */

public class CustomSessionManager extends DefaultWebSessionManager {

    //ctrl+o快速重写
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
       //sessionKey中存储这request的对象
        Serializable sessionId=getSessionId(sessionKey);
        ServletRequest request=null;
        //第一次访问的时候是没有sessionId
        if(sessionKey instanceof WebSessionKey){
            System.out.println("转化为Request");
            request=((WebSessionKey) sessionKey).getServletRequest();
        }
        //System.out.println(request+"----"+sessionId);
        //第一次访问时同时也是调用这个方法，不过从请求中取到的session是为空的
        if(request!=null&&sessionId!=null){
            Session session= (Session) request.getAttribute(sessionId.toString());
            if(session!=null){
                System.out.println("返回session");
                return session;
            }
        }

        //第一次访问时会执行这个方法，这个方法调用底层会去多次读取session
        //按住ctrl点击retrieveSession进行可以查看到调用了retrieveSessionFromDataSource这个方法，
        // 而这个方法中的sessionDao中的读取session的方法，我们要修改为了从redis中进行读取
        /**
         *原本的session是通过new MemorySessionDAO()进行实现的，而MemorySessionDAO中关于session是存储于ConcurrentHashMap中，
         * 既然是存储于集合中，那么就只要保证单机，要实现分布式需要引入redis
         */
        Session session=super.retrieveSession(sessionKey);
        if(request!=null&&sessionId!=null){
            System.out.println("设置Session");
            request.setAttribute(sessionId.toString(),session);
        }

        return session;
    }
}
