package com.weavernorth.meitan.util;

import java.net.URLEncoder;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import sun.misc.BASE64Decoder;

/**
 * 合同系统
 * AES算法进行加密解密
 *
 * @author zhangyan
 * @create 2020-09-15 下午13:00
 **/
public class AesSecurityUtils {

    /**
     * 密钥
     */
    private static final String KEY = "smartdot&hd@9999";// AES加密要求key必须要128个比特位（这里需要长度为16，否则会报错）

    /**
     * 算法
     */
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    public static void main(String[] args) throws Exception {
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("account", "SsoTest");
        jsonObject.put("password", "SsoTest@123");
        String jsonStr = jsonObject.toJSONString();
        System.out.println("加密前：" + jsonStr);

        String encrypt = aesEncrypt(jsonStr, KEY);
        System.out.println("aes加密后：" + encrypt);
        System.out.println("url加密后： " + URLEncoder.encode(encrypt, "utf-8"));

//        String decrypt = aesDecrypt("GgNvw+rL+GnOZ3hKkY78zGxmnPk8Y6vhOlkbnOjRAafydk3n2wCFzzcubaWjFAFM");
//
//        System.out.println("解密后：" + decrypt);
//
//        Map<String, String> decryptS = decryptUserInfoBase64ByAES(encrypt);
//
//        System.out.println(decryptS);
//
//        boolean flag = isNotExpired(encrypt);
//
//        System.out.println(flag);
    }

    /**
     * base 64 encode
     *
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    private static String base64Encode(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    /**
     * base 64 decode
     *
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     * @throws Exception 抛出异常
     */
    private static byte[] base64Decode(String base64Code) throws Exception {
        return StringUtils.isEmpty(base64Code) ? null : new BASE64Decoder().decodeBuffer(base64Code);
    }


    /**
     * AES加密
     *
     * @param content    待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的byte[]
     */
    private static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));

        return cipher.doFinal(content.getBytes("utf-8"));
    }


    /**
     * AES加密为base 64 code
     *
     * @param content    待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的base 64 code
     */
    private static String aesEncrypt(String content, String encryptKey) throws Exception {
        return base64Encode(aesEncryptToBytes(content, encryptKey));
    }

    /**
     * AES解密
     *
     * @param encryptBytes 待解密的byte[]
     * @param decryptKey   解密密钥
     * @return 解密后的String
     */
    private static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);

        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

        return new String(decryptBytes);
    }


    /**
     * 将base 64 code AES解密
     *
     * @param encryptStr 待解密的base 64 code

     * @return 解密后的string
     */
    private static String aesDecrypt(String encryptStr) throws Exception {
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), KEY);
    }


    /**
     * 解密用户信息
     *
     * @param  经过Base64加密的待解密的内容
     * @return
     * @throws Exception
     */
    public static final Map<String, String> decryptUserInfoBase64ByAES(String encryptStr) throws Exception {
        String decryptByAES = aesDecrypt(encryptStr);
        Map<String, String> jsonMap = JSONObject.parseObject(decryptByAES, Map.class);
        return jsonMap;
    }

    /**
     * 判断是否过期
     *
     * @param
     * @return
     * @throws Exception
     */
    public static final boolean isNotExpired(String encryptStr) throws Exception {
        Map<String, String> map = decryptUserInfoBase64ByAES(encryptStr);
        String expires = map.get("expires");
        long expiresLong = Long.parseLong(expires);
        long currentTimeMillis = System.currentTimeMillis();
        return currentTimeMillis < expiresLong;
    }

}