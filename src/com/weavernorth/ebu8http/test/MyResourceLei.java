package com.weavernorth.ebu8http.test;

import com.mytest.annotation.test.TestInter;
import com.weavernorth.ebu8http.ann.Ebu8Components;
import com.weavernorth.ebu8http.ann.ResourceBiu;

import java.util.HashMap;
import java.util.Map;

@Ebu8Components(name = "MyResourceLei")
public class MyResourceLei {

    @ResourceBiu
    public static TestInter testInter;

    public void sendGet() {
        Map<String, String> map = new HashMap<>();
        map.put("请求头", "tou");
        String s = testInter.sendGet(map,"param");
        System.out.println(s);
    }

}


