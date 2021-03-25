package com.weavernorth.gqzl.BeisenSSO.oidcsdk.provider;

import com.weavernorth.gqzl.BeisenSSO.oidcsdk.crypto.Crypto;
import com.weavernorth.gqzl.BeisenSSO.oidcsdk.models.Jwt;
import com.weavernorth.gqzl.BeisenSSO.oidcsdk.models.Jwt_header;
import com.weavernorth.gqzl.BeisenSSO.oidcsdk.models.Jwt_payload;
import com.weavernorth.gqzl.BeisenSSO.oidcsdk.utility.SafeTools;

import java.util.HashMap;

public class BeisenTokenProvider {
    /**
     * 跳转地址
     */
    public final static String iss = "119.3.201.175/wui/main.jsp?templateId=1";

    /**
     * 租户ID
     */
    public final static String aud = "109320";

    /**
     * 公钥
     */
    public final static String public_key = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIDANBgkqhkiG9w0BAQEFAAOCAQ0AMIIBCAKCAQEAzFd5G4EhYs1qDfpHzmqj\n" +
            "17uHCj/vWRine0T5kMzVM6US9Hs3YzM5YZ7soVRo1etZyDNOwtCyALREW9L3HO79\n" +
            "MKpfMSW/d13H0BrO55izs2hXaAB0ZCZ2XPTZRAuScNDgDtQ0Pi/9/qNQn0Ef0MyP\n" +
            "nYu783xXEa4BU22aw128q6VKKf0xiXR8v7+Xs23JSfRVGlOZIGsKUFOG/OWZQ2jw\n" +
            "IQ09c2gnENvRIjz5niq7fXQjofizUK4bX9htei5U1ckdyro3F/Z7I8xw5d4sawa5\n" +
            "FNxzqO4w4xJ4IqeONJ10zuPlEsnJFMXIEVFpJivD9t+cWNsPWtpeU7jei1xlAuFk\n" +
            "8wIBAw==\n" +
            "-----END PUBLIC KEY-----";

    /**
     * 私钥
     */
    public final static String private_key = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEowIBAAKCAQEAzFd5G4EhYs1qDfpHzmqj17uHCj/vWRine0T5kMzVM6US9Hs3\n" +
            "YzM5YZ7soVRo1etZyDNOwtCyALREW9L3HO79MKpfMSW/d13H0BrO55izs2hXaAB0\n" +
            "ZCZ2XPTZRAuScNDgDtQ0Pi/9/qNQn0Ef0MyPnYu783xXEa4BU22aw128q6VKKf0x\n" +
            "iXR8v7+Xs23JSfRVGlOZIGsKUFOG/OWZQ2jwIQ09c2gnENvRIjz5niq7fXQjofiz\n" +
            "UK4bX9htei5U1ckdyro3F/Z7I8xw5d4sawa5FNxzqO4w4xJ4IqeONJ10zuPlEsnJ\n" +
            "FMXIEVFpJivD9t+cWNsPWtpeU7jei1xlAuFk8wIBAwKCAQAiDpQvQDA7IjxXqbai\n" +
            "ZxtOn0EsX/05hBvp4NRCzM4zRi3Tad6QiImQRSdwOLwjpzmhXeJ1zXMAHgtkoykv\n" +
            "fSoyxw/dhkqT5PairyJ77sid5rk8ABNmBmkPfiQ2Ae29eCVXzgi1B/+qcI1v4C/4\n" +
            "Ihfvl0n96g6C8lWN55nLOkocm0AZ5zlrNwxZJ6e3BtH3I6aVJgDB+KkQQS+q8A3X\n" +
            "Jv3Gc/r5CcAE/HSYjg0G4IDJR/3f+8Kz9KnYgaN2L9gIRCwOSeY9y2J2+JGN5pfG\n" +
            "tDQUEigu8kJTWH/NbBnCk+udYNljrqsnHHR1VYph540EhyeG3paL0AVQYD+mxE8o\n" +
            "dcOjAoGBAOoUi6DPttrHrWHPFBVoeIE39Pomm7fLvl/6qphrhxqU+cfJ4YzKG6ml\n" +
            "bD0j2edmiUqGB5CsQn3hRjJco9LroYxL7FgIkfiqzdpMRJbnZ4dHhUF8IrG7C+rb\n" +
            "/8NdNi3f8fSVRYf/0mb1gXCm98yI4Dem/AOhTbVN8APv5XSVnTSbAoGBAN96Bjg2\n" +
            "c1fhJEeaMMgWpexjeppt+1wjDgkAmq3M9+LlW5PTTBoo1WvpfG5RDc9fOuXcCpJs\n" +
            "r6JriGZYa3G4jokpEnmcoDOvKy1GMgbbZflU6kETHfEz8452HzBH78E+rNgnbD4q\n" +
            "SKZbzrozxqlS1HeIp4RyrIkjh1yy3RV6gZqJAoGBAJwNsms1JJHac5aKDWOa+wDP\n" +
            "+KbEZ8/dKZVRxxBHr2cN+9qGll3cEnEY8tNtO++ZsNxZWmBy1v6WLsw9woydFl2H\n" +
            "8uVbC/sciTwy2GSaRQTaWNZSwcvSB/Hn/9eTeXPqoU242QVVNu9OVksZ+ohbQCUZ\n" +
            "/VfA3njeoAKf7k25E3hnAoGBAJT8BCV5ojqWGC+8IIVkbp2XpxGep5LCCVtVvHPd\n" +
            "+pdDkmKM3WbF451GUvQ2CTTqJ0PoBwxIdRbyWu7lnPZ7CbDGDFETFXfKHMjZdq88\n" +
            "7qY4nCtiE/Yiol75aiAv9St/HeVvnX7G2xmSidF32cY3OE+wb62hyFttBOh3Pg5R\n" +
            "q7xbAoGBAIKEvPLJofxxNEdb5+LIYOuj7BM30uo1XFk2WW5homf8hwtxvCW4cN3R\n" +
            "RtrPM02Oa8tQzwAuA0KyD8JrkrGK+sTJLwfxbOWsLCwiwKZ/K0y1PUnS6kLbj0OM\n" +
            "4Jp2EYBgTbDwYOqaIissw9IPQDMofN9/oADD5Du43yYVdVGAz00P\n" +
            "-----END RSA PRIVATE KEY-----";

    /**
     * 默认编码格式使用统一指定字符集 UTF-8
     */
    public final static String encoding = "utf-8";

    /**
     * 初始化header json
     *
     * @param alg
     * @param public_key
     * @return
     * @throws Exception
     */
    public static String GetHeader(String alg, String public_key) throws Exception {
        String kid = GetKid(public_key);
        Jwt_header header = new Jwt_header(alg, kid);
        return header.toString();
    }

    /**
     * 初始化 payload json
     *
     * @param iss
     * @param sub
     * @param aud
     * @param exp
     * @param iat
     * @param cls
     * @return
     * @throws Exception
     */
    public static String GetPayload(String iss, String sub, String aud, long exp, long iat, HashMap<String, Object> cls) throws Exception {
        Jwt_payload payload = new Jwt_payload(iss, sub, aud, exp, iat, cls);
        return payload.toString();
    }

    /**
     * 获取id_token
     *
     * @param header      jwt header json
     * @param payload     jwt payload json
     * @param private_key
     * @return url safe base64 编码的 jwt token
     * @throws Exception
     */
    public static String GetIdToken(String header, String payload, String private_key) throws Exception {
        String[] jwt = new String[3];
        jwt[0] = java.util.Base64.getEncoder().encodeToString(header.getBytes(encoding));
        jwt[1] = java.util.Base64.getEncoder().encodeToString(payload.getBytes(encoding));
        jwt[2] = Crypto.Sign(String.format("%s.%s", jwt[0], jwt[1]), private_key);
        // 转为 url safe base64
        for (int i = 0; i < 3; i++) {
            jwt[i] = SafeTools.Base64StringToSafeBase64(jwt[i]);
        }
        return String.format("%s.%s.%s", jwt[0], jwt[1], jwt[2]);
    }

    /**
     * 获取公钥hash  值用于对比公钥信息与服务器端是否一致
     *
     * @param public_key
     * @return
     * @throws Exception
     */
    public static String GetKid(String public_key) throws Exception {
        return new Crypto().sha256Encode(public_key.replaceAll("\r|\n", ""));
    }

    /**
     * 生成idtoken
     * <p>
     * iss         Issuer Identifier：必须。提供认证信息者的唯一标识。一般是一个https的url（不包含querystring和fragment部分）。
     * sub         Subject Identifier：必须。iss提供的EU的标识，在iss范围内唯一。它会被RP用来标识唯一的用户。最长为255个ASCII个字符。
     * aud         Audience(s)：必须。标识ID Token的受众。必须包含OAuth2的client_id。
     * private_key
     * public_key
     *
     * @return jwt 格式token
     * @throws Exception
     */
    public static String GenerateBeisenIDToken(String sub, String url_type) throws Exception {
        String header = GetHeader("RS256", public_key);
        HashMap<String, Object> cls = new HashMap<String, Object>();// 用户自定义可见权限内容列表
        cls.put("appid", "100");
        cls.put("uty", "email");
        cls.put("url_type", url_type);
        cls.put("isv_type", "0");
        long iat = SafeTools.getNowTimeStamp();// Issued At Time：必须。JWT的构建的时间【Unix时间】。
        long exp = iat + 60 * 60 * 24; // Expiration time：必须。过期时间，超过此时间的ID Token会作废不再被验证通过【Unix时间】。此处使用24小时后
        String payload = GetPayload(iss, sub, aud, exp, iat, cls);
        return GetIdToken(header, payload, private_key);
    }

    /**
     * 验签token
     *
     * @param id_token   获得的 id_token
     * @param public_key
     * @return 当返回的jwt 不为空 且验证方法未见异常信息时 表示验证通过 jwt内容可信任
     * @throws Exception
     */
    public static Jwt VerifySign(String id_token, String public_key) throws Exception {
        // 实例化idtoken
        Jwt jwt = new Jwt(id_token);
        // 签名方式是否是RSA-SHA256
        if (!"RS256".equals(jwt.getHeader().getAlg())) {
            throw new Exception("错误的签名方式！");
        }
        // 验证是否过期
        if (SafeTools.ToLong(jwt.getPayload().getExp(), 0L) < SafeTools.getNowTimeStamp()) {
            throw new Exception("IDToken 已过期，请重新获取");
        }
        String kid = GetKid(public_key);
        if (!kid.equals(jwt.getHeader().getKid().toLowerCase())) {
            throw new Exception("公钥校验错误！");
        }
        // 验证签名
        if (!Crypto.Verify(jwt.getSignContent(), jwt.getRaw_signature(), public_key)) {
            throw new Exception("签名验证错误！");
        }
        // TODO: 业务内逻辑添加

        return jwt;
    }
}
