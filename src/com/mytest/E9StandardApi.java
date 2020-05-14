package com.mytest;

import com.alibaba.fastjson.JSONObject;
import com.mytest.vo.MyTest;
import okhttp3.*;
import org.junit.Test;
import weaver.rsa.security.RSA;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 发布e9标准接口调用方式
 */
public class E9StandardApi {
    // ECOLOGY_BIZ_EC表中的appid
    private String appId = "test1";

    /**
     * 1、注册服务
     */
    @Test
    public void zhuCe() {
        Map<String, String> heads = new HashMap<>();
        String cpk = RSA.getRSA_PUB();
        heads.put("appid", appId);
        heads.put("cpk", cpk);
        String data = okPost("http://127.0.0.1:8080/api/ec/dev/auth/regist", heads);
        System.out.println("注册接口返回： " + data);
        JSONObject jsonObject = JSONObject.parseObject(data);
        System.out.println("spk: " + jsonObject.getString("spk"));
        System.out.println("secrit: " + jsonObject.getString("secrit"));
    }

    /**
     * 2、获取token 有效期30分钟
     */
    @Test
    public void getToken() {
        //请求头信息封装集合
        Map<String, String> heads1 = new HashMap<String, String>();
        //ECOLOGY返回的系统公钥
        String spk = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjK1+RAMprQUHFIA0Q57TqkRn1oKQaDR6DzcGMkYSQG63hhE7fwpedxKjqS4GHb/XzM5FHUWYnTixSmgqjf9i6B5tukdLtzk/rHRbFXwW5oDVaNH8UCjLqnvd04fODQmcsgIidxmUSkqbICGYfZ2Oq3kuZzyKwFw8vgHwxCvcyui9k/Ukog61m6y1mcVbahsIVNe9hYHas+68mOOKi1JOe9nwukrngHJJt65x63fblEu3Cm7AtfkUHX4ogrNwAkANuiSDoq7gyabFlmd+OtXKMrvMo14XnJ6V38qbK6pzR7GcOMy8GTKg4yKvjTnUwuMZk+eFnCXi+r8cU2lMwnKZTQIDAQAB";
        String secrit = "1c8cc2f0-aab5-4b66-b7a3-7729b1e59c5c";
        RSA rsa = new RSA();
        //对秘钥进行加密传输，防止篡改数据
        String secret = rsa.encrypt(null, secrit, null, "utf-8", spk, false);
        heads1.put("appid", appId);
        heads1.put("secret", secret);
        String data1 = okPost("http://127.0.0.1:8080/api/ec/dev/auth/applytoken", heads1);
        System.out.println("返回token： " + data1);
        JSONObject jsonObject1 = JSONObject.parseObject(data1);
        System.out.println("token: " + jsonObject1.getString("token"));
    }

    /**
     * 3、调用接口
     */
    @Test
    public void execute() {
        String token = "3ad520be-e352-4641-b5e9-6ebf049f9cfe";
        //封装参数到请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("token", token);
        headers.put("appid", appId);
        headers.put("skipsession", "1");

        String s = okGet("http://localhost:8080/api/hrm/getDataTest", headers);
        System.out.println("接口返回： " + s);
    }

    private String okGet(String url, Map<String, String> header) {
        //1 构造Request
        Request.Builder builder = new Request.Builder()
                .get()
                .url(url);
        header.forEach(builder::addHeader);
        Request request = builder.build();
        //2 将Request封装为Call
        Call call = new OkHttpClient().newCall(request);
        //3 执行Call，得到response
        String returnStr = "";
        try {
            Response response = call.execute();
            System.out.println("返回码： " + response.code());
            returnStr = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnStr;
    }

    /**
     * 参数拼接到请求头
     *
     * @param url
     * @param map
     * @return
     */
    private String okPost(String url, Map<String, String> map) {
        OkHttpClient okHttpClient = new OkHttpClient();

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(new FormBody.Builder().build());
        map.forEach(builder::header);
        Request request = builder.build();

        Call call = okHttpClient.newCall(request);
        String returnStr = "";
        try {
            //同步调用,返回Response,会抛出IO异常
            Response response = call.execute();
            System.out.println(response.code());
            System.out.println(response.isSuccessful());
            returnStr = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnStr;
    }

    @Test
    public void testSSO() {
        // 获取token
        String url = "http://localhost:8080/ssologin/getToken";
        Map<String, String> params = new HashMap<>();
        params.put("appid", "Client1");
        params.put("loginid", "nzm"); // 登录名
        String s = okPostBody(url, params);
        System.out.println(s);
    }

    /**
     * 参数拼接到请求头
     *
     * @param url
     * @param map
     * @return
     */
    private String okPostBody(String url, Map<String, String> map) {
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody.Builder body = new FormBody.Builder();
        map.forEach(body::add);
        RequestBody formBody = body.build();

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(formBody);
        Request request = builder.build();
        Call call = okHttpClient.newCall(request);
        String returnStr = "";
        try {
            //同步调用,返回Response,会抛出IO异常
            Response response = call.execute();
            System.out.println(response.code());
            System.out.println(response.isSuccessful());
            returnStr = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnStr;
    }
}
