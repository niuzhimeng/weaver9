package com.weavernorth.createFlow.util;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;

public class OkHttpUtil {
    /**
     * 参数拼接到请求头
     *
     * @param url
     * @param map
     * @return
     */
    public static String okPost(String url, Map<String, String> map) {
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
            returnStr = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnStr;
    }

    /**
     * 发送请求body+请求头
     *
     * @param url    地址
     * @param body   请求体
     * @param herder 请求头
     * @return
     */
    public static String okPostBodyHeader(String url, Map<String, String> body, Map<String, String> herder) {
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        body.forEach(bodyBuilder::add);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(bodyBuilder.build());
        herder.forEach(builder::header);
        Request request = builder.build();

        Call call = okHttpClient.newCall(request);
        String returnStr = "";
        try {
            //同步调用,返回Response,会抛出IO异常
            Response response = call.execute();
            returnStr = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnStr;
    }
}
