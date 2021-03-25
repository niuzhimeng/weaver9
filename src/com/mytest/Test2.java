package com.mytest;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weavernorth.gqzl.BeisenSSO.oidcsdk.models.Jwt_header;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Test;
import weaver.general.MD5;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;

public class Test2 {

    private static final OkHttpClient client = new OkHttpClient();

    @Test
    public void test1() throws UnsupportedEncodingException {
        String url = "http://localhost:8080/api/GetDataTestApi/getDataTest";
//        String s = postKeyValue("localhost:8080/api/workflow/paService/getToDoWorkflowRequestList");
//        System.out.println(s);

        String s = get(url, null);
        System.out.println(s);
    }

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
            System.out.println(("http请求get异常： " + e));
        }

        return returnStr;
    }

    public static String postKeyValue(String url, Map<String, String> bodyMap, Map<String, String> headerMap) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (bodyMap != null) {
            bodyMap.forEach(formBodyBuilder::add);
        }

        Request.Builder requestBuilder = new Request.Builder()
                .post(formBodyBuilder.build())
                .url(url);

        // 添加请求头
        if (headerMap != null) {
            headerMap.forEach(requestBuilder::addHeader);
        }

        try {
            Response response = client.newCall(requestBuilder.build()).execute();
            return response.body().string();
        } catch (IOException e) {
            System.out.println(("http请求postJsonHeader异常： " + e));
        }

        return "";
    }

    @Test
    public void test3() throws UnsupportedEncodingException {
        String s = new String(Base64.encode("123".getBytes("utf-8")));
        String s2 = java.util.Base64.getEncoder().encodeToString("123".getBytes());

        System.out.println(s);
        System.out.println(s2);
        Gson gson = new Gson();

        gson.fromJson("str", new TypeToken<Jwt_header>() {
        }.getType());


    }

    public static void main(String[] args) {
        DanLiEnum instance = DanLiEnum.INSTANCE;
        instance.sysOk();

        BigDecimal zero = BigDecimal.ZERO;


    }

    @Test
    public void test4() throws UnsupportedEncodingException {
        String loginId = "nzm";
        long timestamp = System.currentTimeMillis();
        String sign = new MD5().getMD5ofStr(loginId + "OA_HD_TODO" + timestamp);

        System.out.println(timestamp);
        System.out.println(sign);

    }

    public static String stringToMD5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        StringBuilder md5code = new StringBuilder(new BigInteger(1, secretBytes).toString(16));
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code.insert(0, "0");
        }
        return md5code.toString().toUpperCase();
    }

    /**
     * 单一职责
     * 接口隔离
     * 开闭原则
     * 里氏代换原则
     * 依赖倒置
     * 迪米特法则
     */
    @Test
    public void test5() {
        try {
//            TimeUnit.SECONDS.sleep(5);


            Stack stack = new Stack();
            boolean add = stack.add(1);


            Map<String, String> map = new HashMap<>();
            map.put("1", "1");

            "".hashCode();
            int i = 0;
            int num = 0;
            do {
                num += i;
                i++;
            } while (i <= 100);
            System.out.println(num);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test6() {
        MyObj o = new MyObj();
        System.out.println(o.getClass().getClassLoader());
        System.out.println(o.getClass().getClassLoader().getParent());
        System.out.println(o.getClass().getClassLoader().getParent().getParent());

        Thread thread = new Thread();
        thread.interrupted();
        Thread.interrupted();

        ReentrantLock reentrantLock = new ReentrantLock();
//        reentrantLock.lockInterruptibly();
    }

    class MyObj {
    }

    @Test
    public void test7() {
        Socket socket = new Socket();

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
        for (long id : deadlockedThreads) {
            ThreadInfo threadInfo = threadMXBean.getThreadInfo(id);
            System.out.println("死锁了： " + threadInfo.getThreadName());
        }
    }

    @Test
    public void test8() throws NacosException {


    }


}

class MyResource {
    private int i;
    private void add1(){

    }

}