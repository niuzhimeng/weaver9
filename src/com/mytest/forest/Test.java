package com.mytest.forest;

import com.dtflys.forest.config.ForestConfiguration;

public class Test {

    @org.junit.Test
    public void test1(){
        // 实例化Forest配置对象
        ForestConfiguration configuration = ForestConfiguration.configuration();
        // 通过Forest配置对象实例化Forest请求接口
        MyClient myClient = configuration.createInstance(MyClient.class);

        // 调用Forest请求接口，并获取响应返回结果
        String result = myClient.simpleRequest();
        System.out.println(result);


    }

}
