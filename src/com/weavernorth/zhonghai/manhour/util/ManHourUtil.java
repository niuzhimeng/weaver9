package com.weavernorth.zhonghai.manhour.util;

import weaver.conn.RecordSet;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class ManHourUtil {

    /**
     * 判断是否Wie工作日
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
