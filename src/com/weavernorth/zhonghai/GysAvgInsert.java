package com.weavernorth.zhonghai;

import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

import java.math.BigDecimal;

/**
 * 供应商信息更改
 * 更新平均分、等级、状态
 */
public class GysAvgInsert extends BaseAction {

    @Override
    public String execute(RequestInfo requestInfo) {
        String requestId = requestInfo.getRequestid();
        String operateType = requestInfo.getRequestManager().getSrc();
        int formId = requestInfo.getRequestManager().getFormid();
        String tableName = "";
        RecordSet updateSet = new RecordSet();
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("SELECT tablename FROM workflow_bill WHERE id = '" + formId + "'");
        if (recordSet.next()) {
            tableName = recordSet.getString("tablename");
        }

        this.writeLog("供应商信息更改 Start requestid=" + requestId + "  operatetype --- " + operateType + "   fromTable --- " + tableName);
        try {
            // 查询主表
            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            recordSet.next();

            String gysmc = recordSet.getString("gysmc");  // 供应商名称

            String selSql = "SELECT gfmc, AVG( pjdf *1.00 ) myAvg FROM uf_gfpjhzb where gfmc = '" + gysmc + "' GROUP BY gfmc";
            this.writeLog("查询当前供应商平均数的sql： " + selSql);
            recordSet.executeQuery(selSql);
            if (recordSet.next()) {
                String myAvg = recordSet.getString("myAvg").equals("") ? "0" : recordSet.getString("myAvg");
                this.writeLog("供应商： " + gysmc + ", 平均分： " + myAvg);

                BigDecimal b = new BigDecimal(myAvg);
                double avgCount = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                String level = getLevel(avgCount);
                String state = getState(level);
                this.writeLog("供应商等级： " + level + ", 供应商状态： " + state);

                updateSet.executeUpdate("update uf_gysxx set myAvg = ?, myLevel = ?, gyszt = ? where id = ?",
                        avgCount, level, state, gysmc);

            }

            this.writeLog("供应商信息更改 End ===============");
        } catch (Exception e) {
            this.writeLog("供应商信息更改 异常： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("供应商信息更改 异常： " + e);
            return "0";
        }

        return "1";
    }

    /**
     * 获取供应商状态
     * AB-合格 CD不合格
     */
    private String getState(String stateStr) {
        String levelStr = "2"; //公共选择框 合格1 不合格2
        if ("A".equals(stateStr) || "B".equals(stateStr)) {
            levelStr = "1";
        }
        return levelStr;
    }

    /**
     * 获取供应商等级
     */
    private String getLevel(double count) {
        String levelStr = "D";
        if (count >= 90) {
            levelStr = "A";
        } else if (count >= 75) {
            levelStr = "B";
        } else if (count >= 60) {
            levelStr = "C";
        }

        return levelStr;
    }

}
