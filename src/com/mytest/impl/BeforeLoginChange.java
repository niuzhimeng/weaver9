package com.mytest.impl;

import com.alibaba.fastjson.JSONObject;
import com.weaverboot.frame.ioc.anno.classAnno.WeaIocReplaceComponent;
import com.weaverboot.frame.ioc.anno.methodAnno.WeaReplaceAfter;
import com.weaverboot.frame.ioc.handler.replace.weaReplaceParam.impl.WeaAfterReplaceParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@WeaIocReplaceComponent
public class BeforeLoginChange {
    private static final Log LOGGER = LogFactory.getLog(BeforeLoginChange.class);

    //这个是接口后置方法，大概的用法跟前置方法差不多，稍有差别
    //注解名称为WeaReplaceAfter
    //返回类型必须为String
    //参数叫WeaAfterReplaceParam，这个类前四个参数跟前置方法的那个相同，不同的是多了一个叫data的String，这个是那个接口执行完返回的报文
    //你可以对那个报文进行操作，然后在这个方法里return回去
    @WeaReplaceAfter(value = "/api/portal/login/logininfo", order = 1)
    public String after(WeaAfterReplaceParam weaAfterReplaceParam) {
        String data = weaAfterReplaceParam.getData();//这个就是接口执行完的报文
        LOGGER.info("登录页面返回json： " + data);
        JSONObject jsonObject = JSONObject.parseObject(data);
        JSONObject jsonObject1 = jsonObject.getJSONObject("labelInfo").getJSONObject("langid7");
        //jsonObject1.remove("rememberPassword","记住密码");
        jsonObject1.fluentRemove("rememberPassword");

        LOGGER.info("修改后josn" + jsonObject.toJSONString());
        return jsonObject.toJSONString();
    }
}
