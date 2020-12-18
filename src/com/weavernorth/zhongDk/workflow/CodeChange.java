package com.weavernorth.zhongDk.workflow;

import org.apache.commons.lang3.StringUtils;
import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

/**
 * 流程编号替换
 */
public class CodeChange extends BaseAction {

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

        this.writeLog("流程编号替换 Start requestid=" + requestId + "  operatetype --- " + operateType + "   fromTable --- " + tableName);
        try {
            // 查询主表
            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            recordSet.next();

            String xmbm = recordSet.getString("xmbm"); // 	项目编码（系统） 去掉3、4位
            String szbmzzbm = recordSet.getString("szbmzzbm"); // 所属部门（组织编码）：取后4位
            this.writeLog("系统原编码： " + xmbm);
            if (StringUtils.isBlank(xmbm)) {
                this.writeLog("编码为空，不作操作");
                return "1";
            }
            // rx2021test 去掉年份前两位
            String part1 = xmbm.substring(0, 2);
            String part2 = xmbm.substring(4);
            String xmbm_new = part1 + part2;

            String szbmzzbm_new = szbmzzbm.substring(szbmzzbm.length() - 4);

            String newCode = xmbm_new + szbmzzbm_new;
            this.writeLog("拼接后编号： " + newCode);

            recordSet.executeUpdate("update " + tableName + " set xmbmzz = ? where requestid = ?",
                    newCode, requestId);

            this.writeLog("流程编号替换 End ===============");
        } catch (Exception e) {
            this.writeLog("流程编号替换 Error： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("流程编号替换 Error： " + e);
            return "0";
        }

        return "1";
    }
}
