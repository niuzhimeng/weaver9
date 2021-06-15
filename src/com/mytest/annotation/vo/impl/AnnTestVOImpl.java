package com.mytest.annotation.vo.impl;

import com.mytest.annotation.MyComponent;
import com.mytest.annotation.MyResource;
import com.mytest.annotation.vo.AnnTestVO;
import com.mytest.annotation.vo.OneUtil;

@MyComponent
public class AnnTestVOImpl implements AnnTestVO {

    @MyResource
    private static OneUtil oneUtil;

    @Override
    public void get() {
        System.out.println("被代理类方法执行===");
    }

    public static void myPrint() {
        oneUtil.myPrint();
    }
}
