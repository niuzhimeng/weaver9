package com.mytest.annotation.test;

import com.weavernorth.ebu8http.ann.GetBiu;
import com.weavernorth.ebu8http.ann.PostBiu;
import com.weavernorth.ebu8http.ann.field.Herder;

import java.util.Map;

public interface TestInter {

    @PostBiu(path = "")
    String sendPost(String json, Map<String, String> herders);

    @GetBiu(path = "http://www.baidu.com")
    String sendGet(@Herder Map<String, String> herders, String param);
}
