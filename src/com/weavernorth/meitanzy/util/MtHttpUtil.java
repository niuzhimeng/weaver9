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

    public static String get(String url) {
        String returnStr = "";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();
        try {
            Response response = client.newCall(request).execute();
            returnStr = response.body().string();
        } catch (IOException e) {
            LOGGER.error("http请求get异常： " + e);
        }

        return returnStr;
    }


}
