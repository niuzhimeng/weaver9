package com.weavernorth.zhonghai;

import weaver.conn.RecordSet;
import weaver.conn.RecordSetTrans;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

/**
 * 流程编码修改
 * 去除年份前两位
 */
public class WorkflowCodeChange extends BaseAction {

    @Override
    public String execute(RequestInfo requestInfo) {
        String requestId = requestInfo.getRequestid();
        String operateType = requestInfo.getRequestManager().getSrc();
        int formId = requestInfo.getRequestManager().getFormid();
        String tableName = "";
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("SELECT tablename FROM workflow_bill WHERE id = '" + formId + "'");
        if (recordSet.next()) {
            tableName = recordSet.getString("tablename");
        }

        this.writeLog("流程编码修改 Start requestid=" + requestId + "  operatetype --- " + operateType + "   fromTable --- " + tableName);
        try {
            // 查询主表
            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            recordSet.next();

            String flowCode = recordSet.getString("xmbh"); // 格式： CZHCW-00-2020072
            this.writeLog("系统生成的编号: " + flowCode);

            String[] split = flowCode.split("-");
            String after = split[2].substring(2);
            String newCode = split[0] + "-" + split[1] + "-" + after;

            //更新流程表单
            String updateSql = "update " + tableName + " set xmbh = '" + newCode + "'  where requestid = " + requestId;
            this.writeLog("更新编号的sql：" + updateSql);
            recordSet.executeUpdate(updateSql);
            //更新请求名称
            RecordSetTrans updateWorkFlowSet = requestInfo.getRsTrans();
            String updateWorkFlow = "update workflow_requestbase set requestmark = '" + flowCode + "' WHERE requestid = '" + requestId + "'";
            try {
                updateWorkFlowSet.executeSql(updateWorkFlow);
            } catch (Exception e) {
                this.writeLog("RecordSetTrans报错：" + e);
            }

            this.writeLog("流程编码修改 End ===============");
        } catch (Exception e) {
            this.writeLog("流程编码修改 异常： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("流程编码修改 异常： " + e);
            return "0";
        }

        return "1";
    }
}

