package com.weavernorth.siwei;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

/**
 * 项目立项申请流程 编码拼接
 */
public class WorkflowNum extends BaseAction {

    @Override
    public String execute(RequestInfo requestInfo) {
        String requestId = requestInfo.getRequestid();
        String operateType = requestInfo.getRequestManager().getSrc();
        int formId = requestInfo.getRequestManager().getFormid();
        String tableName = "";
        RecordSet updateSet = new RecordSet();
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("SELECT tablename FROM workflow_bill WHERE id = '" + formId + "'");
        if (recordSet.next()) {
            tableName = recordSet.getString("tablename");
        }

        this.writeLog("项目立项申请流程 编码拼接 Start requestid --- " + requestId + "  operatetype --- " + operateType + "   fromTable --- " + tableName);
        try {
            // 查询主表
            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            if (recordSet.next()) {
                // 项目编号
                String xmbh = recordSet.getString("xmbh");
                // 项目分类
                String xmflwb = recordSet.getString("xmflwb");
                // 事业部代号
                String sybdh = recordSet.getString("sybdh");
                // 立项会时间
                String lxhsj = Util.null2String(recordSet.getString("lxhsj")).replace("-", "");

                this.writeLog("项目编号: " + xmbh);
                this.writeLog("项目分类: " + xmflwb);
                this.writeLog("事业部代号: " + sybdh);
                this.writeLog("立项会时间: " + lxhsj);
                String endNum = xmflwb + sybdh + lxhsj + xmbh;
                this.writeLog("拼接后编号： " + endNum);

                updateSet.executeUpdate("update " + tableName + " set xmbh = '" + endNum + "' where requestid = " + requestId);
            }
            this.writeLog("项目立项申请流程 编码拼接 End ===============");
        } catch (Exception e) {
            this.writeLog("项目立项申请流程 编码拼接 异常： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("项目立项申请流程 编码拼接 异常： " + e);
            return "0";
        }

        return "1";
    }
}
