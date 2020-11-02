package com.weavernorth.meitanzy.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weavernorth.meitanzy.service.PushService;
import com.weavernorth.meitanzy.util.ConnUtil;
import com.weavernorth.meitanzy.util.MeiTanConfigInfo;
import com.weavernorth.meitanzy.util.MtHttpUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.RecordSet;

import java.util.Map;

/**
 * 款项信息上报接口
 */
public class KxInfoService implements PushService {

    private static final Log LOGGER = LogFactory.getLog(KxInfoService.class);

    @Override
    public String push(String ids, String token) {
        LOGGER.info("款项信息上报接口Start========推送数据id： " + ids);
        Map<String, String> headerMap = ConnUtil.getHeader(token);
        LOGGER.info("款项信息上报接口请求头： " + JSONObject.toJSONString(headerMap));

        RecordSet recordSet = new RecordSet();
        RecordSet recordSet_Detail = new RecordSet();
        recordSet.executeQuery("select * from uf_Mkzy_htgl where id in (" + ids + ")");
        while (recordSet.next()) {
            String mainId = recordSet.getString("id");
            String htzbh = recordSet.getString("htzbh");

            JSONArray planArray = new JSONArray(); // 计划
            JSONArray feedbackArray = new JSONArray(); // 反馈
            recordSet_Detail.executeQuery("select * from uf_Mkzy_htgl_dt1 where mainid = " + mainId);
            while (recordSet_Detail.next()) {
                String id = recordSet_Detail.getString("id");
                // 计划数据拼接
                JSONObject jhObj = new JSONObject(true);
                jhObj.put("planId", id); // 计划ID
                jhObj.put("sortNum", id); // 计划序号
                jhObj.put("performItem", recordSet_Detail.getString("lxsx")); // 履行事项
                jhObj.put("payDate", recordSet_Detail.getString("jhsfkrq")); // 计划收/付款日期
                jhObj.put("reminderDay", recordSet_Detail.getString("tqtxts")); // 提前提醒天数

                jhObj.put("payAmount", recordSet_Detail.getString("jhsfkje")); // 计划收/付款金额
                planArray.add(jhObj);

                // 反馈数据拼接
                JSONObject fkObj = new JSONObject(true);
                fkObj.put("planId", id); // 计划ID
                fkObj.put("feedBackId", id); // 反馈ID
                fkObj.put("sortNum", id); // 反馈序号
                fkObj.put("isNormal", recordSet_Detail.getString("sfzc")); // 是否正常
                fkObj.put("abnormalReason", recordSet_Detail.getString("ycyy")); // 异常原因

                fkObj.put("realPayDate", recordSet_Detail.getString("sjsfkrq")); // 实际收付款日期
                fkObj.put("realPayAmount", recordSet_Detail.getString("sjsfkje")); // 实际收付款金额
                feedbackArray.add(fkObj);
            }
            JSONObject planObj = getJsonObj(htzbh, "PLAN", planArray);
            String planJson = planObj.toJSONString();
            LOGGER.info("款项信息上报接口 - 计划 传输json： " + planJson);
            String planReturnStr = MtHttpUtil.postJsonHeader(MeiTanConfigInfo.KUAN_XIANG_URL.getValue(), planJson, headerMap);
            LOGGER.info("款项信息上报接口 - 计划 返回数据： " + planReturnStr);

            JSONObject feedbackObj = getJsonObj(htzbh, "FEEDBACK", feedbackArray);
            String feedbackJson = feedbackObj.toJSONString();
            LOGGER.info("款项信息上报接口 - 反馈 传输json： " + feedbackJson);
            String feedbackReturnStr = MtHttpUtil.postJsonHeader(MeiTanConfigInfo.KUAN_XIANG_URL.getValue(), feedbackJson, headerMap);
            LOGGER.info("款项信息上报接口 - 反馈 返回数据： " + feedbackReturnStr);
        }
        LOGGER.info("款项信息上报接口End========");
        return "";
    }

    /**
     * 获取发送json对象
     *
     * @param htzbh 合同自编号
     * @param xxly  信息来源 PLAN, FEEDBACK
     */
    private JSONObject getJsonObj(String htzbh, String xxly, JSONArray jsonArray) {
        JSONObject allObj = new JSONObject();
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("contractUniqueId", htzbh); // 合同唯一标识
        jsonObject.put("sourceInfo", xxly); // 信息来源
        if ("PLAN".equals(xxly)) {
            jsonObject.put("paymentPlanList", jsonArray);
            jsonObject.put("paymentFeedbackList", new JSONArray());
        } else {
            jsonObject.put("paymentPlanList", new JSONArray());
            jsonObject.put("paymentFeedbackList", jsonArray);
        }
        allObj.put("paymentPlanAndFeedbackInfo", jsonObject);

        return allObj;
    }
}
