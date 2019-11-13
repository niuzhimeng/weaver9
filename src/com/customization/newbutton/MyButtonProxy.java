package com.customization.newbutton;

import com.alibaba.fastjson.JSONObject;
import com.engine.core.cfg.annotation.CommandDynamicProxy;
import com.engine.core.interceptor.AbstractCommandProxy;
import com.engine.core.interceptor.Command;
import com.engine.workflow.cmd.requestForm.GetRightMenuCmd;
import com.engine.workflow.constant.requestForm.RequestMenuType;
import com.engine.workflow.entity.requestForm.RightMenu;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.hrm.User;

import java.util.List;
import java.util.Map;

@CommandDynamicProxy(target = GetRightMenuCmd.class, desc = "新建自定义按钮")
public class MyButtonProxy extends AbstractCommandProxy {
    @Override
    public Object execute(Command command) {
        BaseBean baseBean = new BaseBean();

        GetRightMenuCmd menuCmd = (GetRightMenuCmd) command;
        Map<String, Object> params = menuCmd.getParams();
        User user = menuCmd.getUser();
        baseBean.writeLog("当前操作者： " + user.getLastname());
        baseBean.writeLog("代理类接收参数: " + JSONObject.toJSONString(params));

        // 调用真实方法
        Map<String, Object> stringObjectMap = (Map<String, Object>) nextExecute(command);

        String workflowid = Util.null2String(stringObjectMap.get("workflowid"));
        String nodeid = Util.null2String(stringObjectMap.get("nodeid"));
        String requestid = Util.null2String(stringObjectMap.get("requestid"));
        baseBean.writeLog("workflowid ============= " + workflowid);
        baseBean.writeLog("nodeid ============= " + nodeid);
        baseBean.writeLog("requestid ============= " + requestid);

        List<RightMenu> rightMenus = (List<RightMenu>) stringObjectMap.get("rightMenus");
        RightMenu rightMenu = new RightMenu("自定义按钮", RequestMenuType.BTN_CUSTOMIZE, "diyFun()",
                "icon-workflow-Right-menu-Collection", 4.1);
        rightMenu.setIsTop("1");
        rightMenus.add(rightMenu);

        baseBean.writeLog("系统按钮返回数据：" + JSONObject.toJSONString(stringObjectMap));
        return stringObjectMap;
    }
}
