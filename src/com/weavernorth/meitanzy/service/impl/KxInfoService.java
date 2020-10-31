package com.weavernorth.meitanzy.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weavernorth.meitanzy.service.PushService;
import com.weavernorth.meitanzy.util.ConnUtil;
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
            String xxly = recordSet.getString("xxly");

            JSONObject allObj = new JSONObject();
            JSONObject jsonObject = new JSONObject(true);
            JSONArray jsonArray = new JSONArray();
            jsonObject.put("contractUniqueId", htzbh); // 合同唯一标识
            jsonObject.put("sourceInfo", xxly); // 信息来源
            recordSet_Detail.executeQuery("select * from uf_Mkzy_htgl_dt1 where mainid = " + mainId);
            while (recordSet_Detail.next()) {
                if ("xx".equals(xxly)) {
                    JSONObject object = new JSONObject(true);
                    object.put("planId", recordSet_Detail.getString("id")); // 计划ID
                    object.put("performItem", recordSet_Detail.getString("lxsx")); // 履行事项
                    object.put("payDate", recordSet_Detail.getString("jhsfkrq")); // 计划收/付款日期
                    object.put("payAmount", recordSet_Detail.getString("jhsfkje")); // 计划收/付款金额
                    jsonArray.add(object);
                    jsonObject.put("paymentPlanList", jsonArray);
                } else {
                    JSONObject object = new JSONObject(true);
                    object.put("planId", recordSet_Detail.getString("id")); // 计划ID
                    object.put("feedBackId", recordSet_Detail.getString("fkid")); // 反馈ID
                    object.put("isNormal", recordSet_Detail.getString("sfzc")); // 是否正常
                    object.put("abnormalReason", recordSet_Detail.getString("ycyy")); // 异常原因
                    object.put("realPayDate", recordSet_Detail.getString("sjsfkrq")); // 实际收付款日期

                    object.put("realPayAmount", recordSet_Detail.getString("sjsfkje")); // 实际收付款金额
                    jsonArray.add(object);
                    jsonObject.put("paymentFeedbackList", jsonArray);
                }

            }
            allObj.put("paymentPlanAndFeedbackInfo", jsonObject);
            LOGGER.info("款项信息上报接口传输json： " + allObj.toJSONString());
            // 调用接口
//            String returnStr = MtHttpUtil.postJsonHeader(MeiTanConfigInfo.SHOU_RU_URL.getValue(), allObj.toJSONString(), headerMap);
//            LOGGER.info("款项信息上报接口返回数据： " + returnStr);
        }
        LOGGER.info("款项信息上报接口End========");
        return null;
    }
}
