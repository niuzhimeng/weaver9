package com.customization.Test0620;

import com.alibaba.fastjson.JSONObject;
import com.engine.core.cfg.annotation.CommandDynamicProxy;
import com.engine.core.interceptor.AbstractCommandProxy;
import com.engine.core.interceptor.Command;
import com.engine.doc.cmd.secCategoryList.DocSecCategoryAddCmd;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.RecordSet;
import weaver.hrm.User;

import java.util.Map;

@CommandDynamicProxy(target = DocSecCategoryAddCmd.class, desc = "考核试题")
public class DocAddProxy extends AbstractCommandProxy<Map<String, Object>> {

    private static final Log MY_LOG = LogFactory.getLog(DocAddProxy.class);

    @Override
    public Map<String, Object> execute(Command command) {
        MY_LOG.info("考核测试-新建文档拦截Start==============");

        DocSecCategoryAddCmd docSecCategoryAddCmd = (DocSecCategoryAddCmd) command;
        Map<String, Object> params = docSecCategoryAddCmd.getParams();
        User user = docSecCategoryAddCmd.getUser();
        MY_LOG.info("当前操作者： " + user.getLastname());
        MY_LOG.info("代理类接收参数: " + JSONObject.toJSONString(params));

        // 继承上级目录设置 1、是；0、否
        String extendParentAttr = String.valueOf(params.get("extendParentAttr"));
        MY_LOG.info("extendParentAttr: " + extendParentAttr);
        // 调用真实方法
        Map<String, Object> stringObjectMap = nextExecute(docSecCategoryAddCmd);

        if ("0".equals(extendParentAttr)) {
            RecordSet recordSet = new RecordSet();
            String id = String.valueOf(stringObjectMap.get("id"));
            MY_LOG.info("新目录的id： " + id);
            // 	是否可以订阅 1、是；0、否
            String updateSql = "update docseccategory set orderable = 1 where id = ?";
            recordSet.executeUpdate(updateSql, id);
        }
        MY_LOG.info("考核测试-新建文档拦截End==============");
        return stringObjectMap;
    }
}
