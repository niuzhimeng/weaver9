package com.weavernorth.ebu8http.ann;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented // 是否生成api文档
@Inherited // 是否被子类集成
public @interface Ebu8Components {

    String name() default "";
}
