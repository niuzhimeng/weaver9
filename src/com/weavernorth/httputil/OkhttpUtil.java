package com.weavernorth.httputil;

import okhttp3.*;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class OkhttpUtil {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    /**
     * GET 根据url下载文件
     *
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        Request.Builder builder = new Request.Builder()
                .url("http://111.160.2.51:8888/gysDoc/test.docx")
                .get();

        Request request = builder.build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        ResponseBody body = response.body();
        InputStream inputStream = body.byteStream();

        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\86157\\Desktop\\test.docx");
        byte[] bytes = new byte[1024];
        int i;
        while ((i = inputStream.read(bytes)) != -1) {
            fileOutputStream.write(bytes, 0, i);
        }

        inputStream.close();
        fileOutputStream.close();
    }

    @Test
    public void test2() throws IOException {
        String jsonStr = "{\n" +
                "\t\"account\":\"18100000000\",\n" +
                "\t\"key\":\"95F596A5-7B1A-11E8-A778-00FF140FC3616\"\n" +
                "}";

        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder()
                .post(requestBody)
                .url("http://ucapitest.tidepharm.com/api/account/verification")
                .build();
        Response response = client.newCall(request).execute();
        String string = response.body().string();
        System.out.println(string);

    }

    @Test
    public void test3() throws IOException {
        RequestBody requestBody = new FormBody.Builder()
                .add("Param name1", "Param value1")
                .add("Param name2", "Param value2").build();

        Request request = new Request.Builder()
                .post(requestBody)
                .url("http://ucapitest.tidepharm.com/api/account/verification")
                .build();
        Response response = client.newCall(request).execute();
        String string = response.body().string();
        System.out.println(string);
    }

}
