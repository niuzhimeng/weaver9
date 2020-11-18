package com.weavernorth.meitan.util;

import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 煤炭安全监管系统AES
 */
public class AnJianAes {

    @Test
    public void test1() throws UnsupportedEncodingException {

        String jsonStr = "{\"MenuType\":null,\"LoginName\":\"shmk_admin\",\"Password\":\"123456\"}";
        String encryptStr = encrypt(jsonStr);
        encryptStr = URLEncoder.encode(encryptStr, "utf-8");
        System.out.println(encryptStr);

    }

    public String encrypt(String src) {
        byte[] rgbIV = {49, 99, 105, 53, 99, 114, 110, 100, 97, 54, 111, 106, 122, 103, 116, 114};
        try {
            // 加密
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(rgbIV);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec("Password12345678".getBytes(StandardCharsets.UTF_8), "AES"), iv);
            byte[] result = cipher.doFinal(src.getBytes());

            return Base64.getEncoder().encodeToString(result); //通过Base64转码返回
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
