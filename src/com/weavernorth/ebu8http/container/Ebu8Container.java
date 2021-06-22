package com.weavernorth.ebu8http.container;

import com.mytest.annotation.MyComponent;
import com.mytest.annotation.MyResource;
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
    private static final String ROOT_PATH = "E:\\WEAVER\\ecology\\classbean\\com\\weavernorth";
    /**
     * 用于装载带有注解MyComponent的类
     */
    public static Map<String, Object> containerMap = new HashMap<>();

    static {
//        // 加载带有MyComponent注解的类到map中
//        initComponents();
//        // 为标有MyIoc注解的字段进行类注入
//        inject();

    }

    public static void inject() {
        try {
            for (Map.Entry<String, Object> entry : containerMap.entrySet()) {
                Class<?> aClass = entry.getValue().getClass();

                Field[] declaredFields = aClass.getDeclaredFields();
                for (Field field : declaredFields) {
                    // 字段上不包含MyResource 跳过
                    if (!field.isAnnotationPresent(MyResource.class)) {
                        continue;
                    }
                    MyResource annotation = field.getAnnotation(MyResource.class);
                    String annName = annotation.name();
                    if (StringUtils.isBlank(annName)) {
                        // 用户没有指定注入名称，按注入类的全路径
                        annName = field.getType().getName();
                    }
                    // 根据用户自定义名称或类的全路径 找到的obj
                    Object o = containerMap.get(annName);
                    if (null == o) {
                        System.out.println("标记： " + annName + " 没有找到对应类");
                        continue;
                    }
                    field.setAccessible(true);
                    field.set(entry.getValue(), containerMap.get(annName));
                    System.out.println("类： " + annName + " 注入到： " + aClass + " 中成功");
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
//                if (!aClass.isAnnotationPresent(Ebu8Http.class)) {
//                    continue;
//                }

                // 添加到容器中 name不为空按照name注入，否则按照类的全路径名注入
                MyComponent myComponentAnn = aClass.getAnnotation(MyComponent.class);
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
                int i = path.indexOf("\\com\\");
                path = path.substring(i + 1).replace("\\", ".").replace(".class", "");
                list.add(path);
            }
        }
        return list;
    }

}
