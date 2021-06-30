package com.weavernorth.ebu8http.test;

import com.mytest.annotation.test.TestInter;
import com.weavernorth.ebu8http.container.Ebu8Container;
import com.weavernorth.ebu8http.proxy.Ebu8Proxy;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class Test {
    /**
     * 注解扫描方式
     */
    @org.junit.Test
    public void test1() throws Exception {
        Map<String, Object> containerMap = Ebu8Container.containerMap;
        // MyResourceLei myResourceLei = (MyResourceLei) containerMap.get("MyResourceLei");
        MyResourceLei myResourceLei = new MyResourceLei();
        myResourceLei.sendGet();
    }

    /**
     * 手动调用获取代理类方式
     */
    @org.junit.Test
    public void test2() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("请求头", "头info");

        TestInter proxy = Ebu8Proxy.getProxy(TestInter.class);
        String s = proxy.sendGet(map, "parma21");
        System.out.println(s);
    }

    @org.junit.Test
    public void test3(){
        Annotation[] annotations = TestInter.class.getAnnotations();
        for (Annotation annotation : annotations) {
            System.out.println(annotation);
        }


    }
}
