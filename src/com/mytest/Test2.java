package com.mytest;

import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.config.ForestConfiguration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.mytest.annotation.vo.OneUtil;
import com.mytest.forest.MyClient;
import com.weavernorth.gqzl.BeisenSSO.oidcsdk.models.Jwt_header;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import weaver.general.MD5;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.util.List;
import java.util.Map;
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
    public void test8() throws Exception {

        String outPath = "E:\\WEAVER\\cus_file\\2021\\04\\12\\9fb5759f-a631-4423-a55b-495fde4750b9.zip";

        String str = "e:\\WEAVER\\ecology\\filesystem\\202104\\X\\9fb5759f-a631-4423-a55b-495fde4750b9.zip";
        String str1 = "[\"Java异常体系.png#nzm#E:\\\\WEAVER\\\\cus_file\\\\2021\\\\04\\\\12\\\\6c1f3b74-b523-4df5-aedd-94d90a57da19.zip\",\"wait原理.png#nzm#E:\\\\WEAVER\\\\cus_file\\\\2021\\\\04\\\\12\\\\051b3118-0717-43b7-8001-33e05ec4a350.zip\"]";
        List<String> list = JSONObject.parseObject(str1, List.class);
//        for (String path : list) {
//            String[] split = path.split("#nzm#");
//            String fileName = split[0];
//            String pathStr = split[1];
//            System.out.println(fileName + ", " + pathStr);
//        }

        String s = "4dfaa437-2432-4c08-bf33-4eec303f5627.zip";
        int i = s.lastIndexOf(".");
        String substring = s.substring(0, i);
        System.out.println(substring);

    }

    @Test
    public void test9() {
        String strFileRealPath = "[\"mk7.jpg#nzm#D:\\Weaver2019_base\\cus_file\\2021\\04\\16\\7edd9df6-fb89-46eb-8687-8aebd0ddd52e.zip\"]";
        List<String> list = JSONObject.parseObject(strFileRealPath, List.class);
        for (String path : list) {
            String[] split = path.split("#nzm#");
            String fileName = split[0]; // 压缩文件真实名  Java异常体系.png
            String pathStr = split[1]; // 压缩文件全路径 E:\WEAVER\cus_file\2021\04\12\4dfaa437-2432-4c08-bf33-4eec303f5627.zip
            System.out.println(fileName);
            System.out.println(pathStr);
        }
    }

    @Test
    public void test10() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, "localhost", 6379, 10, "123456");

        Jedis resource = jedisPool.getResource();
        resource.set("sex", "男");
        resource.close();
        //连接本地的 Redis 服务
//        Jedis jedis = new Jedis("localhost");
//        jedis.auth("123456");
//        String select = jedis.select(1);
//        System.out.println(select);
//        String ping = jedis.ping();
//        System.out.println(ping);


//        String s = jedis.flushDB();
//        System.out.println("flushDB结果： "+ s);

//        for (int i = 0; i < 13; i++) {
//            jedis.set("age" + i, "27岁");
//        }


//        jedis.lpush("studentNo", "nzm");
//        jedis.lpush("studentNo", "nzm");
//        jedis.lpush("studentNo", "nzm1");
//
//        Long studentNo = jedis.llen("studentNo");
//        System.out.println("数量： "+ studentNo);
//        List studentNo1 = jedis.lrange("studentNo", 0, studentNo);
//        System.out.println(JSONObject.toJSONString(studentNo1));

        //jedis.expire("studentNo", 10);


        //jedis.close();
    }


    /**
     * @param str     原字符串
     * @param sToFind 需要查找的字符串
     * @return 返回在原字符串中sToFind出现的次数
     */
    private int countStr(String str, String sToFind) {
        int num = 0;
        while (str.contains(sToFind)) {
            str = str.substring(str.indexOf(sToFind) + sToFind.length());
            num++;
        }
        return num;
    }

    @Test
    public void test11() {

        BigDecimal bigDecimal = new BigDecimal("1");
        BigDecimal bigDecimal2 = new BigDecimal("6");
        BigDecimal divide = bigDecimal.divide(bigDecimal2, 4, BigDecimal.ROUND_HALF_UP);
        String s = divide.toString();
        System.out.println(s);

    }

    @Test
    public void test12() throws Exception {
        OneUtil abc = new OneUtil();

        abc.setName("123");


    }

}

