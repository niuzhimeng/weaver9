package com.weavernorth.zhonghai.manhour.report;

import com.weavernorth.zhonghai.manhour.util.ManHourUtil;
import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

import java.time.LocalDate;

/**
 * 入职流程
 */
public class RuZhiAction extends BaseAction {

    @Override
    public String execute(RequestInfo requestInfo) {
        String requestId = requestInfo.getRequestid();
        String operateType = requestInfo.getRequestManager().getSrc();
        int formId = requestInfo.getRequestManager().getFormid();
        String tableName = "";
        RecordSet recordSet = new RecordSet();
        RecordSet updateSet = new RecordSet();
        RecordSet existSet = new RecordSet();
        recordSet.executeQuery("SELECT tablename FROM workflow_bill WHERE id = '" + formId + "'");
        if (recordSet.next()) {
            tableName = recordSet.getString("tablename");
        }

        this.writeLog("入职流程 Start requestid=" + requestId + "  operatetype --- " + operateType + "   fromTable --- " + tableName);
        try {
            // 查询主表
            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            recordSet.next();
            String gh = recordSet.getString("gh"); // 工号
            String rzrq = recordSet.getString("rzrq"); // 入职日期
            String xnbm = recordSet.getString("xnbm"); // 虚拟部门
            String id = getIdByWorkCode(gh); // 人员id
            this.writeLog("工号： " + gh + ", 入职日期： " + rzrq + ", 虚拟部门: " + xnbm + ", 人员id： " + id);

            LocalDate companyDate = LocalDate.parse(rzrq);

            this.writeLog("插入虚拟部门完成===========");
            /*
                插入考勤报表
             */
            // 获取当前考勤区间
            LocalDate[] currentRange = ManHourUtil.getCurrentRange();
            LocalDate startDate = currentRange[0];
            LocalDate endDate = currentRange[1];
            int year = endDate.getYear();
            int month = endDate.getMonthValue();
            this.writeLog("报表区间： " + startDate.toString() + " 至 " + endDate.toString());
            this.writeLog("报表年： " + year + " 报表月 " + month);

            if (companyDate.isAfter(endDate)) {
                this.writeLog("入职日期在当前区间结束日期后，不做处理，等待下一个循环计算");
                return "1";
            }

            int workdays;
            if (companyDate.isBefore(startDate) || endDate.isEqual(startDate)) {
                this.writeLog("入职日期 在区间开始日期之前，或等于开始日期，计算【区间结束日期】-【区间开始日期】");
                workdays = ManHourUtil.getWorkdays(startDate, endDate);
            } else {
                this.writeLog("入职日期 在区间内，计算【区间结束日期】-【入职日期】");
                workdays = ManHourUtil.getWorkdays(companyDate, endDate);
            }

            int hour = workdays * 8;
            this.writeLog("工作日天数：" + workdays + " 标准工时: " + hour);

            String mainInsertSql = "insert into uf_gsbb(xm, gh, szbm, bzgs, n, y)values(?,?,?,?,?, ?)";
            existSet.executeQuery("select 1 from uf_gsbb where xm = ? and n = ? and y = ?", id, year, month);
            if (!existSet.next()) {
                updateSet.executeUpdate(mainInsertSql,
                        id, gh, xnbm, hour, year, month);
            } else {
                this.writeLog("id为： " + id + " 的员工，在 " + year + "-" + month + " 报表中已存在。");
            }

            this.writeLog("入职流程 End ===============");
        } catch (Exception e) {
            this.writeLog("入职流程 Error： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("入职流程 Error： " + e);
            return "0";
        }

        return "1";
    }

    /**
     * 根据工号查询人员id
     */
    private String getIdByWorkCode(String workCode) {
        String id = "";
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("select id from hrmresource where workcode = ?", workCode);
        if (recordSet.next()) {
            id = recordSet.getString("id");
        }

        return id;
    }

}
