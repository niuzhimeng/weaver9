package com.weavernorth.ebu8http.proxy;

import com.alibaba.fastjson.JSONObject;
import com.weavernorth.ebu8http.ann.GetBiu;
import com.weavernorth.ebu8http.ann.PostBiu;
import com.weavernorth.ebu8http.realHttpUtil.RealHttpUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Ebu8Proxy implements InvocationHandler {


    private Object obj;

    /**
     * obj  实际对象
     *
     * @return 一个代理类对象
     */
    public static <T> T getProxy(Class<T> aClass) {

        // 实际对象类加载器， 实际对象实现的接口， 代理类对象
        return (T) Proxy.newProxyInstance(aClass.getClassLoader(), new Class[]{aClass}, new Ebu8Proxy());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("方法名： " + method.getName());
        if (method.isAnnotationPresent(GetBiu.class) ) {
            GetBiu getBiu = method.getAnnotation(GetBiu.class);
            String path = getBiu.path();
            System.out.println("get方法请求路径： "+ path);
            return RealHttpUtil.get(path, null);
        }
        if (method.isAnnotationPresent(PostBiu.class)) {
            return "";
        }
        System.out.println("方法参数： " + JSONObject.toJSONString(args));
        System.out.println("后置执行============");
        return "";
    }


}
