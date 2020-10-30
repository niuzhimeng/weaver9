package com.mytest.resultToBean;

import com.alibaba.fastjson.JSONObject;
import weaver.conn.RecordSet;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: LZW
 * @create: 2020-10-18 14:36
 **/

public class ResultToPojo {

    public static void main(String[] args) throws Exception {
        UfMoneyCheck ufMoneyCheck = new UfMoneyCheck();
        Class<? extends UfMoneyCheck> aClass = ufMoneyCheck.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();

        List<? extends UfMoneyCheck> ufMoneyChecks = Populate1(aClass);
        System.out.println(JSONObject.toJSONString(ufMoneyChecks));
    }

    public static <T> List<T> Populate1(Class<T> tClass) throws Exception {
        List<T> list = new ArrayList<>();
        // 实体对象字段名 - 对象
        Map<String, Field> beanMap = new HashMap<>();
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            beanMap.put(field.getName(), field);
        }

        for (Field field : fields) {
            String name = field.getName();
            System.out.println(name);
            //实例化对象
            T t = tClass.newInstance();
            Field field1 = beanMap.get(name);
            String name1 = field1.getType().getName();
            field1.setAccessible(true);
            if (name1.contains("String")) {
                field1.set(t, "123");
            } else {
                field1.set(t, 111);
            }

            list.add(t);
        }
        return list;
    }

    public static <T> List<T> Populate(RecordSet recordSet, Class<T> tClass) throws Exception {
        List<T> list = new ArrayList<>(recordSet.getCounts());
        String[] colNames = recordSet.getColumnName();

        // 实体对象字段名 - 对象
        Map<String, Field> beanMap = new HashMap<>();
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            beanMap.put(field.getName(), field);
        }

        while (recordSet.next()) {
            //实例化对象
            T t = tClass.newInstance();
            //取出每一个字段进行赋值
            for (String name : colNames) {
                String currColVal = recordSet.getString(name);
                //匹配实体类中对应的属性
                Field field = beanMap.get(name);
                field.setAccessible(true);
                field.set(t, currColVal);
            }
            list.add(t);
        }

        return list;

    }

}
