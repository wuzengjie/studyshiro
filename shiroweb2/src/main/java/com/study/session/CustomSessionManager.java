package com.study.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;

import javax.servlet.ServletRequest;
import java.io.Serializable;

/**
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
            request=((WebSessionKey) sessionKey).getServletRequest();
        }
        System.out.println(request+"----"+sessionId);
        if(request!=null&&sessionId!=null){
            Session session= (Session) request.getAttribute(sessionId.toString());
            if(session!=null){
                return session;
            }
        }

        //第一次访问时会执行这个方法，这个方法调用底层会去多次读取session
        Session session=super.retrieveSession(sessionKey);
        if(request!=null&&sessionId!=null){
            request.setAttribute(sessionId.toString(),session);
        }
        return session;
    }
}
