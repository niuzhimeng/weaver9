package com.weavernorth.weaverboot.impl;

import com.alibaba.fastjson.JSONObject;
import com.weaverboot.frame.ioc.anno.classAnno.WeaIocReplaceComponent;
import com.weaverboot.frame.ioc.anno.methodAnno.WeaReplaceAfter;
import com.weaverboot.frame.ioc.anno.methodAnno.WeaReplaceBefore;
import com.weaverboot.frame.ioc.handler.replace.weaReplaceParam.impl.WeaAfterReplaceParam;
import com.weaverboot.frame.ioc.handler.replace.weaReplaceParam.impl.WeaBeforeReplaceParam;
import weaver.general.BaseBean;
import weaver.rsa.security.RSA;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WeaIocReplaceComponent()
public class WeaverBootTest1 {
    private BaseBean baseBean = new BaseBean();

    //这个是接口后置方法，大概的用法跟前置方法差不多，稍有差别
    //注解名称为WeaReplaceAfter
    //返回类型必须为String
    //参数叫WeaAfterReplaceParam，这个类前四个参数跟前置方法的那个相同，不同的是多了一个叫data的String，这个是那个接口执行完返回的报文
    //你可以对那个报文进行操作，然后在这个方法里return回去
    @WeaReplaceAfter(value = "/api/hrm/systeminfo/save", order = 1)
    public String after(WeaAfterReplaceParam weaAfterReplaceParam) {
        baseBean.writeLog("执行后： " + JSONObject.toJSONString(weaAfterReplaceParam.getParamMap()));
        String data = weaAfterReplaceParam.getData();//这个就是接口执行完的报文

        baseBean.writeLog("接口返回json： " + JSONObject.toJSONString(data));

        return data;
    }

    //这是接口前置方法，这个方法会在接口执行前执行
    //前值方法必须用@WeaReplaceBefore,这里面有两个参数，第一个叫value，是你的api地址
    //第二个参数叫order，如果你有很多方法拦截的是一个api，那么这个就决定了执行顺序
    //前置方法的参数为WeaBeforeReplaceParam 这个类，里面有四个参数，request，response，请求参数的map，api的地址
    @WeaReplaceBefore(value = "/api/hrm/systeminfo/save", order = 2, description = "")
    public void before(WeaBeforeReplaceParam weaBeforeReplaceParam) {
        //一顿操作
        baseBean.writeLog("查看密码拦截开始============");
        try {
            baseBean.writeLog("1weaverboot接口前参数： " + JSONObject.toJSONString(weaBeforeReplaceParam.getParamMap()));
            //weaBeforeReplaceParam.setParamMap();
            Map paramMap = weaBeforeReplaceParam.getParamMap();

            String password = (String) paramMap.get("password");
            ArrayList var25 = new ArrayList();
            var25.add(password);
            RSA var26 = new RSA();
            List var27 = var26.decryptList(weaBeforeReplaceParam.getRequest(), var25);
            password = (String) var27.get(0);
            baseBean.writeLog("解密后密码： " + password);
        } catch (Exception e) {
            baseBean.writeLog("查密码前置接口异常： " + e);
        }

    }
}
