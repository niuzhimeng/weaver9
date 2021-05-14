package com.mytest;

import cn.hutool.http.HtmlUtil;
import com.alibaba.fastjson.JSONObject;
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
import java.util.*;
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
        String str = "{\"templateType\":\"Ecology8\",\"settingversion\":0,\"loginSettingInfo\":{\"loginForm\":{\"backgroundColor\":\"#0b1a32\",\"color\":\"#bacde0\",\"level\":\"center\",\"verticalMargin\":101,\"vertical\":\"middle\",\"showtype\":\"loginForm\",\"rate_x\":0,\"rate_y\":0,\"isLock\":true,\"win_width\":1920,\"win_height\":1080,\"x\":0,\"width\":324,\"isAbsolute\":\"0\",\"y\":0,\"levelMargin\":0,\"height\":292},\"qrcode\":{\"color\":\"#f5f7fa\",\"level\":\"center\",\"verticalMargin\":-269,\"vertical\":\"middle\",\"showtype\":\"qrcode\",\"rate_x\":0,\"rate_y\":0,\"isLock\":true,\"win_width\":1920,\"win_height\":1080,\"x\":0,\"width\":28,\"isAbsolute\":\"0\",\"y\":0,\"levelMargin\":198,\"height\":28},\"loginInfo\":{\"autoCarousel\":false,\"bgColor\":\"\",\"showBgImageBox\":true,\"width\":3000,\"carouselTime\":3,\"bgOpacity\":1,\"fillStyle\":\"stretch\",\"showQrcode\":true,\"imgsrc\":\"/wui/theme/ecology9/image/bg1.jpg\",\"height\":1875}},\"customElements\":[{\"original_width\":444,\"rotate\":0,\"level\":\"center\",\"verticalMargin\":0,\"vertical\":\"middle\",\"showtype\":\"loginbox\",\"type\":\"image\",\"rate_x\":0,\"content\":\"/wui/theme/ecology9/image/login-box.png\",\"rate_y\":0,\"isLock\":true,\"win_width\":1920,\"win_height\":1080,\"x\":0,\"width\":444,\"isAbsolute\":\"0\",\"y\":0,\"original_height\":587,\"levelMargin\":0,\"opacity\":100,\"height\":587},{\"original_width\":170,\"rotate\":0,\"level\":\"center\",\"verticalMargin\":-157,\"vertical\":\"middle\",\"showtype\":\"logo\",\"type\":\"image\",\"rate_x\":0,\"content\":\"/wui/theme/ecology9/image/e9.png\",\"rate_y\":0,\"isLock\":true,\"win_width\":1920,\"win_height\":1080,\"x\":0,\"width\":170,\"isAbsolute\":\"0\",\"y\":0,\"original_height\":130,\"levelMargin\":0,\"opacity\":100,\"height\":130}],\"labelInfo\":{\"langid7\":{\"fh\":\"返回\",\"rememberAccount\":\"记住账号\",\"rememberPassword\":\"记住密码\",\"forgetPassword\":\"忘记密码\",\"qrcode\":\"请使用e-mobile扫描二维码以登录\",\"validateCode\":\"请输入验证码\",\"refresh\":\"刷新\",\"djqhbj\":\"单击切换背景\",\"login\":\"登录\",\"backgroundtitle\":\"切换背景图\",\"password\":\"密码\",\"tokenKey\":\"动态令牌口令\",\"download\":\"点击下载客户端\",\"loginpoptitle\":\"由于长时间未操作，系统自动退出，需要重新登录\",\"lbsd\":\"轮播速度:\",\"resend\":\"重新发送\",\"smdl\":\"扫码登录\",\"zdlb\":\"自动轮播\",\"qrcodeisinvalid\":\"二维码已失效\",\"send\":\"获取动态密码\",\"account\":\"账号\"}},\"qrcode\":{\"loginkey\":\"8ff2b802-5e70-4118-a534-e490b6ce8be2\",\"text\":\"ecologylogin:8ff2b802-5e70-4118-a534-e490b6ce8be2,actionName:QR_LOGIN,randomNumber:-2033576501,bizSN:-1,em_sys_id:null\"},\"loginTemplateTitle\":\"泛微协同商务系统\",\"recordcode\":\"\",\"bgImage\":\"\",\"isDefault\":true,\"logoImage\":\"\",\"bgImagesInfo\":[{\"width\":3000,\"imgsrc\":\"/wui/theme/ecology9/image/bg1.jpg\",\"height\":1875},{\"width\":2000,\"imgsrc\":\"/wui/theme/ecology9/image/bg2.jpg\",\"height\":1333},{\"width\":2880,\"imgsrc\":\"/wui/theme/ecology9/image/bg3.jpg\",\"height\":1920},{\"width\":2560,\"imgsrc\":\"/wui/theme/ecology9/image/bg4.jpg\",\"height\":1568},{\"width\":3000,\"imgsrc\":\"/wui/theme/ecology9/image/bg5.jpg\",\"height\":2000}],\"isRememberPW\":\"1\",\"loginTemplateTitle_base64\":\"泛微协同商务系统\",\"id\":\"21\"}";
        JSONObject jsonObject = JSONObject.parseObject(str);
        JSONObject jsonObject1 = jsonObject.getJSONObject("labelInfo").getJSONObject("langid7");
        //jsonObject1.remove("rememberPassword","记住密码");
        jsonObject1.fluentRemove("rememberPassword");

        System.out.println(jsonObject.toJSONString());
    }


}

