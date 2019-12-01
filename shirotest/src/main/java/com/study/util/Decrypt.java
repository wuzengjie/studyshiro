package com.study.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;

/**
 * 解析token
 */
public class Decrypt {
    /**
     * 先验证token是否被伪造，然后解码token。
     * @param token 字符串token
     *
     *  如何数据出现伪造那么必定是不同的签名，那么在这种必定会抛出异常。
     *  其实是因为他并不知道这边进行加密签名的密钥是什么，所以在伪造数据此处的解密签名就会不通过
     */
    public DecodedJWT deToken(final String token) {
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256("mysecret"))
                    .withIssuer("auth0")
                    .build(); //Reusable verifier instance
            jwt = verifier.verify(token);
        } catch (JWTVerificationException exception){
            //Invalid signature/claims
            exception.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jwt;
    }
}
