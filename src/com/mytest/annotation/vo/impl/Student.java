package com.mytest.annotation.vo.impl;

import com.mytest.annotation.MyCompoent;
import com.mytest.annotation.MyIoc;
import com.mytest.annotation.vo.Human;
import com.mytest.annotation.vo.OneUtil;

@MyCompoent
public class Student implements Human {

    public static String name;

    private String age;

    @MyIoc(classPath = "com.mytest.annotation.vo.OneUtil")
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
