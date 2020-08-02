package com.mytest.annotation;

import com.mytest.annotation.vo.AnnTestVO;
import org.junit.Test;

import java.lang.reflect.Method;

public class MyTest {

    @Test
    public void test() {
        try {
            Integer j = 0;
            if (1 < 2) {
                j = null;
            }
            int i = 1 + j;
        } catch (Exception e) {
            String simpleName = e.getCause().getClass().getSimpleName();
            String message = e.getCause().getMessage();

            System.out.println(message);
            System.out.println(simpleName);
        }
//        Class<AnnTestVO> annTestVOClass = AnnTestVO.class;
//        Method test = annTestVOClass.getMethod("test");
//
//        MyAnn annotation = test.getAnnotation(MyAnn.class);
//        String value = annotation.value();
//        int order = annotation.order();
//        System.out.println(value + " " + order);

    }
}
