package com.mytest.annotation.vo;

import com.mytest.annotation.MyComponent;

@MyComponent(name = "nzm")
public class OneUtil {

    public static String name;

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        OneUtil.name = name;
    }

    public void myPrint() {
        System.out.println("myPrint执行====");
    }
}

