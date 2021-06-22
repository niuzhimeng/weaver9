package com.mytest.annotation.test;

import com.weavernorth.ebu8http.proxy.Ebu8Proxy;

import java.util.HashMap;
import java.util.Map;

public class Test {


    @org.junit.Test
    public void test1() throws ClassNotFoundException {

        TestInter proxy = Ebu8Proxy.getProxy(TestInter.class);

        Map<String, String> map = new HashMap<>();
        map.put("name", "nzm");

        String s1 = proxy.sendGet(map);
        System.out.println("接口返回: "+ s1);

    }

}
