package com.weavernorth.youke;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Test {

    @org.junit.Test
    public void test() {
        String json = "{\n" +
                "\t\"key\": \"d3fa857ec2e640d98c0e143fa18e3ad1\",\n" +
                "\t\"secret\":\"b731c63d9f5e4782a61810841282c356\"\n" +
                "}";
        String returns = YkHttpUtil.postJsonHeader("https://udaapi-dev.ucommune.com/rent/oa/login", json, null);
        System.out.println("获取token返回： " + returns);
    }

    @org.junit.Test
    public void test2() {
        String url = "https://udaapi-dev.ucommune.com/rent/oa/uploadFile";

        File file = new File("C:\\Users\\86157\\Desktop\\优达系统对接OA接口技术文档.docx");
        Map<String, String> headers = new HashMap<>();
        headers.put("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ1Y29tbXVuZSIsImV4cCI6MTYyMDcxNTg1NCwia2V5IjoiZDNmYTg1N2VjMmU2NDBkOThjMGUxNDNmYTE4ZTNhZDEifQ.OnSA0QC3Yxu9j54BDWn4tmkMA-tdzyZJ8zSlRqA0q5Q");

        String s = YkHttpUtil.postFileHeader(url, file, headers);
        System.out.println("返回：" + s);

    }

}
