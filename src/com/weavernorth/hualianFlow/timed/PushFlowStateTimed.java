package com.weavernorth.hualianFlow.timed;

import com.alibaba.fastjson.JSONObject;
import com.weavernorth.hualianFlow.util.HlConnUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.RecordSet;
import weaver.general.TimeUtil;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;

/**
 * 定时将 推送失败流程状态 再次推送
 */
public class PushFlowStateTimed extends BaseCronJob {

    private static Log log = LogFactory.getLog(PushFlowStateTimed.class);

    @Override
    public void execute() {
        log.info("定时推送流程状态Start");
        RecordSet recordSet = new RecordSet();
        RecordSet updateSet = new RecordSet();
        String currentDate = TimeUtil.getCurrentDateString().replace("-", "");
        try {
            JSONObject sendJsonObj = new JSONObject(true);
            sendJsonObj.put("appid", HlConnUtil.APP_ID);
            sendJsonObj.put("ts", currentDate);
            sendJsonObj.put("sig", HlConnUtil.getSig());
            sendJsonObj.put("format", "json");
            sendJsonObj.put("userip", HlConnUtil.USER_IP);

            recordSet.executeQuery("select id, logid, myRequestid, sendType, operDatetime, loginid, description, " +
                    "returnInfo, rePushCount from uf_err_log where rePushCount < 3");
            while (recordSet.next()) {
                String id = recordSet.getString("id");
                int currentCount = Util.getIntValue(recordSet.getString("rePushCount"), 0);
                currentCount += 1;

                JSONObject dataJsonObj = new JSONObject(true);
                dataJsonObj.put("ExtInstanceID", recordSet.getString("myRequestid"));
                dataJsonObj.put("Result", recordSet.getString("sendType")); // 1:审批通过；2：驳回；3：超时；4：最终审批通过
                dataJsonObj.put("CreateDate", recordSet.getString("operDatetime")); // 审批时间
                dataJsonObj.put("LoginName", recordSet.getString("loginid"));
                dataJsonObj.put("Description", recordSet.getString("description"));

                sendJsonObj.put("Approve", dataJsonObj);

                String sendJsonStr = sendJsonObj.toJSONString();
                // 调用签字意见接口
                String returnJson = HlConnUtil.sendPost(HlConnUtil.URL, sendJsonStr);
                log.info("定时任务推送签字意见接口返回： " + returnJson);
                if (returnJson.startsWith("error")) {
                    // 接口调用异常 推送次数+1
                    updateSet.executeUpdate("update uf_err_log set rePushCount = ? where id = ?", currentCount, id);
                } else {
                    JSONObject returnJsonObj = JSONObject.parseObject(returnJson);
                    String code = returnJsonObj.getString("code");
                    if (!"200".equals(code)) {
                        // 接口返回状态为失败，推送次数+1
                        updateSet.executeUpdate("update uf_err_log set rePushCount = ? where id = ?", currentCount, id);
                    } else {
                        // 重推成功，更新标记 推送次数+1
                        updateSet.executeUpdate("update uf_err_log set rePushCount = ?, ifsuccess = 0 where id = ?", currentCount, id);
                    }
                }
            }

        } catch (Exception e) {
            log.error("定时推送流程状态异常： " + e);
        }


        log.info("定时推送流程状态End");
    }
}
