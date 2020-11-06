package com.weavernorth.meitan.util;

import com.alibaba.fastjson.JSONObject;
import weaver.general.SecurityHelper;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 网报系统
 * @author puqi
 * @version 1.0
 * @description Aes加密工具类
 * @date 2020/10/20-10:18
 */
public class AesEncryptUtil {

    private static int blockSize = 128;
    private static int minSize = 128;
    private static int maxSize = 256;
    private static int skipSize = 64;

    public static void main(String[] args) {
//        String password = "MWrZc3s4nXg=QjRGUSZ3PP4=";
//        password = SecurityHelper.decryptSimple(password);
//        System.out.println(password);
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("account", "SsoTest");
        jsonObject.put("password", "SsoTest@123");
        String jsonStr = jsonObject.toJSONString();
        System.out.println(jsonStr);
        String secretKey = "smartdot&hd@9999";

        String encryptStr = encrypt(jsonStr, secretKey);
        System.out.println(encryptStr);
    }

    public static String encrypt(String stringIn, String key) {
        byte[] byteIn = stringIn.getBytes(StandardCharsets.UTF_8);
        byte[] byteOut = encrypt(byteIn, key);
        return Base64.getEncoder().encodeToString(byteOut);
    }

    public static byte[] encrypt(byte[] byteIn, String key) {
        byteIn = appendBottomZero(byteIn, blockSize);
        byte[] keyByte = getLegalKey(key);
        byte[] vector = getLegalIV(key);
        return encrypt(byteIn, keyByte, vector);
    }

    public static byte[] encrypt(byte[] byteIn, byte[] key, byte[] vector) {
        try {
            SecretKeySpec aesSecretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(1, aesSecretKeySpec, new IvParameterSpec(vector));
            return cipher.doFinal(byteIn);
        } catch (Exception var6) {
            var6.printStackTrace();
            return null;
        }
    }

    private static byte[] getLegalKey(String key) {
        String resultString;
        int length = key.length() * 8;
        if (length <= minSize) {
            resultString = String.format("%-" + minSize / 8 + "s", key);
        } else if (length <= maxSize) {
            length = ((length - 1) / skipSize + 1) * skipSize;
            resultString = String.format("%-" + length / 8 + "s", key);
        } else {
            resultString = key.substring(0, maxSize / 8);
        }
        return resultString.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] getLegalIV(String key) {
        if (key.length() > blockSize / 8) {
            key = key.substring(0, blockSize / 8);
        }
        return String.format("%-" + blockSize / 8 + "s", key).getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] appendBottomZero(byte[] bytes, int blockSize) {
        int length = bytes.length;
        blockSize /= 8;
        if (length % blockSize != 0) {
            length += blockSize - length % blockSize;
        }
        byte[] result = new byte[length];
        System.arraycopy(bytes, 0, result, 0, bytes.length);
        return result;
    }
}
