package com.mytest.annotation;


import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented // 是否生成api文档
@Inherited // 是否被子类集成
public @interface MyComponent {

    /**
     * 注入名称，默认为空
     */
    String name() default "";

//    String url();
//
//    int order() default 0;

//    String[] shuzu();
//
//    int[] a();
}
