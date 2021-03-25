package com.weavernorth.guohang.timed;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.RecordSet;
import weaver.conn.RecordSetDataSource;
import weaver.general.TimeUtil;
import weaver.interfaces.schedule.BaseCronJob;

import java.util.ArrayList;
import java.util.List;

/**
 * 读取两个数据源中的两个视图，生成一个报表
 */
public class CreateReportBySource extends BaseCronJob {

    private static final Log MY_LOG = LogFactory.getLog(CreateReportBySource.class);

    @Override
    public void execute() {
        MY_LOG.info("生成报表计划任务Start========" + TimeUtil.getCurrentTimeString());

        try {
            // 类型 + 部门 + 年 + 月 确定一条唯一数据
            RecordSet updateErp = new RecordSet();
            updateErp.executeQuery("select lx, bm, n, y from uf_tbscdj");
            int erpCounts = updateErp.getCounts();
            List<String> erpExistList = new ArrayList<>(erpCounts); // 生产单价实体表中已存在的数据
            while (updateErp.next()) {
                erpExistList.add(updateErp.getString("lx") + updateErp.getString("bm")
                        + updateErp.getString("n") + updateErp.getString("y"));
            }
            //MY_LOG.info("erpExistList: " + JSONObject.toJSONString(erpExistList));

            // 生产单价虚拟表（数据源是ERP）
            RecordSetDataSource erpSource = new RecordSetDataSource("ERP");
            erpSource.executeSql("SELECT DATA_TYPE AS lx, COMPANY AS bm,SUBSTR( PERIOD_NAME, 1, 4 ) AS YEAR,SUBSTR( PERIOD_NAME, 5, 2 ) AS MONTH,round( PROD_PRICE, 4 ) AS PROD_PRICE,round( NO_PROD_PRICE, 4 ) AS NO_PROD_PRICE FROM apps.cux_fcm_oil_purchars_jnhb_v");

            MY_LOG.info("生产单价虚拟表数量： " + erpSource.getCounts() + ", oa实体表已有数据量：" + erpCounts);
            String insertErpSql = "insert into uf_tbscdj(lx, bm, n, y, scdj, fscdj)values(?,?,?,?,?, ?)";
            String updateErpSql = "update uf_tbscdj set scdj = ?, fscdj = ? where lx = ? and bm = ? and n = ? and y = ?";
            while (erpSource.next()) {
                String lx = erpSource.getString("lx"); // 类型
                String bm = erpSource.getString("bm"); // 部门
                String year = erpSource.getString("YEAR"); // 年
                String month = monthDeleteZero(erpSource.getString("MONTH")); // 月
                String prod_price = null2Double(erpSource.getString("PROD_PRICE"), "0.0000"); // 生产单价

                String NO_PROD_PRICE = null2Double(erpSource.getString("NO_PROD_PRICE"), "0.0000"); // 非生产单价

                // MY_LOG.info("lx+bm + year + month: " + lx + bm + year + month + "; contains: " + erpExistList.contains(lx + bm + year + month));
                if (erpExistList.contains(lx + bm + year + month)) {
                    updateErp.executeUpdate(updateErpSql, prod_price, NO_PROD_PRICE,
                            lx, bm, year, month);
                } else {
                    updateErp.executeUpdate(insertErpSql, lx, bm, year, month, prod_price, NO_PROD_PRICE);
                }

            }

            // 生产数据虚拟表（数据源是GHSCTJ）
            RecordSet updateGHSCTJ = new RecordSet();
            updateGHSCTJ.executeQuery("select lx, bm, n, y from uf_tbscsj");
            int counts = updateGHSCTJ.getCounts();
            List<String> GHSCTJExistList = new ArrayList<>(counts); // 生产数据实体表中已存在的数据
            while (updateGHSCTJ.next()) {
                GHSCTJExistList.add(updateGHSCTJ.getString("lx") + updateGHSCTJ.getString("bm") +
                        updateGHSCTJ.getString("n") + updateGHSCTJ.getString("y"));
            }

            RecordSetDataSource GHSCTJSource = new RecordSetDataSource("GHSCTJ");
            GHSCTJSource.executeSql("SELECT LX AS lx,FXD AS bm,SUBSTR( NY, 1, 4 ) AS YEAR,SUBSTR( NY, 5, 2 ) AS MONTH,round( SCHY, 4 ) AS schbxhl,round( FSCHY, 4 ) AS fschbxhl,round( FSCHY + SCHY, 4 ) AS hkmyxhl,round( SCZZL, 2 ) AS yszzl,round( SCXS, 2 ) AS fxxs FROM acepub.v_fls_ljyt");

            MY_LOG.info("生产数据虚拟表数量： " + GHSCTJSource.getCounts() + ", oa实体表已有数据量：" + counts);
            String insertGHSCTJ_sql = "insert into uf_tbscsj(lx, bm, n, y, schbxhl, " +
                    "fschbxhl, hkmyxhl, yszzl, fxxs)values(?,?,?,?,?, ?,?,?,?)";
            String updateGHSCTJ_sql = "update uf_tbscsj set schbxhl = ?, fschbxhl = ?, hkmyxhl = ?, yszzl = ?, fxxs = ? " +
                    " where lx = ? and bm = ? and n = ? and y = ?";
            while (GHSCTJSource.next()) {
                String lx = GHSCTJSource.getString("lx"); // 类型
                String bm = GHSCTJSource.getString("bm"); // 部门
                String year = GHSCTJSource.getString("YEAR"); // 年
                String month = monthDeleteZero(GHSCTJSource.getString("MONTH")); // 月
                String schbxhl = null2Double(GHSCTJSource.getString("schbxhl"), "0.0000"); // 生产航班消耗量

                String fschbxhl = null2Double(GHSCTJSource.getString("fschbxhl"), "0.0000"); // 非生产航班消耗量
                String hkmyxhl = null2Double(GHSCTJSource.getString("hkmyxhl"), "0.0000"); // 航空煤油消耗量
                String yszzl = null2Double(GHSCTJSource.getString("yszzl"), "0.00"); // 运输周转量
                String fxxs = null2Double(GHSCTJSource.getString("fxxs"), "0.00"); // 飞行小时数

                if (GHSCTJExistList.contains(lx + bm + year + month)) {
                    updateGHSCTJ.executeUpdate(updateGHSCTJ_sql,
                            schbxhl, fschbxhl, hkmyxhl, yszzl, fxxs,
                            lx, bm, year, month
                    );
                } else {
                    updateGHSCTJ.executeUpdate(insertGHSCTJ_sql,
                            lx, bm, year, month, schbxhl,
                            fschbxhl, hkmyxhl, yszzl, fxxs);
                }

            }

            // 拼接报表
            // createReport();
        } catch (Exception e) {
            MY_LOG.info("抓取两个虚拟表中数据异常：" + e);
        }

        MY_LOG.info("生成报表计划任务End========" + TimeUtil.getCurrentTimeString());
    }

    private void createReport() {
        try {
            MY_LOG.info("拼接报表开始===========");
            RecordSet updateSet = new RecordSet();
            RecordSet recordSet = new RecordSet();
            recordSet.executeQuery("select bm, n ,y from uf_xxxx");
            int counts = recordSet.getCounts();
            List<String> reportExistList = new ArrayList<>(counts); // oa报表表中已存在的数据
            while (recordSet.next()) {
                reportExistList.add(recordSet.getString("bm") + recordSet.getString("n") + recordSet.getString("y"));
            }

            MY_LOG.info("oa报表已有数据量：" + counts);
            String insertReport = "insert into uf_xxx(lx, bm, n, y, schbxhl, " +
                    "fschbxhl, hkmyxhl, yszzl, fxxs)values(?,?,?,?,?, ?,?,?,?)";

            recordSet.executeQuery(""); // 联合两张实体表查询
            while (recordSet.next()) {
                String bm = recordSet.getString("bm");


                if (reportExistList.contains(bm)) {
                    continue;
                }
                updateSet.executeUpdate(insertReport);

            }

        } catch (Exception e) {
            MY_LOG.info("生成报表异常： " + e);
        }
    }

    /**
     * 如果值为空，则返回代替值
     */
    private String null2Double(String value, String replaceValue) {
        if (StringUtils.isBlank(value)) {
            return replaceValue;
        } else {
            return value;
        }
    }

    /**
     * 如果月份是小于10的，删除前面的0（数据源获取的是01,02这种格式，oa表中是整数字段，获取到的是1,2）
     */
    private String monthDeleteZero(String month) {
        if (month.startsWith("0")) {
            month = month.substring(1);
        }
        return month;
    }

}


