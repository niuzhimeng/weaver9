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
    /**
     * 创建人id（固定值：张益源）
     */
    private static final String PERSON_ID = "29";

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

            RecordSet depSet = new RecordSet();
            depSet.executeQuery("select departmentid from HrmResource where id = " + PERSON_ID);
            depSet.next();
            String depId = depSet.getString("departmentid");

            WorkflowRequestTableField[] mainField = new WorkflowRequestTableField[15]; //主表行对象

            int i = 0;
            mainField[i] = new WorkflowRequestTableField();
            mainField[i].setFieldName("mdmc");// 字段名
            baseBean.writeLog("店铺名称: " + recordSet.getString("dpmc"));
            mainField[i].setFieldValue(recordSet.getString("dpmc")); // 店铺名称
            mainField[i].setView(true); //字段是否可见
            mainField[i].setEdit(true); //字段是否可编辑

            i++;
            mainField[i] = new WorkflowRequestTableField();
            mainField[i].setFieldName("mdbh");
            baseBean.writeLog("门店编号: " + recordSet.getString("mdbh"));
            mainField[i].setFieldValue(recordSet.getString("mdbh")); // 门店编号
            mainField[i].setView(true);
            mainField[i].setEdit(true);

            i++;
            mainField[i] = new WorkflowRequestTableField();
            mainField[i].setFieldName("mdlx");
            String bzdmnd = recordSet.getString("bzdmnd");
            baseBean.writeLog("标准店/迷你店: " + bzdmnd);
            mainField[i].setFieldValue(bzdmnd); // 标准店/迷你店
            mainField[i].setView(true);
            mainField[i].setEdit(true);

            i++;
            mainField[i] = new WorkflowRequestTableField();
            mainField[i].setFieldName("qytz");
            baseBean.writeLog("区域拓展: " + recordSet.getString("qytz"));
            mainField[i].setFieldValue(recordSet.getString("qytz")); // 区域拓展
            mainField[i].setView(true);
            mainField[i].setEdit(true);

            i++;
            mainField[i] = new WorkflowRequestTableField();
            mainField[i].setFieldName("mdsjkyrq");
            String mdsjkyrq = recordSet.getString("mdsjkyrq");
            baseBean.writeLog("门店实际开业日期: " + mdsjkyrq);
            mainField[i].setFieldValue(mdsjkyrq); // 门店实际开业日期
            mainField[i].setView(true);
            mainField[i].setEdit(true);

            i++;
            mainField[i] = new WorkflowRequestTableField();
            mainField[i].setFieldName("fqr");
            mainField[i].setFieldValue(PERSON_ID); // 创建人（固定张益源）
            mainField[i].setView(true);
            mainField[i].setEdit(true);

            i++;
            mainField[i] = new WorkflowRequestTableField();
            mainField[i].setFieldName("fqbm");
            mainField[i].setFieldValue(depId); // 发起人部门
            mainField[i].setView(true);
            mainField[i].setEdit(true);

            i++;
            mainField[i] = new WorkflowRequestTableField();
            mainField[i].setFieldName("fqrq");
            mainField[i].setFieldValue(LocalDate.now().toString()); // 发起日期
            mainField[i].setView(true);
            mainField[i].setEdit(true);

            i++;
            mainField[i] = new WorkflowRequestTableField();
            mainField[i].setFieldName("scbtyf");
            String firstMonth = getFirstMonth(bzdmnd, mdsjkyrq);
            baseBean.writeLog("首次补贴月份： " + firstMonth);
            mainField[i].setFieldValue(firstMonth); // 首次补贴月份
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
            workflowRequestInfo.setCreatorId(PERSON_ID);// 创建人id
            workflowRequestInfo.setRequestLevel("0");// 0 正常，1重要，2紧急
            workflowRequestInfo.setRequestName("ZC-04开业补贴申请" + "-张益源-" + LocalDate.now());// 流程标题
            workflowRequestInfo.setWorkflowBaseInfo(workflowBaseInfo);
            workflowRequestInfo.setWorkflowMainTableInfo(workflowMainTableInfo);// 添加主表字段数据
            workflowRequestInfo.setIsnextflow("0");

            //创建流程的类
            WorkflowServiceImpl service = new WorkflowServiceImpl();
            String requestId = service.doCreateWorkflowRequest(workflowRequestInfo, Integer.parseInt(PERSON_ID));

            baseBean.writeLog("ZC-04开业补贴申请创建流程完毕===============" + requestId);

        } catch (Exception e) {
            baseBean.writeLog("ZC-04开业补贴申请： " + e);
        }
    }


    /**
     * 获取首次补贴日期
     *
     * @param dpType    店铺类型
     * @param startDate 实际开业日期
     * @return
     */
    private String getFirstMonth(String dpType, String startDate) {
        String returnStr;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parse = LocalDate.parse(startDate, formatter);
        if ("0".equals(dpType)) {
            // 标准店
            returnStr = parse.plusMonths(3).toString();
        } else {
            returnStr = parse.plusMonths(6).toString();
        }
        return returnStr;
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
