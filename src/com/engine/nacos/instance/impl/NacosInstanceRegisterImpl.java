package com.engine.nacos.instance.impl;

import com.engine.nacos.instance.NacosBeat;
import com.engine.nacos.task.NacosTimerSchedule;
import com.engine.nacos.util.HttpUtils;
import com.weaverboot.frame.ioc.anno.classAnno.WeaSysInitComponent;
import com.weaverboot.frame.ioc.anno.methodAnno.WeaSysInit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: wlj
 * @Date: 2021/3/23 15:28
 * @Modified By:
 */
@WeaSysInitComponent("启动类")
public class NacosInstanceRegisterImpl {

    private static final Log logger = LogFactory.getLog(NacosInstanceRegisterImpl.class);



    @WeaSysInit(order = 1 ,description = "执行方法1")
    public void register(){

        logger.info("============================nacos注册");

        Map<String, Object> params = new HashMap<>();

        params.put("ip","10.3.115.179");
        params.put("port","8080");
        params.put("serviceName","weaver-service1");

        HttpUtils.doPost("http://10.3.0.66:8848/nacos/v1/ns/instance",params,null);
    }



    @WeaSysInit(order = 3 ,description = "执行方法3")
    public void register2(){

        logger.info("============================nacos注册2");

        Map<String, Object> params = new HashMap<>();

        params.put("ip","10.3.115.179");
        params.put("port","8080");
        params.put("serviceName","weaver-service2");

        HttpUtils.doPost("http://10.3.0.66:8848/nacos/v1/ns/instance",params,null);
    }



    @WeaSysInit(order = 4 ,description = "执行方法4")
    public void task2(){

        logger.info("============================nacos心跳检测2");

        NacosTimerSchedule.startSave();
    }


}