package com.weavernorth.ebu8http.ann;

import java.lang.annotation.*;

@BaseLifeCycle
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented // 是否生成api文档
@Inherited // 是否被子类集成
public @interface  GetBiu {
    /**
     * 请求路径
     */
    String path();

}
