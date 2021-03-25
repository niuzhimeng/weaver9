package com.engine.nacos.instance.instance;

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
        Map<String, String> params = new HashMap<>();

        params.put("beat", "%7b%22cluster%22%3a%22c1%22%2c%22ip%22%3a%22127.0.0.1%22%2c%22metadata%22%3a%7b%7d%2c%22port%22%3a8080%2c%22scheduled%22%3atrue%2c%22serviceName%22%3a%22jinhan0Fx4s.173TL.net%22%2c%22weight%22%3a1%7d");
        params.put("serviceName", "weaver-service");


        return HttpUtils.doPut("http://10.3.0.66:8848/nacos/v1/ns/instance/beat", params, null);
    }
}
