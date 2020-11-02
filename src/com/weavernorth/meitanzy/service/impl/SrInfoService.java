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
 * 收入信息上报接口
 */
public class SrInfoService implements PushService {

    private static final Log LOGGER = LogFactory.getLog(SrInfoService.class);

    @Override
    public String push(String ids, String token) {
        LOGGER.info("收入信息上报接口Start========推送数据id： " + ids);
        Map<String, String> headerMap = ConnUtil.getHeader(token);
        LOGGER.info("收入信息上报接口请求头： " + JSONObject.toJSONString(headerMap));

        RecordSet recordSet = new RecordSet();
        RecordSet recordSet_Detail = new RecordSet();
        recordSet.executeQuery("select * from uf_Mkzy_htgl where id in (" + ids + ")");
        while (recordSet.next()) {
            String mainId = recordSet.getString("id");
            String htzbh = recordSet.getString("htzbh");
            String ljskje = recordSet.getString("ljskje");

            JSONObject allObj = new JSONObject();
            JSONObject jsonObject = new JSONObject(true);
            JSONArray jsonArray = new JSONArray();
            jsonObject.put("contractUniqueId", htzbh); // 合同唯一标识
            jsonObject.put("incomeTotalAmount", ljskje); // 累计收款金额
            recordSet_Detail.executeQuery("select * from uf_Mkzy_htgl_dt2 where mainid = " + mainId);
            while (recordSet_Detail.next()) {
                JSONObject object = new JSONObject(true);
                object.put("incomeId", recordSet_Detail.getString("srfkid")); // 收入ID
                object.put("incomeAmount", recordSet_Detail.getString("yqrsrje")); // 已确认收入金额
                object.put("assistEvidence", ConnUtil.pushFileToFtp(recordSet_Detail.getString("fzzj1"), htzbh, "fzzj1")); // 辅助证据
                object.put("currentPeriodAmount", recordSet_Detail.getString("dqkpje")); // 当期开票金额
                jsonArray.add(object);
            }
            jsonObject.put("incomeInfoList", jsonArray);
            allObj.put("registerIncomeInfo", jsonObject);
            LOGGER.info("收入信息上报接口传输json： " + allObj.toJSONString());
            // 调用接口
//            String returnStr = MtHttpUtil.postJsonHeader(MeiTanConfigInfo.SHOU_RU_URL.getValue(), allObj.toJSONString(), headerMap);
//            LOGGER.info("收入信息上报接口返回数据： " + returnStr);

        }
        LOGGER.info("收入信息上报接口End========");
        return null;
    }


}
