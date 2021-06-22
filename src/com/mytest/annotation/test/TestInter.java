package com.mytest.annotation.test;

import com.weavernorth.ebu8http.ann.GetBiu;
import com.weavernorth.ebu8http.ann.PostBiu;

import java.util.Map;

public interface TestInter {

    @PostBiu(path = "")
    String sendPost(String json, Map<String, String> herders);

    @GetBiu(path = "http://localhost:8081/person/23")
    String sendGet(Map<String, String> herders);
}
