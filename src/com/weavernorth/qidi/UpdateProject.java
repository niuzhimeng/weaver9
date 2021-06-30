package com.weavernorth.qidi;

import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

/**
 * XM02- 项目立项信息变更流程
 */
public class UpdateProject extends BaseAction {

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

        this.writeLog("XM02- 项目立项信息变更流程 Start requestid=" + requestId + "  operatetype --- " + operateType + "   fromTable --- " + tableName);
        try {
            // 查询主表
            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            recordSet.next();
            String xmmc = recordSet.getString("xmmc"); // 项目id
            String gghhtzje = recordSet.getString("gghhtzje"); // 合同总金额
            String qzpxf2 = recordSet.getString("qzpxf2"); // 培训费
            String qzzxf2 = recordSet.getString("qzzxf2"); // 咨询费
            String qzqfy2 = recordSet.getString("qzqfy2"); // 其他费用
            this.writeLog("项目id： " + xmmc + ", 合同总金额: " + gghhtzje + ", 培训费: " + qzpxf2 +
                    ", 咨询费: " + qzzxf2 + ", 其他费用： " + qzqfy2);

            recordSet.executeUpdate("update prj_projectinfo set htje = ?, pxf = ?, zxf = ?, qfy = ? where id = ?",
                    gghhtzje, qzpxf2, qzzxf2, qzqfy2, xmmc);


            this.writeLog("XM02- 项目立项信息变更流程 End ===============");
        } catch (Exception e) {
            this.writeLog("XM02- 项目立项信息变更流程 Error： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("XM02- 项目立项信息变更流程 Error： " + e);
            return "0";
        }

        return "1";
    }
}
