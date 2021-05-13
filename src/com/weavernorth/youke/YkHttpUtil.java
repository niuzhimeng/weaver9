package com.weavernorth.youke;

import okhttp3.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class YkHttpUtil {
    private static final Log LOGGER = LogFactory.getLog(YkHttpUtil.class);
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType FROM_DATA = MediaType.parse("multipart/form-data");
    private static final OkHttpClient client = new OkHttpClient();

    /**
     * post请求发送json + header
     */
    public static String postJsonHeader(String url, String jsonStr, Map<String, String> headerMap) {
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
     * post请求发送文件 + 请求头
     *
     * @param url       请求地址
     * @param file      传输的文件
     * @param headerMap 请求头
     * @return
     */
    public static String postFileHeader(String url, File file, Map<String, String> headerMap) {
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        MultipartBody body = new MultipartBody.Builder()
                .setType(FROM_DATA)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                .post(body)
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
