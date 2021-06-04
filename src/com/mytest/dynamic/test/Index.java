package com.mytest.dynamic.test;

import com.mytest.annotation.vo.AnnTestVO;
import com.mytest.annotation.vo.impl.AnnTestVOImpl;
import com.mytest.dynamic.MyProxy;
import com.mytest.dynamic.MyProxy_inter;


public class Index {

    /**
     * 动态代理 - 实现类方式
     */
    @org.junit.Test
    public void test1() {
        AnnTestVO proxyInstance = (AnnTestVO) MyProxy.getProxyInstance(new AnnTestVOImpl());
        proxyInstance.get();
    }

    /**
     * 动态代理 - 接口方式
     */
    @org.junit.Test
    public void test2() {
        AnnTestVO proxyInstance = MyProxy_inter.getProxyInstance(AnnTestVO.class);
        proxyInstance.get();
    }
}





