package com.weavernorth.hualianFlow.myThread;

import com.weavernorth.hualianFlow.util.HlConnUtil;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;

import java.util.HashMap;
import java.util.Map;

public class PushThread extends BaseBean implements Runnable {

    private String requestId; // 请求id
    private String nodeId; // 节点id
    private String nodeName; // 节点名称
    private String operateType; // 操作类型 submit reject

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
            Thread.sleep(3000);
            this.writeLog("nodeId: " + nodeId + " nodeName: " + nodeName);
            RecordSet recordSet = new RecordSet();
            recordSet.executeQuery("SELECT requestid,nodeid,logtype,operatedate + ' ' + operatetime operdatetime,operator," +
                    " receivedpersonids,receivedPersons,remark FROM workflow_requestLog WHERE requestid = ? AND nodeid = ? " +
                    " ORDER BY operdatetime ASC", this.requestId, this.nodeId);
            while (recordSet.next()) {
                String logType = logTypeMap.get(recordSet.getString("logtype")); // 操作类型
                String operDatetime = recordSet.getString("operdatetime"); // 操作时间
                String operator = recordSet.getString("operator"); // 操作者
                String receivedpersonids = recordSet.getString("receivedpersonids"); // 接收人id
                String receivedPersons = recordSet.getString("receivedPersons"); // 接收人姓名

                String remark = HlConnUtil.getRemarkStr(recordSet.getString("remark")); // 签字意见
                this.writeLog("操作类型： " + logType + " 操作时间： " + operDatetime + " 操作者： " + operator);
                this.writeLog("接收人id： " + receivedpersonids + " 接收人姓名： " + receivedPersons + " 签字意见： " + remark);
            }

//            JSONObject sendJsonObj = new JSONObject(true);
//            sendJsonObj.put("appid", HlConnUtil.APP_ID);
//            sendJsonObj.put("ts", TimeUtil.getCurrentDateString().replace("-", ""));
//            sendJsonObj.put("sig", HlConnUtil.getSig());
//            sendJsonObj.put("format", "json");
//            sendJsonObj.put("userip", HlConnUtil.USER_IP);
//
//            JSONObject dataJsonObj = new JSONObject(true);
//            dataJsonObj.put("ExtInstanceID", requestId);
//            dataJsonObj.put("Result", "1"); // 1:审批通过；2：驳回；3：超时；4：最终审批通过
//            dataJsonObj.put("CreateDate", TimeUtil.getCurrentTimeString());
//            dataJsonObj.put("LoginName", "");
//            dataJsonObj.put("Description", "remarkStr");
//
//            sendJsonObj.put("Approve", dataJsonObj);
//
//            String sendJsonStr = sendJsonObj.toJSONString();
//            this.writeLog("签字意见接口发送数据：" + sendJsonStr);
//
//            // 调用签字意见接口
//            String returnJson = HlConnUtil.sendPost(HlConnUtil.URL, sendJsonStr);
//            this.writeLog("推送签字意见接口返回： " + returnJson);
//
//            JSONObject returnJsonObj = JSONObject.parseObject(returnJson);
//            String code = returnJsonObj.getString("code");
//            if (!"200".equals(code)) {
//                // 写入错误信息表
//            }
        } catch (Exception e) {
            this.writeLog("推送状态异常： " + e);
        }
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
