package com.weavernorth.zhongJiJian;

import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

/**
 * 更新结算台账流程状态
 */
public class UpdateModeState extends BaseAction {

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

        this.writeLog("更新结算台账流程状态 Start requestid=" + requestId + "  operateType --- " + operateType + "   fromTable --- " + tableName);
        try {
            if ("submit".equals(operateType)) {
                String submitSql = "update uf_yhxxb set zt = 0 where qqid = '" + requestId + "'";
                this.writeLog("提交更新流程状态sql：" + submitSql);
                recordSet.executeUpdate(submitSql);
            } else {
                String rejectSql = "delete from uf_yhxxb where qqid = '" + requestId + "'";
                this.writeLog("退回更删除该条数据sql：" + rejectSql);
                recordSet.executeUpdate(rejectSql);
            }

            this.writeLog("更新结算台账流程状态 End ===============");
        } catch (Exception e) {
            this.writeLog("更新结算台账流程状态 Error： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("更新结算台账流程状态 Error： " + e);
            return "0";
        }

        return "1";
    }
}
