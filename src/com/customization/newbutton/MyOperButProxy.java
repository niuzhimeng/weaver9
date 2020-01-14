package com.customization.newbutton;

import com.alibaba.fastjson.JSONObject;
import com.engine.core.cfg.annotation.CommandDynamicProxy;
import com.engine.core.interceptor.AbstractCommandProxy;
import com.engine.core.interceptor.Command;
import com.engine.workflow.cmd.workflowPath.node.operationMenu.GetOperationMenuInfoCmd;
import com.engine.workflow.entity.operationMenu.OperationMenuEntity;
import weaver.general.BaseBean;
import weaver.hrm.User;

import java.util.List;
import java.util.Map;

/**
 * 操作菜单加按钮
 */
@CommandDynamicProxy(target = GetOperationMenuInfoCmd.class, desc = "操作菜单加按钮")
public class MyOperButProxy extends AbstractCommandProxy {
    @Override
    public Object execute(Command command) {
        BaseBean baseBean = new BaseBean();

        GetOperationMenuInfoCmd menuCmd = (GetOperationMenuInfoCmd) command;
        Map<String, Object> params = menuCmd.getParams();
        User user = menuCmd.getUser();
        baseBean.writeLog("当前操作者： " + user.getLastname());
        baseBean.writeLog("代理类接收参数: " + JSONObject.toJSONString(params));

        // 调用真实方法
        Map<String, Object> stringObjectMap = (Map<String, Object>) nextExecute(command);

        baseBean.writeLog("操作菜单加按钮返回数据 ============= " + stringObjectMap);

        List<OperationMenuEntity> rightMenus = (List<OperationMenuEntity>) stringObjectMap.get("opratorSetDatas");
        baseBean.writeLog("opratorSetDatas=== " + JSONObject.toJSONString(rightMenus));
        OperationMenuEntity rightMenu = new OperationMenuEntity();
        rightMenu.setDefaultName("随便一个按钮");
        rightMenu.setMenuType(-1);
        rightMenu.setStartUsing(1);
        rightMenu.setType(0);
        rightMenu.setOrderId(6);
        rightMenu.setId(123);
        //rightMenu.setFunction("operFun");
        rightMenu.setStartUsing(1);
        rightMenu.setCustomName("");
        rightMenu.setDefaultSign("");

        rightMenus.add(rightMenu);

        baseBean.writeLog("操作菜单加按钮返回数据,加工后：" + JSONObject.toJSONString(stringObjectMap));
        return stringObjectMap;
    }
}
