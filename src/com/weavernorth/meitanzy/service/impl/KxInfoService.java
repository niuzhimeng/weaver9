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

import java.util.ArrayList;
import java.util.List;
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
        try {
            RecordSet updateSet = new RecordSet();
            RecordSet recordSet = new RecordSet();
            RecordSet recordSet_Detail = new RecordSet();
            recordSet.executeQuery("select * from uf_Mkzy_htgl where id in (" + ids + ")");
            while (recordSet.next()) {
                JSONArray planArray = new JSONArray(); // 计划
                JSONArray feedbackArray = new JSONArray(); // 反馈
                String mainId = recordSet.getString("id");
                String htzbh = recordSet.getString("htzbh"); // 传输类型 计划、反馈、计划+反馈

                recordSet_Detail.executeQuery("select * from uf_Mkzy_htgl_dt1 where mainid = " + mainId);
                while (recordSet_Detail.next()) {
                    String jhfk = recordSet_Detail.getString("jhfk"); // 传输类型
                    String id = recordSet_Detail.getString("id");
                    // 计划 部门所需字段
                    String lxsx = recordSet_Detail.getString("lxsx");  // 履行事项
                    String jhsfkrq = recordSet_Detail.getString("jhsfkrq"); // 计划收/付款日期
                    String tqtxts = recordSet_Detail.getString("tqtxts"); // 提前提醒天数
                    String jhsfkje = recordSet_Detail.getString("jhsfkje"); // 计划收/付款金额

                    // 反馈 部门所需字段
                    String sfzc = recordSet_Detail.getString("sfzc"); // 是否正常
                    String ycyy = recordSet_Detail.getString("ycyy"); // 异常原因
                    String sjsfkrq = recordSet_Detail.getString("sjsfkrq"); // 实际收付款日期
                    String sjsfkje = recordSet_Detail.getString("sjsfkje"); // 实际收付款金额
                    if ("0".equals(jhfk)) {
                        // 计划数据拼接
                        planArray.add(getPlanJsonObj(id, lxsx, jhsfkrq, tqtxts, jhsfkje));
                    } else if ("1".equals(jhfk)) {
                        // 反馈数据拼接
                        feedbackArray.add(getFeedbackJsonObj(id, sfzc, ycyy, sjsfkrq, sjsfkje));
                    } else if ("2".equals(jhfk)) {
                        // 全部推送
                        planArray.add(getPlanJsonObj(id, lxsx, jhsfkrq, tqtxts, jhsfkje));
                        feedbackArray.add(getFeedbackJsonObj(id, sfzc, ycyy, sjsfkrq, sjsfkje));
                    }
                }

                List<String> stateList = new ArrayList<>();
                String result = "";
                if (planArray.size() > 0) {
                    String planJson = getJsonObj(htzbh, "PLAN", planArray);
                    LOGGER.info("款项信息上报接口 - 计划 传输json： " + planJson);
                    String planReturnStr = MtHttpUtil.postJsonHeader(MeiTanConfigInfo.KUAN_XIANG_URL.getValue(), planJson, headerMap);
                    LOGGER.info("款项信息上报接口 - 计划 返回数据： " + planReturnStr);
                    String[] planMessage = ConnUtil.parseReturnJson(planReturnStr);
                    stateList.add(planMessage[0]);
                    result += "计划：" + planMessage[1] + " | ";
                }

                if (feedbackArray.size() > 0) {
                    String feedbackJson = getJsonObj(htzbh, "FEEDBACK", feedbackArray);
                    LOGGER.info("款项信息上报接口 - 反馈 传输json： " + feedbackJson);
                    String feedbackReturnStr = MtHttpUtil.postJsonHeader(MeiTanConfigInfo.KUAN_XIANG_URL.getValue(), feedbackJson, headerMap);
                    LOGGER.info("款项信息上报接口 - 反馈 返回数据： " + feedbackReturnStr);
                    String[] feedbackMessage = ConnUtil.parseReturnJson(feedbackReturnStr);
                    stateList.add(feedbackMessage[0]);
                    result += "反馈: " + feedbackMessage[1];
                }
                String endingState = "0";
                if (stateList.contains("1")) {
                    endingState = "1";
                }

                // 更新推送返回信息
                String updateSql = "update uf_Mkzy_htgl set kxxxfhzt = ?, kxxxsbztfk = ? where id = ?";
                updateSet.executeUpdate(updateSql, endingState, result, mainId);
            }
            LOGGER.info("款项信息上报接口End========");
        } catch (Exception e) {
            LOGGER.error("款项信息上报接口异常： " + e);
        }
        return "";
    }

    /**
     * 计划 - 部分json对象拼接
     *
     * @param id      明细数据id
     * @param lxsx    履行事项
     * @param jhsfkrq 计划收/付款日期
     * @param tqtxts  提前提醒天数
     * @param jhsfkje 计划收/付款金额
     */
    private JSONObject getPlanJsonObj(String id, String lxsx, String jhsfkrq, String tqtxts, String jhsfkje) {
        // 计划数据拼接
        JSONObject jhObj = new JSONObject(true);
        jhObj.put("planId", id); // 计划ID
        jhObj.put("sortNum", id); // 计划序号
        jhObj.put("performItem", lxsx);
        jhObj.put("payDate", jhsfkrq);
        jhObj.put("reminderDay", tqtxts);

        jhObj.put("payAmount", jhsfkje);
        return jhObj;
    }

    /**
     * 反馈 - 部分json对象拼接
     *
     * @param id      明细数据id
     * @param sfzc    是否正常
     * @param ycyy    异常原因
     * @param sjsfkrq 实际收付款日期
     * @param sjsfkje 实际收付款金额
     */
    private JSONObject getFeedbackJsonObj(String id, String sfzc, String ycyy, String sjsfkrq, String sjsfkje) {
        JSONObject fkObj = new JSONObject(true);
        fkObj.put("planId", id); // 计划ID
        fkObj.put("feedBackId", id); // 反馈ID
        fkObj.put("sortNum", id); // 反馈序号
        fkObj.put("isNormal", sfzc);
        fkObj.put("abnormalReason", ycyy);

        fkObj.put("realPayDate", sjsfkrq);
        fkObj.put("realPayAmount", sjsfkje);
        return fkObj;
    }

    /**
     * 获取发送json对象
     *
     * @param htzbh 合同自编号
     * @param xxly  信息来源 PLAN, FEEDBACK
     */
    private String getJsonObj(String htzbh, String xxly, JSONArray jsonArray) {
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
        JSONObject allObj = new JSONObject();
        allObj.put("paymentPlanAndFeedbackInfo", jsonObject.toJSONString());
        return allObj.toJSONString();

    }
}
