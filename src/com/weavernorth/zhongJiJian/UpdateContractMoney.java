package com.weavernorth.zhongJiJian;

import org.apache.commons.lang3.StringUtils;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
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

    public static BaseBean baseBean = new BaseBean();

    @Override
    public String execute(RequestInfo requestInfo) {
        String requestId = requestInfo.getRequestid();
        String operateType = requestInfo.getRequestManager().getSrc();
        int formId = requestInfo.getRequestManager().getFormid();
        String tableName = "";
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
            updateMode(yhtmc);
            this.writeLog("更改【工程合同变更后造价】 End ===============");
        } catch (Exception e) {
            this.writeLog("更改【工程合同变更后造价】 Error： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("更改【工程合同变更后造价】 Error： " + e);
            return "0";
        }

        return "1";
    }

    public static double updateMode(String yhtmc) throws Exception {
        RecordSet recordSet = new RecordSet();
        // 查询基本信息表
        recordSet.executeQuery("select hte from uf_gcjbxxwh where htmc = '" + yhtmc + "'");
        recordSet.next();
        String gchtzjyStr = recordSet.getString("hte"); // 工程合同造价（元）
        if (StringUtils.isBlank(gchtzjyStr)) {
            gchtzjyStr = "0";
        }
        baseBean.writeLog("工程合同造价（元）: " + gchtzjyStr);
        BigDecimal add1 = new BigDecimal(gchtzjyStr);

        String myCountStr = "0";
        recordSet.executeQuery("select sum(je) myCount from formtable_main_45 where yhtmc = '" + yhtmc + "' and requestid is null");
        if (recordSet.next()) {
            myCountStr = recordSet.getString("myCount");
            if (StringUtils.isBlank(myCountStr)) {
                myCountStr = "0";
            }
        }
        baseBean.writeLog("修改列表金额合计： " + myCountStr);

        BigDecimal result = add1.add(new BigDecimal(myCountStr));
        result = result.setScale(2, BigDecimal.ROUND_HALF_UP);//保留两位小数
        double value = result.doubleValue();
        baseBean.writeLog("工程合同变更后造价：" + value);

        // 更改基本信息表中的 工程合同变更后造价
        recordSet.executeUpdate("update uf_gcjbxxwh set htbgje = ? where htmc = ?",
                value, yhtmc);

        return value;
    }
}
