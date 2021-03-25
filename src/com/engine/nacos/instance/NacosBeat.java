package com.engine.nacos.instance;

import com.alibaba.fastjson.JSON;
import com.engine.nacos.entity.BeatInfo;
import com.engine.nacos.util.HttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: wlj
 * @Date: 2021/3/24 10:51
 * @Modified By:
 */
public class NacosBeat {


    public static String beat() {

        beat1();
        beat2();
        return "ture";
    }

    public static String beat1() {
        Map<String, String> params = new HashMap<>();

        BeatInfo beatInfo = new BeatInfo();
        beatInfo.setIp("10.3.115.179");
        beatInfo.setPort("8080");
        beatInfo.setServiceName("weaver-service1");
        beatInfo.setScheduled(true);

        params.put("beat", JSON.toJSONString(beatInfo));
        params.put("serviceName", "weaver-service1");


        return HttpUtils.doPut("http://10.3.0.66:8848/nacos/v1/ns/instance/beat", params, null);
    }


    public static String beat2() {
        Map<String, String> params = new HashMap<>();

        BeatInfo beatInfo = new BeatInfo();
        beatInfo.setIp("10.3.115.179");
        beatInfo.setPort("8080");
        beatInfo.setServiceName("weaver-service2");
        beatInfo.setScheduled(true);

        params.put("beat", JSON.toJSONString(beatInfo));
        params.put("serviceName", "weaver-service2");


        return HttpUtils.doPut("http://10.3.0.66:8848/nacos/v1/ns/instance/beat", params, null);
    }
}