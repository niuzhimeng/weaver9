package com.mytest.annotation;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented // 是否生成api文档
@Inherited // 是否被子类集成
public @interface MyAnn {

    String sendType();

    String url();

    int order() default 0;

//    String[] shuzu();
//
//    int[] a();
}
