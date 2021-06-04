package com.mytest.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MyProxy_inter implements InvocationHandler {

    public static <T> T getProxyInstance(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new MyProxy_inter());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        System.out.println("接口代理执行前====");
        // obj ：被代理类对象
        //method.invoke(this.obj, args);
        System.out.println("接口代理执行后====");
        return null;
    }
}

