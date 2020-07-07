package com.weavernorth.hualianFlow;

import com.alibaba.fastjson.JSONObject;
import com.weavernorth.hualianFlow.util.HlConnUtil;
import org.apache.commons.codec.binary.Base64;
import weaver.conn.RecordSet;
import weaver.general.TimeUtil;
import weaver.hrm.User;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

import java.net.URLEncoder;

/**
 * 流程状态推送
 */
public class PushFlowState extends BaseAction {

    @Override
    public String execute(RequestInfo requestInfo) {
        String requestId = requestInfo.getRequestid();
        String remark = requestInfo.getRequestManager().getRemark(); // 当前节点签字意见
        String operateType = requestInfo.getRequestManager().getSrc();
        User currentUser = requestInfo.getRequestManager().getUser();
        int formId = requestInfo.getRequestManager().getFormid();
        String tableName = "";
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("SELECT tablename FROM workflow_bill WHERE id = " + formId);
        if (recordSet.next()) {
            tableName = recordSet.getString("tablename");
        }

        this.writeLog("流程状态推送 Start requestid=" + requestId + "  operatetype --- " + operateType + "   fromTable --- " + tableName);
        try {
            this.writeLog("修改前签字意见： " + remark);
            String remarkStr = HlConnUtil.getRemarkStr(remark);
            this.writeLog("修改后签字意见： " + remarkStr);

            // 原串构造
            String dateString = TimeUtil.getCurrentDateString().replace("-", "");
            String srcStr = "appid=" + HlConnUtil.APP_ID + "&format=json&ts=" + dateString + "&userip=" + HlConnUtil.USER_IP;
            String base64Src = "POST&" + URLEncoder.encode(HlConnUtil.URI, "utf-8") + "&" + URLEncoder.encode(srcStr, "utf-8");
            this.writeLog("base64srcStr: " + base64Src);

            byte[] bytes = HlConnUtil.HmacSHA1EncryptByte(base64Src, HlConnUtil.APP_KEY + "&");

            String sig = new String(new Base64().encode(bytes));
            this.writeLog("sig: " + sig);

            JSONObject sendJsonObj = new JSONObject(true);
            sendJsonObj.put("appid", HlConnUtil.APP_ID);
            sendJsonObj.put("ts", TimeUtil.getCurrentDateString().replace("-", ""));
            sendJsonObj.put("sig", sig);
            sendJsonObj.put("format", "json");
            sendJsonObj.put("userip", HlConnUtil.USER_IP);

            JSONObject dataJsonObj = new JSONObject(true);
            dataJsonObj.put("ExtInstanceID", requestId);
            dataJsonObj.put("Result", "submit".equalsIgnoreCase(operateType) ? "1" : "2"); // 1:审批通过；2：驳回；3：超时；4：最终审批通过
            dataJsonObj.put("CreateDate", TimeUtil.getCurrentTimeString());
            dataJsonObj.put("LoginName", currentUser.getLoginid());
            dataJsonObj.put("Description", remarkStr);

            sendJsonObj.put("Approve", dataJsonObj);

            String sendJsonStr = sendJsonObj.toJSONString();
            this.writeLog("签字意见接口发送数据：" + sendJsonStr);

            // 调用签字意见接口
            String returnJson = HlConnUtil.sendPost(HlConnUtil.URL, sendJsonStr);
            this.writeLog("推送签字意见接口返回： " + returnJson);

            JSONObject returnJsonObj = JSONObject.parseObject(returnJson);
            String code = returnJsonObj.getString("code");
            if (!"200".equals(code)) {
                requestInfo.getRequestManager().setMessageid("110000");
                requestInfo.getRequestManager().setMessagecontent("流程状态推送 异常： " + returnJson);
                return "0";
            }

            this.writeLog("流程状态推送 End ===============");
        } catch (Exception e) {
            this.writeLog("流程状态推送 异常： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("流程状态推送 异常： " + e);
            return "0";
        }

        return "1";
    }


}
