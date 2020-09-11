package com.weavernorth.zhonghai.manhour;

import com.weavernorth.zhonghai.manhour.util.ManHourUtil;
import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

/**
 * 工时控制流程-退回
 */
public class ManHourReject extends BaseAction {

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

        this.writeLog("工时控制流程-退回 Start requestid=" + requestId + "  operatetype --- " + operateType + "   fromTable --- " + tableName);
        try {

            // 查询主表
            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            recordSet.next();
            String bglx = recordSet.getString("bglx"); // 报工类型
            String sfgzr = recordSet.getString("sfgzr"); // 是否工作日

            if (!"0".equals(bglx) || !"0".equals(sfgzr)) {
                // 不是项目 跳过 || 不为工作日
                return "1";
            }

            double zcgs = recordSet.getDouble("zcgs"); // 正常工时
            String bgr = recordSet.getString("bgr"); // 报工人
            String hdbh = recordSet.getString("hdbh"); // 项目编号
            String xmlx = recordSet.getString("xmlx"); // 项目类型
            String jdmc = recordSet.getString("jdmc"); // 阶段名称

            this.writeLog("报工人: " + bgr + " 项目编号: " + hdbh + " 项目类型: " + xmlx + " 阶段名称: " + jdmc + " 正常工时: " + zcgs);

            // 查询建模表
            recordSet.executeQuery("select * from uf_xmcyjl where xmbh = ? and xmlx = ? and jdmc = ? and xmcy = ?",
                    hdbh, xmlx, jdmc, bgr);
            recordSet.next();
            double sykbgs = recordSet.getDouble("sykbgs"); // 剩余可报工时
            double spzgs = recordSet.getDouble("spzgs"); // 审批中工时

            sykbgs = ManHourUtil.add(sykbgs, zcgs);
            spzgs = ManHourUtil.sub(spzgs, zcgs);

            recordSet.executeUpdate("update uf_xmcyjl set sykbgs = ?, spzgs = ? where xmbh = ? and xmlx = ? and jdmc = ? and xmcy = ?",
                    sykbgs, spzgs,
                    hdbh, xmlx, jdmc, bgr);

            this.writeLog("工时控制流程-退回 End ===============");
        } catch (Exception e) {
            this.writeLog("工时控制流程-退回 异常： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("工时控制流程-退回 异常： " + e);
            return "0";
        }

        return "1";
    }
}
