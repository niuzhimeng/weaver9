package com.weavernorth.hualianFlow;

import weaver.conn.RecordSet;
import weaver.general.TimeUtil;
import weaver.hrm.User;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

/**
 * 流程状态推送-异步
 */
public class PushFlowStateAsyn extends BaseAction {

    @Override
    public String execute(RequestInfo requestInfo) {
        String requestId = requestInfo.getRequestid();
        int nodeId = requestInfo.getRequestManager().getNodeid();
        String operateType = requestInfo.getRequestManager().getSrc();
        User user = requestInfo.getRequestManager().getUser();
        int formId = requestInfo.getRequestManager().getFormid();
        String tableName = "";
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("SELECT tablename FROM workflow_bill WHERE id = '" + formId + "'");
        if (recordSet.next()) {
            tableName = recordSet.getString("tablename");
        }
        this.writeLog("推送签字意见节点后执行==========requestId=" + requestId + ", nodeId=" + nodeId);
        try {
            String timeString = TimeUtil.getCurrentTimeString();
            String nodeName = getColumn("nodename", "workflow_nodebase", "id", String.valueOf(nodeId));

            // 插入待推送状态表
            recordSet.executeUpdate("insert into uf_push_state(myRequestid, nodeId, nodeName, operateType, loginid, " +
                            "tableName, rq, ifpush ) values(?,?,?,?,?, ?,?,?)",
                    requestId, nodeId, nodeName, operateType, user.getLoginid(),
                    tableName, timeString, "1");
        } catch (Exception e) {
            this.writeLog("流程状态推送 异常： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("流程状态推送 异常： " + e);
            return "0";
        }
        return "1";
    }

    /**
     * 根据某个字段查询另一个字段
     *
     * @param col        要查询的列名
     * @param tableName  表名
     * @param whereLeft  where条件 例：where name = 'nzm' 中的 name
     * @param whereRight where条件 例：where name = 'nzm' 中的 'nzm'
     */
    private String getColumn(String col, String tableName, String whereLeft, String whereRight) {
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("select " + col + " from " + tableName + " where " + whereLeft + " = '" + whereRight + "'");
        recordSet.next();
        return recordSet.getString(col);
    }
}
