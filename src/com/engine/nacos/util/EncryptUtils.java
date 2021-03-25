package com.engine.nacos.util;

import com.engine.nacos.constant.EncryptConstant;
import com.engine.nacos.entity.EncryptEnum;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * 加密解密工具类
 */
public class EncryptUtils {


    /**
     * 编码或者加密 UTF-8 编码
     *
     * @param str         需要加密的字符串
     * @param encryptEnum 加密方式
     * @return
     */
    public static String encryptStr(String str, String encryptEnum) {
        if (StringUtils.isBlank(encryptEnum))
            return str;
        return encryptStr(str, EncryptEnum.getEncryptEnum(encryptEnum));
    }

    /**
     * 编码或者加密
     *
     * @param str         需要加密的字符串
     * @param encryptEnum 加密方式
     * @return
     */
    public static String encryptStr(String str, EncryptEnum encryptEnum) {
        if (encryptEnum == null)
            return str;
        if (EncryptEnum.URLEncoder == encryptEnum) {
            try {
                return URLEncoder.encode(str, EncryptConstant.UTF_8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (EncryptEnum.Base64 == encryptEnum)
            return codeBase64(str);
        else if (EncryptEnum.Hexadecimal == encryptEnum)
            return codeHexStr(str);
        return str;
    }


    /**
     * Base64加密 默认编码格式 为UTF-8
     *
     * @param str 需要加密的字符串
     * @return
     */
    public static String codeBase64(String str) {
        byte[] bytes = new byte[0];
        try {
            bytes = str.getBytes(EncryptConstant.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //Base64 加密
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Base64加密
     *
     * @param str         需要加密的字符串
     * @param charsetName 加密的编码方式 UTF-8 、 GBK
     * @return
     */
    public static String codeBase64(String str, String charsetName) {
        byte[] bytes = new byte[0];
        try {
            bytes = str.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //Base64 加密
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Base64解密 默认解密方式为UTF-8
     *
     * @param str 需要解密的字符串
     * @return
     */
    public static String decodeBase64(String str) {
        // Base64 解密
        byte[] decoded = Base64.getDecoder().decode(str);
        try {
            return new String(decoded, EncryptConstant.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;

    }


    /**
     * Base64解密
     *
     * @param str         需要解密的字符串
     * @param charsetName 解密的编码方式 UTF-8 、 GBK
     * @return
     */
    public static String decodeBase64(String str, String charsetName) {
        // Base64 解密
        byte[] decoded = Base64.getDecoder().decode(str);
        try {
            return new String(decoded, charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 将字符串加密成16进制 默认编码为 UTF-8
     *
     * @param str 需要加密的字符串
     * @return
     */
    public static String codeHexStr(String str) {
        char[] chars = EncryptConstant.HEXADECIMAL_NUMBER.toCharArray();
        StringBuilder sb = new StringBuilder();
        byte[] bs = new byte[0];
        try {
            bs = str.getBytes(EncryptConstant.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }


    /**
     * 将字符串加密成16进制 默认编码为 UTF-8
     *
     * @param str         需要加密的字符串
     * @param charsetName 加密的编码方式 UTF-8 、 GBK
     * @return
     */
    public static String codeHexStr(String str, String charsetName) {
        char[] chars = EncryptConstant.HEXADECIMAL_NUMBER.toCharArray();
        StringBuilder sb = new StringBuilder();
        byte[] bs = new byte[0];
        try {
            bs = str.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }

    /**
     * 将16进制字符串解密 默认编码为 UTF-8
     *
     * @param bytes 需要解密的字符串
     * @return
     */
    public static String decodeHexStr(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((EncryptConstant.HEXADECIMAL_NUMBER.indexOf(bytes.charAt(i)) << 4 | EncryptConstant.HEXADECIMAL_NUMBER
                    .indexOf(bytes.charAt(i + 1))));
        try {
            return new String(baos.toByteArray(), EncryptConstant.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将16进制字符串解密 默认编码为 UTF-8
     *
     * @param bytes       需要解密密的字符串
     * @param charsetName 解密密的编码方式 UTF-8 、 GBK
     * @return
     */
    public static String decodeHexStr(String bytes, String charsetName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((EncryptConstant.HEXADECIMAL_NUMBER.indexOf(bytes.charAt(i)) << 4 | EncryptConstant.HEXADECIMAL_NUMBER
                    .indexOf(bytes.charAt(i + 1))));
        try {
            return new String(baos.toByteArray(), charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


}
