package com.weavernorth.hualianFlow;

import com.weavernorth.hualianFlow.myThread.PushThread;
import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 流程状态推送-异步
 */
public class PushFlowStateAsyn extends BaseAction {

    private static final ExecutorService executorService = new ThreadPoolExecutor(5, 10,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(512));

    @Override
    public String execute(RequestInfo requestInfo) {
        this.writeLog("推送签字意见节点后执行==========");
        String requestId = requestInfo.getRequestid();
        int nodeId = requestInfo.getRequestManager().getNodeid();
        String operateType = requestInfo.getRequestManager().getSrc();
        try {
            String nodeName = getColumn("nodename", "workflow_nodebase", "id", String.valueOf(nodeId));
            PushThread pushThread = new PushThread();
            pushThread.setRequestId(requestId);
            pushThread.setNodeId(String.valueOf(nodeId));
            pushThread.setNodeName(nodeName);
            pushThread.setOperateType(operateType);
            executorService.execute(pushThread);
        } catch (Exception e) {
            this.writeLog("流程状态推送 异常： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("流程状态推送 异常： " + e);
            return "0";
        }
        return "1";
    }

    /**
     * 根据某个字段查询另一个字段
     *
     * @param col        要查询的列名
     * @param tableName  表名
     * @param whereLeft  where条件 例：where name = 'nzm' 中的 name
     * @param whereRight where条件 例：where name = 'nzm' 中的 'nzm'
     */
    private String getColumn(String col, String tableName, String whereLeft, String whereRight) {
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("select " + col + " from " + tableName + " where " + whereLeft + " = '" + whereRight + "'");
        recordSet.next();
        return recordSet.getString(col);
    }
}
