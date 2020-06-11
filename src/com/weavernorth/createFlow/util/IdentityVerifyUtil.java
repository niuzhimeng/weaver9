package com.weavernorth.createFlow.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import weaver.rsa.security.RSA;

import java.util.HashMap;
import java.util.Map;

/**
 * description :
 * author ：JHY
 * date : 2020/6/3
 * version : 1.0
 */
public class IdentityVerifyUtil {
    public static final String APPID = "EEAA5436-7577-4BE0-8C6C-89E9D88805EA";
    public static final String HOST = "http://127.0.0.1:8080";
    //系统公钥信息
    private String SPK = null;
    //秘钥信息
    private String SECRET = null;

    private static IdentityVerifyUtil instance;

    public static synchronized IdentityVerifyUtil getInstance() {
        if (instance == null) {
            instance = new IdentityVerifyUtil();
            instance.regist();
        }
        return instance;
    }

    private IdentityVerifyUtil() {
    }


    private void regist() {
        //请求头信息封装集合
        Map<String, String> heads = new HashMap<String, String>();
        //获取当前异构系统RSA加密的公钥
        String cpk = RSA.getRSA_PUB();
        //当前异构系统用于向ECOLOGY注册时使用的账号密码通过DES加密后密文进行传输
        //kb1906及以上版本 已废弃账号密码校验
        //封装参数到请求头
        heads.put("appid", APPID);
        heads.put("cpk", cpk);
        //调用ECOLOGY系统接口进行注册
        try {
            String data = OkHttpUtil.okPost(HOST + "/api/ec/dev/auth/regist", heads);
            //返回的数据格式为json，具体格式参数格式请参考文末API介绍。
            //注意此时如果注册成功会返回秘钥信息,请根据业务需要进行保存。
            if (data != null) {
                JSONObject result = JSON.parseObject(data);
                if ("true".equals(result.getString("status"))) {
                    this.SPK = result.getString("spk");
                    this.SECRET = result.getString("secrit");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getToken() {
        //请求头信息封装集合
        Map<String, String> heads = new HashMap<String, String>();
        RSA rsa = new RSA();
        //对秘钥进行加密传输，防止篡改数据
        String secret = rsa.encrypt(null, SECRET, null, "utf-8", SPK, false);
        //封装参数到请求头
        heads.put("appid", APPID);
        heads.put("secret", secret);
        //调用ECOLOGY系统接口进行申请
        try {
            String data = OkHttpUtil.okPost(HOST + "/api/ec/dev/auth/applytoken", heads);
            if (data != null) {
                JSONObject res = JSON.parseObject(data);
                if ("true".equals(res.getString("status"))) {
                    return res.getString("token");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取请求头信息
     *
     * @param token
     * @param userid
     * @param spk
     * @return
     */
    public static Map<String, String> getHttpHeads(String token, String userid, String spk) {
        Map<String, String> heads = new HashMap<>();
        heads.put("token", token);
        heads.put("appid", IdentityVerifyUtil.APPID);
        RSA rsa = new RSA();
        String secretUserid = rsa.encrypt(null, userid, null, "utf-8", spk, false);
        heads.put("userid", secretUserid);
        return heads;
    }

    public String getSPK() {
        return SPK;
    }

    public String getSECRET() {
        return SECRET;
    }
}
