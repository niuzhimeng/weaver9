package com.weavernorth.ebu8http.test;

import com.mytest.annotation.test.TestInter;
import com.weavernorth.ebu8http.container.Ebu8Container;
import com.weavernorth.ebu8http.proxy.Ebu8Proxy;

public class Test {
    /**
     * 注解扫描方式
     */
    @org.junit.Test
    public void test1() throws Exception {
        new Ebu8Container();
        MyResourceLei myResourceLei = new MyResourceLei();
        myResourceLei.sendGet();
    }

    /**
     * 手动调用获取代理类方式
     */
    @org.junit.Test
    public void test2() throws Exception {
        TestInter proxy = Ebu8Proxy.getProxy(TestInter.class);
        String s = proxy.sendGet(null);
        System.out.println(s);
    }
}
