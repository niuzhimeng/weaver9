package com.weavernorth.zhonghai.manhour;

import com.weavernorth.zhonghai.manhour.util.ManHourUtil;
import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 工时控制流程-归档
 */
public class ManHourEnd extends BaseAction {

    @Override
    public String execute(RequestInfo requestInfo) {
        String requestId = requestInfo.getRequestid();
        String operateType = requestInfo.getRequestManager().getSrc();
        int formId = requestInfo.getRequestManager().getFormid();
        String tableName = "";
        RecordSet updateSet = new RecordSet();
        RecordSet detailSet = new RecordSet();
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("SELECT tablename FROM workflow_bill WHERE id = '" + formId + "'");
        if (recordSet.next()) {
            tableName = recordSet.getString("tablename");
        }
        this.writeLog("工时控制流程-归档 Start requestid=" + requestId + "  operatetype --- " + operateType + "   fromTable --- " + tableName);
        try {
            // 查询主表
            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            recordSet.next();
            String bglx = recordSet.getString("bglx"); // 报工类型
            String sfgzr = recordSet.getString("sfgzr"); // 是否工作日
            int bgr = recordSet.getInt("bgr");  // 报工人
            double zcgs = recordSet.getDouble("zcgs") < 0 ? 0 : recordSet.getDouble("zcgs"); // 正常工时
            double jbgs = recordSet.getDouble("jbgs") < 0 ? 0 : recordSet.getDouble("jbgs");  // 加班工时

            String hdbh = recordSet.getString("hdbh");  // 项目编号
            String xmlx = recordSet.getString("xmlx"); // 项目类型
            String jdmc = recordSet.getString("jdmc"); // 阶段名称
            String hdmc = recordSet.getString("hdmc"); // 活动名称
            this.writeLog("报工人: " + bgr + " 项目编号: " + hdbh + " 项目类型: " + xmlx + " 阶段名称: " + jdmc + " 正常工时: " + zcgs +
                    ", 加班工时: " + jbgs + ", 活动名称: " + hdmc);
            // 获取当前考勤区间
            LocalDate[] currentRange = ManHourUtil.getCurrentRange();
            LocalDate startDate = currentRange[0];
            LocalDate endDate = currentRange[1];
            int year = endDate.getYear();
            int month = endDate.getMonthValue();
            String reportRange = startDate.toString() + " 至 " + endDate.toString();
            this.writeLog("报表区间： " + reportRange);
            this.writeLog("报表年： " + year + " 报表月 " + month);

            // 查询工时主表
            detailSet.executeQuery("select * from uf_gsbb where xm = ? and n = ? and y = ?", bgr, year, month);
            int counts = detailSet.getCounts();
            if (!detailSet.next()) {
                requestInfo.getRequestManager().setMessageid("110000");
                requestInfo.getRequestManager().setMessagecontent("员工： " + bgr + " 在考勤区间 " + reportRange + " 中不存在，请联系管理员检查工时报表。");
                return "0";
            }
            if (counts > 1) {
                requestInfo.getRequestManager().setMessageid("110000");
                requestInfo.getRequestManager().setMessagecontent("员工： " + bgr + " 在考勤区间 " + reportRange + " 中存在重复记录，请联系管理员检查工时报表。");
                return "0";
            }
            String id = detailSet.getString("id"); // 主键
            String gh = detailSet.getString("gh"); // 工号
            String szbm = detailSet.getString("szbm"); // 所属部门
            String bzgs = detailSet.getString("bzgs"); // 标准工时

            String insertDetailSql = "insert into uf_gsbb_dt1(xm, gh, szbm, xmbh, xmmc," +
                    " yxgs, bzgs, jbgs, n, y, " +
                    " mainid) values (?,?,?,?,?, ?,?,?,?,?, ?)";
            String updateDetailSql = "update uf_gsbb_dt1 set yxgs = ?, jbgs = ? where mainid = ? and xmbh = ?";
            detailSet.executeQuery("select * from uf_gsbb_dt1 where mainid = ? and xmbh = ?", id, hdbh);
            if (detailSet.next()) {
                this.writeLog("此次项目工时执行【更新】操作======");
                double yxgsMx = detailSet.getDouble("yxgs"); // 有效工时
                double jbgsMx = detailSet.getDouble("jbgs"); // 加班工时
                double addBz = add(yxgsMx, zcgs); // 流程中标准工时 + 明细表中已有标准工时
                double addJb = add(jbgsMx, jbgs);

                updateSet.executeUpdate(updateDetailSql, addBz, addJb, id, hdbh);
            } else {
                this.writeLog("此次项目工时执行【新增】操作======");
                updateSet.executeUpdate(insertDetailSql,
                        bgr, gh, szbm, hdbh, hdmc,
                        zcgs, bzgs, jbgs, year, month,
                        id);
            }

            // 更新工时主表的 【有效工时】【加班工时】
            detailSet.executeQuery("select sum(yxgs) yxgs, sum(jbgs) jbgs from uf_gsbb_dt1 where mainid = ?", id);
            if (detailSet.next()) {
                updateSet.executeUpdate("update uf_gsbb set yxgs = ?, jbgs = ? where id = ?",
                        detailSet.getDouble("yxgs"), detailSet.getDouble("jbgs"), id);
            }

            this.writeLog("工时扣减部分开始============");

            if (!"0".equals(bglx) || !"0".equals(sfgzr)) {
                // 不是项目 跳过 || 不为工作日
                return "1";
            }
            // 查询建模表
            recordSet.executeQuery("select * from uf_xmcyjl where xmbh = ? and xmlx = ? and jdmc = ? and xmcy = ?",
                    hdbh, xmlx, jdmc, bgr);
            recordSet.next();
            double dqybgs = recordSet.getDouble("dqybgs"); // 当前已报工时
            double spzgs = recordSet.getDouble("spzgs"); // 审批中工时

            dqybgs = ManHourUtil.add(dqybgs, zcgs);
            spzgs = ManHourUtil.sub(spzgs, zcgs);

            recordSet.executeUpdate("update uf_xmcyjl set dqybgs = ?, spzgs = ? where xmbh = ? and xmlx = ? and jdmc = ? and xmcy = ?",
                    dqybgs, spzgs,
                    hdbh, xmlx, jdmc, bgr);

            this.writeLog("工时控制流程-归档 End ===============");
        } catch (Exception e) {
            this.writeLog("工时控制流程-归档 异常： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("工时控制流程-归档 异常： " + e);
            return "0";
        }

        return "1";
    }

    private double add(double d1, double d2) {
        d1 = d1 < 0 ? 0 : d1;
        d2 = d2 < 0 ? 0 : d2;
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.add(bd2).doubleValue();
    }

}
