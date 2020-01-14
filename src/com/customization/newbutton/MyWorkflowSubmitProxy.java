package com.customization.newbutton;

import com.alibaba.fastjson.JSONObject;
import com.engine.core.cfg.annotation.CommandDynamicProxy;
import com.engine.core.interceptor.AbstractCommandProxy;
import com.engine.core.interceptor.Command;
import com.engine.workflow.cmd.requestForm.RequestSubmitCmd;
import weaver.general.BaseBean;
import weaver.hrm.User;

import java.util.HashMap;
import java.util.Map;

@CommandDynamicProxy(target = RequestSubmitCmd.class, desc = "操作菜单加按钮")
public class MyWorkflowSubmitProxy extends AbstractCommandProxy {

    @Override
    public Map<String, Object> execute(Command command) {
        BaseBean baseBean = new BaseBean();

        RequestSubmitCmd menuCmd = (RequestSubmitCmd) command;
        Map<String, Object> params = menuCmd.getParams();
        User user = menuCmd.getUser();
        baseBean.writeLog("当前操作者： " + user.getLastname());
        baseBean.writeLog("当前流程id=========== " + params.get("workflowid"));
        baseBean.writeLog("代理类接收参数: " + JSONObject.toJSONString(params));

        Map<String, Object> jsonObject = new HashMap<>();
        Map<String, Object> sonObject = new HashMap<>();

        Map<String, Object> medObject = new HashMap<>();
        medObject.put("bottom", "errormsg");
        medObject.put("detail", "nzm的测试拦截");
        medObject.put("title", "流程提交失败");
        medObject.put("prompttype", "errormsg");

        Map<String, Object> requestObject = new HashMap<>();
        requestObject.put("requestid", "1");

        sonObject.put("messageInfo", medObject);
        sonObject.put("type", "FAILD");
        sonObject.put("resultInfo", requestObject);

        jsonObject.put("data", sonObject);
        baseBean.writeLog("自定义错误返回： " + JSONObject.toJSONString(jsonObject));

//        // 调用真实方法
        Map<String, Object> stringObjectMap = (Map<String, Object>) nextExecute(command);
//
//        baseBean.writeLog("提交方法返回： " + JSONObject.toJSONString(stringObjectMap));
        return stringObjectMap;
    }
}
