package com.study.session;

import com.study.util.JedisUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 集成redis自带的sessionDao,对里面的方法进行重写
 */
public class RedisSessionDao extends AbstractSessionDAO{

    @Resource
    private JedisUtil jedisUtil;

    private final String SHIRO_SESSION_PREFIX="STUDYSHIRO-SESSION";

//    redis中的key以字节的形式显示:是可以提高存取效率，并且在底层存储中节省空间
    private byte[] getKey(String key){
        return (SHIRO_SESSION_PREFIX+key).getBytes();
    }
    //提供保存session的方法
    private void saveSession(Session session){
        if(session!=null&&session.getId()!=null) {
            byte[] key = getKey(session.getId().toString());
            System.out.println("session.getId().toString():" + session.getId().toString());

            //对session进行序列化
            byte[] value = SerializationUtils.serialize(session);
            System.out.println("session:" + session);

            //设置redis的值与过期时间
            jedisUtil.set(key, value);
            jedisUtil.expire(key, 600);
        }
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId=generateSessionId(session);
        System.out.println("sessionId:"+sessionId);
        //将session与sessionId进行捆绑
        assignSessionId(session,sessionId);
        saveSession(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {

        System.out.println("读取session");
        if(sessionId==null){
            return null;
        }
        byte[] key=getKey(sessionId.toString());
        byte[] value=jedisUtil.get(key);

        return (Session) SerializationUtils.deserialize(value);
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        saveSession(session);
    }

    @Override
    public void delete(Session session) {
        if(session!=null&&session.getId()!=null){
            byte[] key=getKey(session.getId().toString());
            jedisUtil.del(key);
        }
    }

    @Override
    public Collection<Session> getActiveSessions() {
        //获取指定存活的key
        Set<byte[]> keys=jedisUtil.keys(SHIRO_SESSION_PREFIX);
        Set<Session> sessions=new HashSet<Session>();
        if(keys.isEmpty()){
            return sessions;
        }

        for(byte[] key:keys){
            Session session=(Session)SerializationUtils.deserialize(jedisUtil.get(key));
            sessions.add(session);
        }
        return sessions;
    }
}
