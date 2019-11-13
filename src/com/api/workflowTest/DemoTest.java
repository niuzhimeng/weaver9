package com.api.workflowTest;

import com.engine.common.biz.AbstractCommonCommand;
import com.engine.common.entity.BizLogContext;
import com.engine.core.interceptor.CommandContext;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.hrm.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoTest extends AbstractCommonCommand<Map<String, Object>> {
    @Override
    public BizLogContext getLogContext() {
        return null;
    }

    public DemoTest(User user, Map<String, Object> params) {
        this.user = user;
        this.params = params;
    }

    @Override
    public Map<String, Object> execute(CommandContext commandContext) {
        BaseBean baseBean = new BaseBean();
        baseBean.writeLog("真实接口执行===========");

        RecordSet recordSet = new RecordSet();
        Map<String, Object> objMap = new HashMap<>();
        recordSet.executeQuery("select * from hrmresource");
        List<Map<String, String>> returnList = new ArrayList<>();
        while (recordSet.next()) {
            Map<String, String> returnMap = new HashMap<>();
            returnMap.put("lastname", recordSet.getString("lastname"));
            returnMap.put("id", recordSet.getString("id"));
            returnMap.put("sex", recordSet.getString("sex"));
            returnList.add(returnMap);
        }
        objMap.put("data", returnList);
        baseBean.writeLog("真实接口结束===========");
        return objMap;
    }
}
