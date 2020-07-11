package com.weavernorth.hualianFlow.myThread;

import com.alibaba.fastjson.JSONObject;
import com.weavernorth.hualianFlow.util.HlConnUtil;
import org.apache.commons.lang.StringUtils;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.TimeUtil;
import weaver.general.Util;
import weaver.hrm.User;

import java.util.HashMap;
import java.util.Map;

public class PushThread extends BaseBean implements Runnable {

    private String requestId; // 请求id
    private String nodeId; // 节点id
    private String nodeName; // 节点名称
    private String operateType; // 操作类型 submit reject
    private User user; // 操作人

    private String tableName; // 流程表名

    private static Map<String, String> logTypeMap = new HashMap<>();

    static {
        logTypeMap.put("0", "批准");
        logTypeMap.put("1", "保存");
        logTypeMap.put("2", "提交");
        logTypeMap.put("3", "退回");
        logTypeMap.put("4", "重新打开");

        logTypeMap.put("5", "删除");
        logTypeMap.put("6", "激活");
        logTypeMap.put("7", "转发");
        logTypeMap.put("9", "批注");
        logTypeMap.put("a", "意见征询");

        logTypeMap.put("b", "意见征询回复");
        logTypeMap.put("e", "强制归档");
        logTypeMap.put("h", "转办");
        logTypeMap.put("i", "干预");
        logTypeMap.put("j", "转办反馈");

        logTypeMap.put("s", "督办");
        logTypeMap.put("t", "抄送");
    }

    @Override
    public void run() {
        try {
            this.writeLog("异步推送签字意见Start================" + requestId);
            Thread.sleep(3000);
            String currentDate = TimeUtil.getCurrentDateString().replace("-", "");
            RecordSet updateSet = new RecordSet();
            RecordSet recordSet = new RecordSet();
            // 查询已推送logid
            recordSet.executeQuery("select logid from " + tableName + " where requestid = ?", requestId);
            recordSet.next();
            String alreadyLogId = Util.null2String(recordSet.getString("logid")).trim();

            recordSet.executeQuery("SELECT r.logid,r.requestid,r.nodeid,r.logtype,r.operatedate + ' ' + r.operatetime operdatetime,r.operator,r.receivedpersonids,r.receivedPersons,r.remark,r.destnodeid," +
                    "n.nodename destnodename,n.isstart,n.isend FROM workflow_requestLog r LEFT JOIN workflow_nodebase n ON r.destnodeid = n.id WHERE r.requestid = ? AND r.nodeid = ? " +
                    "ORDER BY operdatetime ASC", requestId, nodeId);
            // 本次推送的logId
            StringBuilder stringBuilder = new StringBuilder();
            while (recordSet.next()) {
                String currentLogId = recordSet.getString("logid"); // 日志id，防止重复推送
                if (alreadyLogId.contains("," + currentLogId + ",")) {
                    continue;
                }
                stringBuilder.append(currentLogId).append(",");

                String logType = logTypeMap.get(recordSet.getString("logtype")); // 操作类型
                String operDatetime = recordSet.getString("operdatetime"); // 操作时间
                String operator = recordSet.getString("operator"); // 操作者
                String receivedpersonids = recordSet.getString("receivedpersonids"); // 接收人id
                String receivedPersons = recordSet.getString("receivedPersons"); // 接收人姓名
                if (receivedPersons.endsWith(",")) {
                    receivedPersons = receivedPersons.substring(0, receivedPersons.length() - 1);
                }

                String remark = HlConnUtil.getRemarkStr(recordSet.getString("remark")); // 签字意见
                String destnodeid = recordSet.getString("destnodeid"); // 下一节点id
                String destnodename = recordSet.getString("destnodename"); // 下一节点名称
                String isstart = recordSet.getString("isstart"); // 下一节点是否为创建节点 0：否，1：是
                String isend = recordSet.getString("isend"); // 下一节点是否为归档节点	 0：否，1：是

                String destNodeType = getDestNodeType(isstart, isend);
                String operatorName = getColumn("lastname", "hrmresource", "id", operator); // 操作者姓名
                this.writeLog("--------------------------");
                this.writeLog("节点名称： " + nodeName + " 操作类型： " + logType + " 操作时间： " + operDatetime + " 操作者id： " + operator +
                        "操作者姓名：" + operatorName + " 接收人id： " + receivedpersonids + " 接收人姓名： " + receivedPersons + " 签字意见： " +
                        remark + " 下一节点id: " + destnodeid + " 下一节点名称: " + destnodename + " 下一节点类型: " + destNodeType);

                JSONObject sendJsonObj = new JSONObject(true);
                sendJsonObj.put("appid", HlConnUtil.APP_ID);
                sendJsonObj.put("ts", currentDate);
                sendJsonObj.put("sig", HlConnUtil.getSig());
                sendJsonObj.put("format", "json");
                sendJsonObj.put("userip", HlConnUtil.USER_IP);

                JSONObject dataJsonObj = new JSONObject(true);
                dataJsonObj.put("ExtInstanceID", requestId);
                dataJsonObj.put("Result", getSendType(isstart, isend)); // 1:审批通过；2：驳回；3：超时；4：最终审批通过
                dataJsonObj.put("CreateDate", TimeUtil.getCurrentTimeString());
                dataJsonObj.put("LoginName", user.getLoginid());
                dataJsonObj.put("Description", "节点名称： " + nodeName + "；操作类型： " + logType +
                        "；操作人：" + operatorName + "；签字意见： " + remark + "；接收人：" + receivedPersons +
                        "；下节点名称： " + destnodename + "；下节点类型： " + destNodeType);

                sendJsonObj.put("Approve", dataJsonObj);

                String sendJsonStr = sendJsonObj.toJSONString();
                this.writeLog("签字意见接口发送数据：" + sendJsonStr);

                // 调用签字意见接口
                String returnJson = HlConnUtil.sendPost(HlConnUtil.URL, sendJsonStr);
                this.writeLog("推送签字意见接口返回： " + returnJson);
//
//                JSONObject returnJsonObj = JSONObject.parseObject(returnJson);
//                String code = returnJsonObj.getString("code");
//                if (!"200".equals(code)) {
//                    // 写入错误信息表
//                }
            }

            // 更新-已推送logId
            String logIds = stringBuilder.toString();
            if (StringUtils.isBlank(alreadyLogId)) {
                logIds = "," + logIds;
            } else {
                logIds = alreadyLogId + logIds;
            }

            updateSet.executeUpdate("update " + tableName + " set logid = ? where requestid = ?", logIds.trim(), requestId);

            this.writeLog("异步推送签字意见End================");
        } catch (Exception e) {
            this.writeLog("异步推送推送状态异常： " + e);
        }
    }

    /**
     * 获取发送类型
     * 1:审批通过；2：驳回；3：超时；4：最终审批通过
     */
    private String getSendType(String isStart, String isEnd) {
        String nodeType = "1";
        if ("1".equals(isStart)) {
            nodeType = "2";
        } else if ("1".equals(isEnd)) {
            nodeType = "4";
        }
        return nodeType;
    }

    /**
     * 获取下一节点类型
     */
    private String getDestNodeType(String isStart, String isEnd) {
        String nodeType = "中间节点";
        if ("1".equals(isStart)) {
            nodeType = "创建节点";
        } else if ("1".equals(isEnd)) {
            nodeType = "归档节点";
        }

        return nodeType;
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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
