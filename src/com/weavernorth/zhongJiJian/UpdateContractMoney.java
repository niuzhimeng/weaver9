package com.weavernorth.zhongJiJian;

import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

import java.math.BigDecimal;

/**
 * 更改【工程合同变更后造价】
 * 基本信息Tab页面的 工程合同变更后造价gcbghhtzj = 基本信息Tab页面的工程合同造价gchtzjy + 主合同修改信息Tab页面的金额合计；
 * <p>
 * 流程表的【原合同名称-yhtmc】——基本信息建模表uf_gcjbxxwh【合同名称-htmc】——
 */
public class UpdateContractMoney extends BaseAction {

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

        this.writeLog("更改【工程合同变更后造价】 Start requestid=" + requestId + "  operatetype --- " + operateType + "   fromTable --- " + tableName);
        try {
            // 查询本次变更合同id
            recordSet.executeQuery("select yhtmc from " + tableName + " where requestid = '" + requestId + "'");
            recordSet.next();
            String yhtmc = recordSet.getString("yhtmc"); // 原合同名称（主键是id）
            this.writeLog("原合同名称: " + yhtmc);

            // 查询基本信息表
            recordSet.executeQuery("select gchtzjy from uf_gcjbxxwh where htmc = '" + yhtmc + "'");
            recordSet.next();
            double gchtzjy = recordSet.getDouble("gchtzjy") < 0 ? 0 : recordSet.getDouble("gchtzjy"); // 工程合同造价（元）
            this.writeLog("工程合同造价（元）: " + gchtzjy);
            BigDecimal add1 = BigDecimal.valueOf(gchtzjy);

            // 查询建模表中  改合同编号对应的多条修改记录合计
//            BigDecimal add2 = BigDecimal.ZERO;
//            recordSet.executeQuery("select * from formtable_main_45 where yhtmc = '" + yhtmc + "'");
//            while (recordSet.next()) {
//                double je = recordSet.getDouble("je") < 0 ? 0 : recordSet.getDouble("je");
//                if (je > 0) {
//                    add2 = add2.add(BigDecimal.valueOf(je));
//                }
//            }
            recordSet.executeQuery("select sum(je) myCount from formtable_main_45 where yhtmc = '" + yhtmc + "' and requestid is null");
            recordSet.next();
            double myCount = recordSet.getDouble("myCount") < 0 ? 0 : recordSet.getDouble("myCount");
            this.writeLog("修改列表金额合计： " + myCount);
            BigDecimal add2 = BigDecimal.valueOf(myCount);

            BigDecimal result = add1.add(add2);
            result = result.setScale(2, BigDecimal.ROUND_HALF_UP);//保留两位小数
            double value = result.doubleValue();
            this.writeLog("工程合同变更后造价：" + value);

            // 更改基本信息表中的 工程合同变更后造价
            updateSet.executeUpdate("update uf_gcjbxxwh set gcbghhtzj = ? where htmc = ?",
                    value, yhtmc);

            this.writeLog("更改【工程合同变更后造价】 End ===============");
        } catch (Exception e) {
            this.writeLog("更改【工程合同变更后造价】 Error： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("更改【工程合同变更后造价】 Error： " + e);
            return "0";
        }

        return "1";
    }
}
