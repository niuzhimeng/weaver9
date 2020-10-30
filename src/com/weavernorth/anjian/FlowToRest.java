package com.weavernorth.anjian;

import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

import java.time.LocalDate;

/**
 * 流程转调休
 */
public class FlowToRest extends BaseAction {

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

        this.writeLog("流程转调休 Start requestid=" + requestId + "  operatetype --- " + operateType + "   fromTable --- " + tableName);
        try {
            // 查询主表
            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            recordSet.next();

            String sqr = recordSet.getString("sqr");  // 申请人
            String sqrq = recordSet.getString("sqrq");  // 申请日期
            String dhlx = recordSet.getString("dhlx");  // 兑换类型
            String bcdhss = recordSet.getString("bcdhss");  // 本次兑换时数
            this.writeLog("申请人： " + sqr + ", " + "申请日期: " + sqrq + ", 兑换类型： " + dhlx + ", 本次兑换时数: " + bcdhss);
            if ("1".equals(dhlx)) {
                LocalDate now = LocalDate.parse(sqrq);
                int year = now.getYear();
                int month = now.getMonthValue();

                // 过期时间，次年03-01
                LocalDate expirationDate = LocalDate.of(year + 1, 3, 1);
                String insertSql = "insert into kq_balanceofleave(leaveRulesId, resourceId, belongYear, baseAmount, extraAmount, " +
                        "usedAmount, expirationDate, belongMonth, overtimeType, effectiveDate) VALUES(?,?,?,?,?, ?,?,?,?,?)";
                recordSet.executeUpdate(insertSql, 5, sqr, year, 0, bcdhss,
                        0, expirationDate.toString(), month, 7, sqrq);
            } else {
                this.writeLog("不执行调休转换功能");
            }
            this.writeLog("流程转调休 End ===============");
        } catch (Exception e) {
            this.writeLog("流程转调休 Error： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("流程转调休 Error： " + e);
            return "0";
        }

        return "1";
    }
}
