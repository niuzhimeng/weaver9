package com.weavernorth.ebu8http.proxy;

import com.alibaba.fastjson.JSONObject;
import com.weavernorth.ebu8http.ann.BaseLifeCycle;
import com.weavernorth.ebu8http.ann.GetBiu;
import com.weavernorth.ebu8http.ann.PostBiu;
import com.weavernorth.ebu8http.ann.field.Herder;
import com.weavernorth.ebu8http.realHttpUtil.RealHttpUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Ebu8Proxy implements InvocationHandler {


    private static Set<String> excludedAnntotationMethodNames = new HashSet<>();

    static {
        excludedAnntotationMethodNames.add("equals");
        excludedAnntotationMethodNames.add("getClass");
        excludedAnntotationMethodNames.add("annotationType");
        excludedAnntotationMethodNames.add("notify");
        excludedAnntotationMethodNames.add("notifyAll");
        excludedAnntotationMethodNames.add("wait");
        excludedAnntotationMethodNames.add("hashCode");
        excludedAnntotationMethodNames.add("toString");
        excludedAnntotationMethodNames.add("newProxyInstance");
        excludedAnntotationMethodNames.add("newProxyClass");
        excludedAnntotationMethodNames.add("getInvocationHandler");
    }

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
        System.out.println("方法参数： " + JSONObject.toJSONString(args));
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            System.out.println(parameter);
            if (parameter.isAnnotationPresent(Herder.class)) {
                System.out.println("存在参数注解");
            }
        }
        for (Annotation annotation : method.getAnnotations()) {
            System.out.println("annotation.annotationType(): " + annotation.annotationType());
            System.out.println("annotation.annotationType(): " + annotation.annotationType().getAnnotation(BaseLifeCycle.class));
            if (annotation.annotationType().isAnnotationPresent(BaseLifeCycle.class)) {
                Map<String, Object> stringObjectMap = invokeAnnotationMethod(annotation);
                System.out.println("invokeAnnotationMethod: " + JSONObject.toJSONString(stringObjectMap));
                // 存在目标注解

                if (method.isAnnotationPresent(GetBiu.class)) {
                    GetBiu getBiu = method.getAnnotation(GetBiu.class);
                    String path = getBiu.path();
                    System.out.println("get方法请求路径： " + path);
                    return RealHttpUtil.get(path, null);
                }
                if (method.isAnnotationPresent(PostBiu.class)) {
                    return "";
                }
            }
        }
        return "未执行任何动作...";
    }

    private Map<String, Object> invokeAnnotationMethod(Annotation annotation) {
        Map<String, Object> map = new HashMap<>();
        Class<? extends Annotation> aClass = annotation.annotationType();
        Method[] methods = aClass.getMethods();
        Object[] objs = new Object[0];
        try {
            for (Method method : methods) {
                String name = method.getName();
                if (excludedAnntotationMethodNames.contains(name)) {
                    continue;
                }

                Object invoke = aClass.getMethod(name).invoke(annotation, objs);
                map.put(name, invoke);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }


}
