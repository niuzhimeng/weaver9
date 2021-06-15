package com.mytest.annotation.vo.impl;

import com.mytest.annotation.MyComponent;
import com.mytest.annotation.MyResource;
import com.mytest.annotation.vo.Human;
import com.mytest.annotation.vo.OneUtil;

@MyComponent
public class Student implements Human {

    public static String name;

    private String age;

    @MyResource(classPath = "com.mytest.annotation.vo.OneUtil")
    private static OneUtil oneUtil;

    @Override
    public void study() {
        oneUtil.myPrint();
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }


}