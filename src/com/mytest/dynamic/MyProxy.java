package com.mytest.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MyProxy implements InvocationHandler {

    public Object obj;

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public static Object getProxyInstance(Object obj) {
        MyProxy test = new MyProxy();
        test.setObj(obj);
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), test);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        System.out.println("执行前====");
        // obj ：被代理类对象
        //method.invoke(this.obj, args);
        System.out.println("执行后====");
        return null;
    }
}

