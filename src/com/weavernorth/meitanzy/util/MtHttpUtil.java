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
     * post请求发送json + header
     */
    public static String postJsonHeader(String url, String jsonStr, Map<String, String> headerMap) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        Request.Builder requestBuilder = new Request.Builder()
                .post(requestBody)
                .url(url);
        // 添加请求头
        if (headerMap != null) {
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

    /**
     * post请求发送 header
     */
    public static String postHeader(String url, Map<String, String> headerMap) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(null, "");
        Request.Builder requestBuilder = new Request.Builder()
                .post(requestBody)
                .url(url);
        // 添加请求头
        if (headerMap != null) {
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

    /**
     * post请求发送 键值对
     */
    public static String postKeyValue(String url, Map<String, String> bodyMap) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (bodyMap != null) {
            bodyMap.forEach(formBodyBuilder::add);
        }

        Request.Builder requestBuilder = new Request.Builder()
                .post(formBodyBuilder.build())
                .url(url);

        try {
            Response response = client.newCall(requestBuilder.build()).execute();
            return response.body().string();
        } catch (IOException e) {
            LOGGER.error("http请求postJsonHeader异常： " + e);
        }

        return "";
    }

    /**
     * get请求 带请求头
     */
    public static String get(String url, Map<String, String> headerMap) {
        String returnStr = "";
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder().url(url).get();
        if (headerMap != null) {
            headerMap.forEach(builder::addHeader);
        }
        try {
            Response response = client.newCall(builder.build()).execute();
            returnStr = response.body().string();
        } catch (IOException e) {
            LOGGER.error("http请求get异常： " + e);
        }

        return returnStr;
    }


}
