package com.weavernorth.zhongDk.workflow;

import com.alibaba.fastjson.JSONObject;
import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 明细表字段转主表
 */
public class DetailToMainAction extends BaseAction {

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

        this.writeLog("明细表字段转主表 Start requestid=" + requestId + "  operatetype --- " + operateType + "   fromTable --- " + tableName);
        try {
            // 查询主表
            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            recordSet.next();
            String wlfl = recordSet.getString("wlfl"); // 物料分类
            String mainId = recordSet.getString("id");

            if (wlfl.contains("_")) {
                wlfl = wlfl.split("_")[1];
            }

            // 查询表单明细
            Map<String, String> map = new HashMap<>(); // 字段名- 字段值
            recordSet.executeQuery("select * from " + tableName + "_dt1 where mainid = " + mainId);
            while (recordSet.next()) {
                map.put(recordSet.getString("oazdm"), recordSet.getString("zdz"));
            }

            this.writeLog("明细表数据：" + JSONObject.toJSONString(map));

            // 查找对照表中字段（oazdm OA字段名，与主表字段名一致）
            StringJoiner zdJoiner = new StringJoiner(",");
            recordSet.executeQuery("select b.oazdm from uf_wlfldzb a right join uf_wlfldzb_dt1 b on a.id = b.mainid where a.wlfl like '%," + wlfl + ",%'");
            int counts = recordSet.getCounts();
            this.writeLog("物料分类：" + wlfl + ", 配置字段数量： " + counts);

            Object[] values = new String[counts];
            int i = 0;
            while (recordSet.next()) {
                String oazdm = recordSet.getString("oazdm");
                values[i] = map.get(oazdm);
                zdJoiner.add(oazdm + " = ?");
                i++;
            }
            this.writeLog("更新主表value部分： " + JSONObject.toJSONString(values));

            // 插入主表sql
            String updateMainSql = "update " + tableName + " set " + zdJoiner.toString() + " where requestid = " + requestId;
            this.writeLog("更新主表字段sql： " + updateMainSql);

            // 更新明细中字段到主表，为了流程转数据使用
            updateSet.executeUpdate(updateMainSql, values);

            this.writeLog("明细表字段转主表 End ===============");
        } catch (Exception e) {
            this.writeLog("明细表字段转主表 Error： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("明细表字段转主表 Error： " + e);
            return "0";
        }

        return "1";
    }
}
