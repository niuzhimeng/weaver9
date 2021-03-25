package com.engine.nacos.util;

import weaver.general.BaseBean;

import java.lang.reflect.Field;

/**
 * Object 工具类
 */
public class ObjectUtils {

    /**
     * 根据配置文件解析，生成对应的对象
     * @param propValue
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T newInstance(String propValue, Class<T> clazz) {
        BaseBean baseBean = new BaseBean();
        try {
            T t = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            if (fields != null)
                for (Field field : fields) {
                    field.setAccessible(true);
                    field.set(t, baseBean.getPropValue(propValue, field.getName()));
                }
            return t;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
