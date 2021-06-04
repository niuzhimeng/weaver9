package com.mytest.annotation.vo.impl;

import com.mytest.annotation.MyCompoent;
import com.mytest.annotation.MyIoc;
import com.mytest.annotation.vo.AnnTestVO;
import com.mytest.annotation.vo.OneUtil;

@MyCompoent
public class AnnTestVOImpl implements AnnTestVO {

    @MyIoc(classPath = "com.mytest.annotation.vo.OneUtil")
    private static OneUtil oneUtil;

    @Override
    public void get() {
        System.out.println("被代理类方法执行===");
    }

    public static void myPrint(){
        oneUtil.myPrint();
    }
}
