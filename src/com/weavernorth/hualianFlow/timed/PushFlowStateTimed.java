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
                    "returnInfo, rePushCount from uf_err_log where ifsuccess = 1 and rePushCount < 3 ");
            int counts = recordSet.getCounts();
            if (counts <= 0) {
                return;
            }
            log.info("流程状态再次推送Start===============可推送签字意见条数： " + counts);
            while (recordSet.next()) {
                String id = recordSet.getString("id");
                int currentCount = Util.getIntValue(recordSet.getString("rePushCount"), 0);
                currentCount += 1;
                // 审批意见
                String description = recordSet.getString("description");
                description = description.replace("&nbsp;", " ")
                        .replaceAll("\\r", "")
                        .replaceAll("(?i)<br/*>", " ");
                JSONObject dataJsonObj = new JSONObject(true);
                dataJsonObj.put("ExtInstanceID", recordSet.getString("myRequestid"));
                dataJsonObj.put("Result", recordSet.getString("sendType")); // 1:审批通过；2：驳回；3：超时；4：最终审批通过
                dataJsonObj.put("CreateDate", recordSet.getString("operDatetime")); // 审批时间
                dataJsonObj.put("LoginName", recordSet.getString("loginid"));
                dataJsonObj.put("Description", description);

                sendJsonObj.put("Approve", dataJsonObj);

                String sendJsonStr = sendJsonObj.toJSONString();
                // 调用签字意见接口
                String returnJson = HlConnUtil.sendPost(HlConnUtil.URL, sendJsonStr);
                log.info("定时任务推送签字意见接口返回： " + returnJson);
                if (returnJson.startsWith("error")) {
                    // 接口调用异常 推送次数+1
                    updateSet.executeUpdate("update uf_err_log set rePushCount = ?, returnInfo = ? where id = ?",
                            currentCount, returnJson, id);
                } else {
                    JSONObject returnJsonObj = JSONObject.parseObject(returnJson);
                    String code = returnJsonObj.getString("code");
                    if (!"200".equals(code)) {
                        // 接口返回状态为失败，推送次数+1
                        updateSet.executeUpdate("update uf_err_log set rePushCount = ?, returnInfo = ?  where id = ?",
                                currentCount, returnJson, id);
                    } else {
                        // 重推成功，更新标记 推送次数+1
                        updateSet.executeUpdate("update uf_err_log set rePushCount = ?, returnInfo = ?, ifsuccess = 0 where id = ?",
                                currentCount, returnJson, id);
                    }
                }
            }

        } catch (Exception e) {
            log.error("流程状态再次推送异常： " + e);
        }

        log.info("流程状态再次推送End");
    }
}
