package com.mytest.sso;
import com.weaverboot.frame.ioc.anno.classAnno.WeaSsoIocComponent;
import com.weaverboot.frame.ioc.anno.methodAnno.WeaSsoIoc;
import com.weaverboot.frame.ioc.handler.replace.weaReplaceParam.impl.WeaSsoParam;
import com.weaverboot.tools.logTools.LogTools;

@WeaSsoIocComponent("TestLogin")
public class TestLogin {
    //参数weaSsoParam，字段为 request response paramMap
    @WeaSsoIoc(order = 1, description = "单点登录逻辑1")
    public void sso1(WeaSsoParam weaSsoParam){

//        LogTools.info("sso1");
    }

}
