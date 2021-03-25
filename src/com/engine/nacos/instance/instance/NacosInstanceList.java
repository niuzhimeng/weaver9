package com.engine.nacos.instance.instance;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.engine.nacos.util.HttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: wlj
 * @Date: 2021/3/23 15:50
 * @Modified By:
 */
public class NacosInstanceList {

    public static String get(){
        Map<String, String> params = new HashMap<>();

//        params.put("serviceName","weaver-service");

        String result = "";
        try {
//            result = HttpUtils.doGet("http://10.3.0.66:8848/nacos/v1/ns/instance/list",params,null);
//
//            Map<String, Object> resultParams = JSONObject.parseObject(result,Map.class);
//
//
//            List<Map<String, Object>> list = JSONArray.parseArray(String.valueOf(resultParams.get("hosts")),Map.class);
//
//            if(list != null)
//                for(Map map : list){
//
//                }


            return HttpUtils.doGet("http://10.3.115.225:8849/data/test",null,null);



        }catch (Exception e){
            e.printStackTrace();
        }

        return result;


    }
}
