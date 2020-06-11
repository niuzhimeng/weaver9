package com.weavernorth.weaverboot.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weaverboot.frame.ioc.anno.classAnno.WeaIocReplaceComponent;
import com.weaverboot.frame.ioc.anno.methodAnno.WeaReplaceAfter;

import com.weaverboot.frame.ioc.handler.replace.weaReplaceParam.impl.WeaAfterReplaceParam;
import weaver.general.BaseBean;

@WeaIocReplaceComponent("WeaverBootTest") //如不标注名称，则按类的全路径注入
public class WeaverBootTest {
    private BaseBean baseBean = new BaseBean();

    //这个是接口后置方法，大概的用法跟前置方法差不多，稍有差别
    //注解名称为WeaReplaceAfter
    //返回类型必须为String
    //参数叫WeaAfterReplaceParam，这个类前四个参数跟前置方法的那个相同，不同的是多了一个叫data的String，这个是那个接口执行完返回的报文
    //你可以对那个报文进行操作，然后在这个方法里return回去
    @WeaReplaceAfter(value = "/api/workflow/reqform/getRequestLogList", order = 1)
    public String after(WeaAfterReplaceParam weaAfterReplaceParam) {
        String data = weaAfterReplaceParam.getData();//这个就是接口执行完的报文

        baseBean.writeLog("接口返回json： " + JSONObject.toJSONString(data));
        JSONObject jsonObject = JSONObject.parseObject(data);
        JSONArray logList = jsonObject.getJSONArray("loglist");
        for (int i = 0; i < logList.size(); i++) {
            JSONObject obj1 = logList.getJSONObject(i);
            String displaydepname = obj1.getString("displaydepname");
            String displayid = obj1.getString("displayid"); // 人员id
            obj1.put("displaydepname", displaydepname + "-" + "一个岗位");
        }
        return jsonObject.toJSONString();
    }

    //这是接口前置方法，这个方法会在接口执行前执行
    //前值方法必须用@WeaReplaceBefore,这里面有两个参数，第一个叫value，是你的api地址
    //第二个参数叫order，如果你有很多方法拦截的是一个api，那么这个就决定了执行顺序
    //前置方法的参数为WeaBeforeReplaceParam 这个类，里面有四个参数，request，response，请求参数的map，api的地址
//    @WeaReplaceBefore(value = "/api/....", order = "1")
//    public void before(WeaBeforeReplaceParam weaBeforeReplaceParam) {
//        //一顿操作
//        //weaBeforeReplaceParam.setParamMap();
//        Map paramMap = weaBeforeReplaceParam.getParamMap();
//        baseBean.writeLog("weaverboot接口前参数： " + JSONObject.toJSONString(paramMap));
//
//    }
}
