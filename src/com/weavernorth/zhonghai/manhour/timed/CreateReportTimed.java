package com.weavernorth.zhonghai.manhour.timed;

import com.weavernorth.zhonghai.manhour.util.ManHourUtil;
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
            // 生成 【当前月】-【当前月+1】 的报表
            LocalDate nowDate = LocalDate.now();
            int year = nowDate.getYear();
            int month = nowDate.getMonthValue();
            int day = nowDate.getDayOfMonth();
            if (day >= 24) {
                // 生成 【当前月】-【当前月+1】 的报表
                LocalDate alterDate = nowDate.plusMonths(1);
                year = alterDate.getYear();
                month = alterDate.getMonthValue();
            }

            // 计算两个日期间的工作日 上月24 - 本月23
            LocalDate endDate = LocalDate.of(year, month, 23);
            LocalDate startDate = endDate.minusMonths(1).plusDays(1);

            int workdays = ManHourUtil.getWorkdays(startDate, endDate);
            int hour = workdays * 8;
            MY_LOG.info(year + "-" + month + "-23 至上月24号工作日天数：" + workdays + ", 工时: " + hour);

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

                updateSet.executeUpdate(mainInsertSql,
                        id, workCode, departmentId, hour, year, month);
            }

            MY_LOG.info("生成工时报表定时任务 End");
        } catch (Exception e) {
            MY_LOG.info("生成工时报表定时任务 Error");
        }
    }


}
