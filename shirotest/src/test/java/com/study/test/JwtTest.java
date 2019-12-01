package com.study.test;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.study.util.Decrypt;
import com.study.util.Encrypt;
import org.junit.Test;

public class JwtTest {

    @Test
   public void testJwt(){
// 生成token
        Encrypt encrypt = new Encrypt();
        String token = encrypt.getToken(true, "zhangchao", "张超");

        // 打印token
        System.out.println("token: " + token);

        // 解密token
        Decrypt decrypt = new Decrypt();
        DecodedJWT jwt = decrypt.deToken(token);

        System.out.println("issuer: " + jwt.getIssuer());
        System.out.println("isVip:  " + jwt.getClaim("isVip").asBoolean());
        System.out.println("username: " + jwt.getClaim("username").asString());
        System.out.println("name:     " + jwt.getClaim("name").asString());
        System.out.println("过期时间：      " + jwt.getExpiresAt());

   }
}
