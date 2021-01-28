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
        Map<String, String> body = new HashMap<>();
        body.put("appid", "ssss");
        body.put("loginid", "1001");
        String token = OkHttpUtil.okPostBodyHeader("http://127.0.0.1:8080/ssologin/getToken", body, null);
        System.out.println("打开首页： " + "http://localhost:8080/wui/index.html?ssoToken=" + token + "#/main");
        System.out.println("打开流程： " + "http://localhost:8080/spa/workflow/static4form/index.html?ssoToken=" + token + "#/main/workflow/req?requestid=305305");
        System.out.println("打开待办： " + "http://localhost:8080/wui/index.html?ssoToken=" + token + "#/main/workflow/listDoing");
    }
}
