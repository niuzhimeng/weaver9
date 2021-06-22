package com.weavernorth.ebu8http.test;

import com.mytest.annotation.test.TestInter;
import com.weavernorth.ebu8http.ann.Ebu8Components;
import com.weavernorth.ebu8http.ann.ResourceBiu;

@Ebu8Components
public class MyResourceLei {

    @ResourceBiu
    public static TestInter testInter;


    public void sendGet() {
        String s = testInter.sendGet(null);
        System.out.println(s);
    }

}


