package com.customization;

import com.alibaba.fastjson.JSONObject;
import com.api.workflowTest.DemoTest;
import com.engine.core.cfg.annotation.CommandDynamicProxy;
import com.engine.core.interceptor.AbstractCommandProxy;
import com.engine.core.interceptor.Command;
import weaver.general.BaseBean;
import weaver.hrm.User;

import java.util.Map;

@CommandDynamicProxy(target = DemoTest.class, desc = "新增表单页面按钮")
public class MyProxy extends AbstractCommandProxy {

    @Override
    public Object execute(Command command) {
        BaseBean baseBean = new BaseBean();
        Map<String, Object> myResult = null;
        try {
            baseBean.writeLog("进入代理接口调用前");
            DemoTest demoTest = (DemoTest) command;
            User user = demoTest.getUser();
            baseBean.writeLog("当前操作者； " + user.getUID() + ", " + user.getLastname());
            baseBean.writeLog("所有参数： " + JSONObject.toJSONString(demoTest.getParams()));

            myResult = (Map<String, Object>) nextExecute(command);

            baseBean.writeLog("代理接口操作返回数据： ");
            //myResult.put("daili", "代理类新增的数据");

            baseBean.writeLog("进入代理接口调用后");
        } catch (Exception e) {
            baseBean.writeLog("代理类异常： " + e);
        }
        return myResult;
    }
}
