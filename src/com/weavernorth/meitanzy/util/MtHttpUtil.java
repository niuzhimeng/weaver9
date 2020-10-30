package com.weavernorth.meitanzy.util;

import okhttp3.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Map;

public class MtHttpUtil {
    private static final Log LOGGER = LogFactory.getLog(MtHttpUtil.class);
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * post请求，附加请求体
     */
    public static String postBody(String url, Map<String, String> requestBodyMap) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        if (requestBodyMap != null) {
            requestBodyMap.forEach(builder::add);
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            LOGGER.error("http请求postBody异常： " + e);
        }

        return "";
    }

    /**
     * post请求发送json + header
     */
    public static String postJsonHeader(String url, String jsonStr, Map<String, String> headerMap) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        Request.Builder requestBuilder = new Request.Builder()
                .post(requestBody)
                .url(url);
        // 添加请求头
        if(headerMap != null){
            headerMap.forEach(requestBuilder::addHeader);
        }
        try {
            Response response = client.newCall(requestBuilder.build()).execute();
            return response.body().string();
        } catch (IOException e) {
            LOGGER.error("http请求postJsonHeader异常： " + e);
        }

        return "";
    }


}
