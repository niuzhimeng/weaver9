package com.weavernorth.zhonghai.manhour.report;

import com.weavernorth.zhonghai.manhour.util.ManHourUtil;
import org.apache.commons.lang.StringUtils;
import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

import java.time.LocalDate;

/**
 * 离职手续办理单
 */
public class LiZhiAction extends BaseAction {

    @Override
    public String execute(RequestInfo requestInfo) {
        String requestId = requestInfo.getRequestid();
        String operateType = requestInfo.getRequestManager().getSrc();
        int formId = requestInfo.getRequestManager().getFormid();
        String tableName = "";
        RecordSet recordSet = new RecordSet();
        RecordSet updateSet = new RecordSet();
        recordSet.executeQuery("SELECT tablename FROM workflow_bill WHERE id = '" + formId + "'");
        if (recordSet.next()) {
            tableName = recordSet.getString("tablename");
        }

        this.writeLog("离职手续办理单 Start requestid=" + requestId + "  operatetype --- " + operateType + "   fromTable --- " + tableName);
        try {

            // 查询主表
            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            recordSet.next();

            String sqr = recordSet.getString("sqr");  // 申请人
            String rzrq = recordSet.getString("rzrq");  // 入职日期
            LocalDate now = LocalDate.now(); // 离职日期

            // 获取当前考勤区间
            LocalDate[] currentRange = ManHourUtil.getCurrentRange();
            LocalDate startDate = currentRange[0];
            LocalDate endDate = currentRange[1];
            int year = endDate.getYear();
            int month = endDate.getMonthValue();
            this.writeLog("报表区间： " + startDate.toString() + " 至 " + endDate.toString());
            this.writeLog("报表年： " + year + " 报表月 " + month);

            String mainUpdateSql = "update uf_gsbb set bzgs = ? where xm = ? and n = ? and y = ?";
            if (StringUtils.isBlank(rzrq)) {
                this.writeLog("部分老员工没有入职日期============");
                int workdays = ManHourUtil.getWorkdays(startDate, now);
                this.writeLog("【区间开始日期】-【离职日期】工作日天数：" + workdays);
                int hour = workdays * 8;
                this.writeLog("工时: " + hour);
                updateSet.executeUpdate(mainUpdateSql, hour, sqr, year, month);
                return "1";
            }

            LocalDate companyDate = LocalDate.parse(rzrq);
            if (companyDate.isBefore(startDate) || companyDate.isEqual(startDate)) {
                this.writeLog("【入职日期】早于或等于【区间开始日期】");
                int workdays = ManHourUtil.getWorkdays(startDate, now);
                this.writeLog("【区间开始日期】-【离职日期】工作日天数：" + workdays);
                int hour = workdays * 8;
                this.writeLog("工时: " + hour);
                updateSet.executeUpdate(mainUpdateSql, hour, sqr, year, month);
            } else {
                int workdays = ManHourUtil.getWorkdays(companyDate, now);
                this.writeLog("【入职日期】-【离职日期】工作日天数：" + workdays);
                int hour = workdays * 8;
                this.writeLog("工时: " + hour);
                updateSet.executeUpdate(mainUpdateSql, hour, sqr, year, month);
            }

            this.writeLog("离职手续办理单 End ===============");
        } catch (Exception e) {
            this.writeLog("离职手续办理单 Error： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("离职手续办理单  Error： " + e);
            return "0";
        }

        return "1";
    }
}
