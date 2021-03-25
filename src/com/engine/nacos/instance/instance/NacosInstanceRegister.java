package com.engine.nacos.instance.instance;

import com.engine.nacos.util.HttpUtils;
import com.weaverboot.frame.ioc.anno.classAnno.WeaSysInitComponent;
import com.weaverboot.frame.ioc.anno.methodAnno.WeaSysInit;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: wlj
 * @Date: 2021/3/23 15:28
 * @Modified By:
 */
@WeaSysInitComponent("启动类")
public class NacosInstanceRegister {


    @WeaSysInit(order = 1 ,description = "执行方法1")
    public static void register(){

        Map<String, Object> params = new HashMap<>();

        params.put("ip","10.3.115.179");
        params.put("port","8080");
        params.put("serviceName","weaver-service");

        HttpUtils.doPost("http://10.3.0.66:8848/nacos/v1/ns/instance",params,null);
    }


}
