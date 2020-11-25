package com.mytest.sso;

import com.weavernorth.createFlow.util.OkHttpUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestSSO {

    /**
     * 单点进oa  好使
     */
    @Test
    public void test1() {
        Map<String, String> heads = new HashMap<>();
        heads.put("appid", "ssss");
        heads.put("loginid", "nzm");
        String token = OkHttpUtil.okPostBodyHeader("http://127.0.0.1:8080/ssologin/getToken", heads, null);
        System.out.println("获取token： " + "http://localhost:8080/wui/index.html?ssoToken=" + token + "#/main");
    }
}
