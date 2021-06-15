package com.mytest.annotation.proxy;

import com.mytest.annotation.MyComponent;
import com.mytest.annotation.MyResource;
import com.mytest.annotation.vo.impl.Student;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class MyProxy implements InvocationHandler {

    private Object obj;

    /**
     * obj  实际对象
     *
     * @return 一个代理类对象
     */
    public static Object getProxy(Student student) {
        MyProxy myProxy = new MyProxy();
        try {
            myProxy.obj = student;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 实际对象类加载器， 实际对象实现的接口， 代理类对象
        return Proxy.newProxyInstance(student.getClass().getClassLoader(), student.getClass().getInterfaces(), myProxy);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("前置执行============");
        System.out.println("真实方法名： " + method.getName());
        method.invoke(this.obj, args); // 实际对象， 接口参数
        System.out.println("后置执行============");
        return null;
    }

    public static void init() throws Exception {
        List<String> list = new ArrayList<>();
        File file = new File("C:\\Users\\86157\\Desktop\\weaver9\\src\\com\\mytest\\annotation\\");
        getAllFile(file, list);
        for (String path : list) {
            Class<?> aClass = Class.forName(path);
            if (!aClass.isAnnotationPresent(MyComponent.class)) {
                continue;
            }
            // 存在类注解, 获取字段中注解
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(MyResource.class)) {
                    continue;
                }
                MyResource annotation = field.getAnnotation(MyResource.class);

                String classPath = annotation.classPath();
                Class<?> bClass = Class.forName(classPath);
                Object o = bClass.newInstance();
                field.set(field, o);
            }
        }
    }

    public static void getAllFile(File file, List<String> list) {
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File listFile : listFiles) {
                if (listFile.isFile()) {
                    String path = listFile.getPath();
                    int i = path.indexOf("\\com\\");
                    path = path.substring(i + 1).replace("\\", ".").replace(".java", "");
                    list.add(path);
                } else {
                    getAllFile(listFile, list);
                }
            }
        }
    }


}
