package com.weavernorth.zhonghai.manhour.timed;

import com.weavernorth.zhonghai.manhour.util.ManHourUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.RecordSet;
import weaver.interfaces.schedule.BaseCronJob;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成工时报表
 */
public class CreateReportTimed extends BaseCronJob {

    private static final Log MY_LOG = LogFactory.getLog(CreateReportTimed.class);

    @Override
    public void execute() {
        MY_LOG.info("生成工时报表定时任务 Start");

        // 8-9月的 叫9月份报表; 9-10 的叫10月报表
        try {
            // 获取当前考勤区间
            LocalDate[] currentRange = ManHourUtil.getCurrentRange();
            LocalDate startDate = currentRange[0];
            LocalDate endDate = currentRange[1];
            int year = endDate.getYear();
            int month = endDate.getMonthValue();
            MY_LOG.info("报表区间： " + startDate.toString() + " 至 " + endDate.toString());
            MY_LOG.info("报表年： " + year + " 报表月 " + month);

            int workdays = ManHourUtil.getWorkdays(startDate, endDate);
            MY_LOG.info("区间内全部工作日天数：" + workdays);
            int hour = workdays * 8;
            MY_LOG.info("标准工时: " + hour);

            RecordSet recordSet = new RecordSet();
            RecordSet updateSet = new RecordSet();
            // 当月存在报表中的人
            List<String> alreadyPersonList = new ArrayList<>();
            recordSet.executeQuery("select xm from uf_gsbb where n = " + year + " and y = " + month);
            while (recordSet.next()) {
                alreadyPersonList.add(recordSet.getString("xm"));
            }

            // 主表插入语句
            String mainInsertSql = "insert into uf_gsbb(xm, gh, szbm, bzgs, n, y)values(?,?,?,?,?, ?)";
            recordSet.executeQuery("SELECT a.id, a.workcode, b.departmentid, a.companystartdate FROM hrmresource a LEFT JOIN hrmresourcevirtual b ON a.id = b.resourceid WHERE a.STATUS < 4 AND b.virtualtype = '-20003'");
            while (recordSet.next()) {
                String id = recordSet.getString("id");
                if (alreadyPersonList.contains(id)) {
                    continue;
                }
                String workCode = recordSet.getString("workcode");
                String departmentId = recordSet.getString("departmentid");
                String companyStartDate = recordSet.getString("companystartdate"); // 入职日期

                if (StringUtils.isBlank(companyStartDate)) {
                    // 部分老员工没有入职日期，按满工作日工计算
                    updateSet.executeUpdate(mainInsertSql,
                            id, workCode, departmentId, hour, year, month);
                    continue;
                }

                LocalDate companyDate = LocalDate.parse(companyStartDate);
                if (companyDate.isBefore(startDate) || companyDate.isEqual(startDate)) {
                    // 入职日期在 【区间开始日期】 之前，按满工作日工计算
                    updateSet.executeUpdate(mainInsertSql,
                            id, workCode, departmentId, hour, year, month);
                } else if ((companyDate.isAfter(startDate) && companyDate.isBefore(endDate))
                        || companyDate.isEqual(endDate)) {
                    // 区间开始日期 < 入职日期 <= 区间结束日期
                    int workdays1 = ManHourUtil.getWorkdays(companyDate, endDate);
                    int hour1 = workdays1 * 8;
                    updateSet.executeUpdate(mainInsertSql,
                            id, workCode, departmentId, hour1, year, month);
                }

            }
            MY_LOG.info("生成工时报表定时任务 End");
        } catch (Exception e) {
            MY_LOG.info("生成工时报表定时任务 Error");
        }
    }


}
