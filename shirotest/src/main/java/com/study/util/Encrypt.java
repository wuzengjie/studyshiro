package com.study.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 *  常见问题
 ① JWT 安全嗎?
 Base64编码方式是可逆的，也就是透过编码后发放的Token内容是可以被解析的。一般而言，我们都不建议在有效载荷内放敏感讯息，比如使用者的密码。

 ② JWT Payload 內容可以被伪造嗎？
 JWT其中的一个组成内容为Signature，可以防止通过Base64可逆方法回推有效载荷内容并将其修改。因为Signature是经由Header跟Payload一起Base64组成的。

 ③ 如果我的 Cookie 被窃取了，那不就表示第三方可以做 CSRF 攻击?
 是的，Cookie丢失，就表示身份就可以被伪造。故官方建议的使用方式是存放在LocalStorage中，并放在请求头中发送。

 ④ 空间及长度问题？
 JWT Token通常长度不会太小，特别是Stateless JWT Token，把所有的数据都编在Token里，很快的就会超过Cookie的大小（4K）或者是URL长度限制。

 ⑤ Token失效问题？
 无状态JWT令牌（Stateless JWT Token）发放出去之后，不能通过服务器端让令牌失效，必须等到过期时间过才会失去效用。
 假设在这之间Token被拦截，或者有权限管理身份的差异造成授权Scope修改，都不能阻止发出去的Token失效并要求使用者重新请求新的Token。
 *
 *
 *
 * 生成token
 */
public class Encrypt {

    /**
     *
     * @param isVip 是不是VIP,true表示是VIP，false表示不是VIP。
     * @param username 用户名
     * @param name  姓名
     * @return token
     */
    public String getToken(final boolean isVip, final String username,
                           final String name) {
        String token = null;
        try {
            Date expiresAt = new Date(System.currentTimeMillis() + 24L * 60L * 3600L * 1000L);
            token = JWT.create()
                    .withIssuer("auth0")
                    .withClaim("isVip", isVip)
                    .withClaim("username", username)
                    .withClaim("name", name)
                    .withExpiresAt(expiresAt)
                    // mysecret,使用算法对数据进行签名，有效防止数据被修改
                    //HS256 (带有 SHA-256 的 HMAC 是一种对称算法, 双方之间仅共享一个 密钥。由于使用相同的密钥生成签名和验证签名, 因此必须注意确保密钥不被泄密。
                    .sign(Algorithm.HMAC256("mysecret"));
        } catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return token;
    }
}
/**
 *JWT签名算法中，一般有两个选择，一个采用HS256,另外一个就是采用RS256。
 签名实际上是一个加密的过程，生成一段标识（也是JWT的一部分）作为接收方验证信息是否被篡改的依据。
 RS256 (采用SHA-256 的 RSA 签名) 是一种非对称算法, 它使用公共/私钥对: 标识提供方采用私钥生成签名, JWT 的使用方获取公钥以验证签名。由于公钥 (与私钥相比) 不需要保护, 因此大多数标识提供方使其易于使用方获取和使用 (通常通过一个元数据URL)。
 另一方面, HS256 (带有 SHA-256 的 HMAC 是一种对称算法, 双方之间仅共享一个 密钥。由于使用相同的密钥生成签名和验证签名, 因此必须注意确保密钥不被泄密。
 在开发应用的时候启用JWT，使用RS256更加安全，你可以控制谁能使用什么类型的密钥。另外，如果你无法控制客户端，无法做到密钥的完全保密，RS256会是个更佳的选择，JWT的使用方只需要知道公钥。
 由于公钥通常可以从元数据URL节点获得，因此可以对客户端进行进行编程以自动检索公钥。如果采用这种方式，从服务器上直接下载公钥信息，可以有效的减少配置信息。
 。
 */