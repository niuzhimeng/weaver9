package com.mytest.annotation.container;

import com.mytest.annotation.MyComponent;
import com.mytest.annotation.MyResource;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义ioc容器
 */
public class MyContainer {

    public static Map<String, Object> containerMap = new HashMap<>();

    static {
        List<String> list = new ArrayList<>();
        try {
            // 加载带有MyComponent注解的类到map中
            initComponents();
            // 为标有MyIoc注解的字段进行类注入
            inject();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        List<String> list = new ArrayList<>();
        try {
            File file = new File("C:\\Users\\86157\\Desktop\\weaver9\\src\\com\\mytest\\annotation\\");
            getAllFile(file, list);
            for (String path : list) {
                Class<?> aClass = Class.forName(path);
                // 不包含容器注解，跳过
                if (!aClass.isAnnotationPresent(MyComponent.class)) {
                    continue;
                }
                // 添加到容器中 name不为空按照name注入，否则按照类名注入
                MyComponent myComponentAnn = aClass.getAnnotation(MyComponent.class);
                // com.mytest.annotation.vo.impl.AnnTestVOImpl
                String iocKey = aClass.getName();
                if (StringUtils.isNotBlank(myComponentAnn.name())) {
                    iocKey = myComponentAnn.name();
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

}
