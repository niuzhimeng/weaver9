package com.mytest.annotation.vo;

import com.mytest.annotation.MyAnn;


public class AnnTestVO {

    @MyAnn(value = "歪柳", order = 1)
    public void test() {
        System.out.println("test执行-----");
    }
}
