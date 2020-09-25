package com.weavernorth.zhonghai.manhour.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.RecordSet;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class ManHourUtil {

    private static final Log MY_LOG = LogFactory.getLog(ManHourUtil.class);

    /**
     * 获取当前考勤区间
     */
    public static LocalDate[] getCurrentRange() {
        // 生成 【当前月】-【当前月-1】 的报表
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
        return new LocalDate[]{startDate, endDate};
    }

    /**
     * 计算期间的工作日天数
     */
    public static int getWorkdays(LocalDate startDate, LocalDate endDate) {
        String startLike = startDate.getYear() + "-" + addZero(startDate.getMonthValue()) + "%";
        String endLike = endDate.getYear() + "-" + addZero(endDate.getMonthValue()) + "%";

        RecordSet recordSet = new RecordSet();
        String selectSql = "select holidayDate, changeType from kq_holidayset where holidayDate like '" + startLike +
                "' or holidayDate like '" + endLike + "' ORDER BY holidayDate";
        MY_LOG.info("查询节假日表sql： " + selectSql);
        recordSet.executeQuery(selectSql);
        // 工作日集合
        List<String> workdaysList = new ArrayList<>();
        // 休息日集合（节假日 + 调配休息日）
        List<String> restList = new ArrayList<>();
        while (recordSet.next()) {
            // 1-公众假日 2-调配工作日 3-调配休息日
            int changeType = recordSet.getInt("changeType");
            String holidayDate = recordSet.getString("holidayDate");
            if (changeType == 2) {
                workdaysList.add(holidayDate);
            } else {
                restList.add(holidayDate);
            }
        }

        // 工作日天数
        int count = 0;
        long betweenDays = DAYS.between(startDate, endDate);
        for (int i = 0; i <= betweenDays; i++) {
            String dataStr = startDate.toString();
            DayOfWeek week = startDate.getDayOfWeek();

            startDate = startDate.plusDays(1);
            if (restList.contains(dataStr)) {
                continue;
            }
            if (workdaysList.contains(dataStr)) {
                count++;
            } else if (week != DayOfWeek.SATURDAY && week != DayOfWeek.SUNDAY) {
                count++;
            }
        }

        return count;
    }

    /**
     * 月份小于10，补0
     */
    private static String addZero(int month) {
        if (month < 10) {
            return "0" + month;
        } else {
            return String.valueOf(month);
        }
    }

    /**
     * 判断是否为工作日
     */
    public static boolean ifWorkday(String oneDay) {
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("select changeType from kq_holidayset where holidayDate = '" + oneDay + "'");
        if (recordSet.next()) {
            // 1-公众假日 2-调配工作日 3-调配休息日
            int changeType = recordSet.getInt("changeType");
            return 2 == changeType;
        } else {
            // 没有人为调整过
            LocalDate localDate = LocalDate.parse(oneDay);
            DayOfWeek week = localDate.getDayOfWeek();
            return week != DayOfWeek.SATURDAY && week != DayOfWeek.SUNDAY;
        }
    }

    /**
     * double 相加
     */
    public static double add(double d1, double d2) {
        d1 = d1 < 0 ? 0 : d1;
        d2 = d2 < 0 ? 0 : d2;
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.add(bd2).doubleValue();
    }


    /**
     * double 相减
     */
    public static double sub(double d1, double d2) {
        d1 = d1 < 0 ? 0 : d1;
        d2 = d2 < 0 ? 0 : d2;
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.subtract(bd2).doubleValue();
    }
}
