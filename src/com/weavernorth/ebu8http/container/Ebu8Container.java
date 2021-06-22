package com.weavernorth.ebu8http.container;

import com.weavernorth.ebu8http.ann.Ebu8Components;
import com.weavernorth.ebu8http.ann.ResourceBiu;
import com.weavernorth.ebu8http.proxy.Ebu8Proxy;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 自定义ioc容器
 *
 * @author nzm
 * @date 2021-06-22
 */
public class Ebu8Container {
    /**
     * 遍历文件的根目录 路径
     */
    private static final String ROOT_PATH = "C:\\Users\\86157\\Desktop\\weaver9\\target\\classes\\com\\weavernorth\\";
    /**
     * 用于装载带有注解MyComponent的类
     */
    public static Map<String, Object> containerMap = new HashMap<>();

    static {
        // 加载带有MyComponent注解的类到map中
        initComponents();
        System.out.println("bean装载完成，共计： " + containerMap.size());
        // 为标有MyIoc注解的字段进行类注入
        inject();
        System.out.println("代理类注入完成=====");
    }

    public static void inject() {
        try {
            for (Map.Entry<String, Object> entry : containerMap.entrySet()) {
                Class<?> aClass = entry.getValue().getClass();

                Field[] declaredFields = aClass.getDeclaredFields();
                for (Field field : declaredFields) {
                    // 字段上不包含ResourceBiu 跳过
                    if (!field.isAnnotationPresent(ResourceBiu.class)) {
                        continue;
                    }
                    // 字段类型的全路径 例：com.mytest.annotation.test.TestInter
                    String name = field.getType().getName();
                    // 获取代理类对象
                    Object proxy = Ebu8Proxy.getProxy(Class.forName(name));
                    field.setAccessible(true);
                    field.set(entry.getValue(), proxy);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initComponents() {
        try {
            List<String> list = getAllFileByQueue(ROOT_PATH);
            for (String path : list) {
                Class<?> aClass = Class.forName(path);
                // 不包含容器注解，跳过
                if (!aClass.isAnnotationPresent(Ebu8Components.class)) {
                    continue;
                }

                // 添加到容器中 name不为空按照name注入，否则按照类的全路径名注入
                Ebu8Components myComponentAnn = aClass.getAnnotation(Ebu8Components.class);
                String iocKey = myComponentAnn.name();
                if (StringUtils.isBlank(iocKey)) {
                    iocKey = aClass.getName();
                }
                containerMap.put(iocKey, aClass.newInstance());

            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public static List<String> getAllFileByQueue(String filePath) {
        List<String> list = new ArrayList<>();
        Queue<File> queue = new LinkedBlockingQueue<>(1000);
        queue.offer(new File(filePath));

        while (!queue.isEmpty()) {
            File currFile = queue.poll();
            if (currFile.isDirectory()) {
                File[] files = currFile.listFiles();
                if (files != null) {
                    for (File listFile : files) {
                        queue.offer(listFile);
                    }
                }
            } else {
                String path = currFile.getPath();
                if (!path.endsWith(".class")) {
                    continue;
                }
                int i = path.indexOf("\\com\\");
                path = path.substring(i + 1).replace("\\", ".").replace(".class", "");
                list.add(path);
            }
        }
        return list;
    }

}
