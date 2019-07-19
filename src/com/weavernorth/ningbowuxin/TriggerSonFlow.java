package com.weavernorth.ningbowuxin;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.TimeUtil;
import weaver.interfaces.schedule.BaseCronJob;
import weaver.workflow.webservices.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 定时触发子流程(ZC-03开业准备 -> ZC-04开业补贴申请)
 */
public class TriggerSonFlow extends BaseCronJob {

    private BaseBean baseBean = new BaseBean();
    /**
     * 主流程表单名称
     */
    private static final String MAIN_TABLE_NAME = "formtable_main_18";
    /**
     * 子流程id
     */
    private static final String SON_FLOW_ID = "44";

    private RecordSet updateRecordSet = new RecordSet();

    @Override
    public void execute() {
        baseBean.writeLog("TriggerSonFlow定时触发子流程 Start： " + TimeUtil.getCurrentTimeString());
        RecordSet mainRecordSet = new RecordSet();

        try {
            // 查询已归档的主流程 - 标准店(发起4次)
            String standardSql = "select f.* from " + MAIN_TABLE_NAME + " f left join workflow_requestbase w " +
                    "on w.requestid = f.requestId where w.currentnodetype = 3 and f.bzdmnd = 0 and f.cfcs < 5";
            baseBean.writeLog("查询已归档的主流程 - 标准店sql: " + standardSql);

            mainRecordSet.executeQuery(standardSql);
            baseBean.writeLog("查询已归档的主流程数量：" + mainRecordSet.getCounts());

            while (mainRecordSet.next()) {
                // 开店日期加 3 * N 个月
                int anInt = mainRecordSet.getInt("cfcs");
                LocalDate dateAddN = dateAddN(mainRecordSet.getString("mdsjkyrq"), anInt * 3);

                if (LocalDate.now().equals(dateAddN)) {
                    // 到达 3 * N 个月后。触发一条流程
                    triggerFlow(mainRecordSet);
                    // 更新触发次数
                    addCount(mainRecordSet.getString("requestId"), mainRecordSet.getInt("cfcs"));
                }
            }

            // 查询已归档的主流程 - 迷你店(只发起一次)
            RecordSet miniRecordSet = new RecordSet();
            String miniSql = "select f.* from " + MAIN_TABLE_NAME + " f left join workflow_requestbase w " +
                    "on w.requestid = f.requestId where w.currentnodetype = 3 and f.bzdmnd = 1 and f.cfcs < 1";
            baseBean.writeLog("查询已归档的主流程 - 迷你店sql: " + miniSql);
            miniRecordSet.executeQuery(miniSql);
            while (miniRecordSet.next()) {
                // 开店日期加6个月
                LocalDate dateAddN = dateAddN(miniRecordSet.getString("mdsjkyrq"), 6);

                if (LocalDate.now().equals(dateAddN)) {
                    // 到达六个月后。触发一条流程
                    triggerFlow(miniRecordSet);
                    // 更新触发次数
                    addCount(miniRecordSet.getString("requestId"), miniRecordSet.getInt("cfcs"));
                }

            }
        } catch (Exception e) {
            baseBean.writeLog("TriggerSonFlow定时触发子流程 Error: " + e);
        }
        baseBean.writeLog("TriggerSonFlow定时触发子流程 End： " + TimeUtil.getCurrentTimeString());
    }


    private void triggerFlow(RecordSet recordSet) {
        try {
            baseBean.writeLog("创建流程开始===============");
            // 创建人id
            String creatorId = "29";

            WorkflowRequestTableField[] mainField = new WorkflowRequestTableField[15]; //主表行对象

            int i = 0;
            mainField[i] = new WorkflowRequestTableField();
            mainField[i].setFieldName("mdmc");// 字段名
            mainField[i].setFieldValue(recordSet.getString("dpmc")); // 店铺名称
            mainField[i].setView(true); //字段是否可见
            mainField[i].setEdit(true); //字段是否可编辑

            i++;
            mainField[i] = new WorkflowRequestTableField();
            mainField[i].setFieldName("mdbh");
            mainField[i].setFieldValue(recordSet.getString("mdbh")); // 门店编号
            mainField[i].setView(true);
            mainField[i].setEdit(true);

            i++;
            mainField[i] = new WorkflowRequestTableField();
            mainField[i].setFieldName("mdlx");
            mainField[i].setFieldValue(recordSet.getString("bzdmnd")); // 标准店/迷你店
            mainField[i].setView(true);
            mainField[i].setEdit(true);

            i++;
            mainField[i] = new WorkflowRequestTableField();
            mainField[i].setFieldName("qytz");
            mainField[i].setFieldValue(recordSet.getString("qytz")); // 区域拓展
            mainField[i].setView(true);
            mainField[i].setEdit(true);

            i++;
            mainField[i] = new WorkflowRequestTableField();
            mainField[i].setFieldName("mdsjkyrq");
            mainField[i].setFieldValue(recordSet.getString("mdsjkyrq")); // 门店实际开业日期
            mainField[i].setView(true);
            mainField[i].setEdit(true);

            WorkflowRequestTableRecord[] mainRecord = new WorkflowRequestTableRecord[1];// 主字段只有一行数据
            mainRecord[0] = new WorkflowRequestTableRecord();
            mainRecord[0].setWorkflowRequestTableFields(mainField);

            WorkflowMainTableInfo workflowMainTableInfo = new WorkflowMainTableInfo();
            workflowMainTableInfo.setRequestRecords(mainRecord);

            //====================================流程基本信息录入
            WorkflowBaseInfo workflowBaseInfo = new WorkflowBaseInfo();
            workflowBaseInfo.setWorkflowId(SON_FLOW_ID);// 流程id

            WorkflowRequestInfo workflowRequestInfo = new WorkflowRequestInfo();// 流程基本信息
            workflowRequestInfo.setCreatorId(creatorId);// 创建人id
            workflowRequestInfo.setRequestLevel("0");// 0 正常，1重要，2紧急
            workflowRequestInfo.setRequestName("ZC-04开业补贴申请" + TimeUtil.getOnlyCurrentTimeString());// 流程标题
            workflowRequestInfo.setWorkflowBaseInfo(workflowBaseInfo);
            workflowRequestInfo.setWorkflowMainTableInfo(workflowMainTableInfo);// 添加主表字段数据
            workflowRequestInfo.setIsnextflow("0");

            //创建流程的类
            WorkflowServiceImpl service = new WorkflowServiceImpl();
            String requestId = service.doCreateWorkflowRequest(workflowRequestInfo, Integer.parseInt(creatorId));

            baseBean.writeLog("ZC-04开业补贴申请创建流程完毕===============" + requestId);

        } catch (Exception e) {
            baseBean.writeLog("ZC-04开业补贴申请： " + e);
        }
    }


    /**
     * 更新触发次数
     */
    private void addCount(String requestId, int count) {
        count += 1;
        updateRecordSet.executeUpdate("update " + MAIN_TABLE_NAME + " set cfcs = " + count + " where requestid = " + requestId);
    }

    /**
     * 日期加上N个月
     *
     * @param dateStr    日期字符串
     * @param monthCount 加的月数
     * @return LocalDate
     */
    private LocalDate dateAddN(String dateStr, int monthCount) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parse = LocalDate.parse(dateStr, formatter);
        return parse.plusMonths(monthCount);
    }
}
