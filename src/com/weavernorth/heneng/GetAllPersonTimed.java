package com.weavernorth.heneng;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weavernorth.heneng.util.HnUtil;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.interfaces.schedule.BaseCronJob;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取所有员工
 */
public class GetAllPersonTimed extends BaseCronJob {
    private BaseBean baseBean = new BaseBean();
    private HnUtil hnUtil = new HnUtil();

    @Override
    public void execute() {
        baseBean.writeLog("获取全部员工Start=============");
        // 获取token
        // String token = hnUtil.getToken();
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0N2cwR2p6THZrRXdOUTNsVXIwMCIsInR5cGUiOjEsInRlbmFudElkIjoxNzU4ODg5LCJjcmVhdGVkQXQiOjE1ODcxMTI4ODIzNjAsImV4cCI6MTU4NzcxNzY4MiwiaWF0IjoxNTg3MTEyODgyfQ.pZyrcgz3bhRsRGcTt4nnrfdIx8uBw4E8IJXD8aPa-4E";
        if (!"".equals(token)) {
            getAllUser(token);
        } else {
            baseBean.writeLog("获取token失败");
        }

        baseBean.writeLog("获取全部员工End=============");
    }

    private void getAllUser(String token) {
        try {
            // 拼接请求头
            Map<String, String> header = new HashMap<>();
            header.put("AccessToken", token);
            header.put("ClientId", HnUtil.clientId);
            header.put("CreateTime", String.valueOf(System.currentTimeMillis()));

            // 拼接请求体
            JSONObject body = new JSONObject();
            body.put("tenantId", HnUtil.tenantId);

            // 调用接口
            String returnStr = hnUtil.okPostJson(HnUtil.GET_ALL_USER_URL, body.toJSONString(), header);
            baseBean.writeLog("获取所有员工： " + returnStr);
            JSONObject jsonObject = JSONObject.parseObject(returnStr);
            String returnStatus = jsonObject.getString("status");
            if ("200".equals(returnStatus)) {
                String insertSql = "insert into uf_hn_hrm(hn_id, jobTitle, fullname, sn, status) values(?,?,?,?,?)";

                RecordSet recordSet = new RecordSet();
                JSONArray dataArray = jsonObject.getJSONArray("data");
                int size = dataArray.size();
                recordSet.executeQuery("select 1 from uf_hn_hrm");
                if (!recordSet.next()) {
                    for (int i = 0; i < size; i++) {
                        JSONObject arrayJSONObject = dataArray.getJSONObject(i);

                        String id = arrayJSONObject.getString("id"); // 员工唯一id（微办公帐号唯一id）
                        String jobTitle = arrayJSONObject.getString("jobTitle"); // 员工职位
                        String fullname = arrayJSONObject.getString("fullname"); // 员工姓名
                        String sn = arrayJSONObject.getString("sn"); // 内部员工编号
                        String status = stateChange(arrayJSONObject.getString("status")); // 员工状态（1为正常，2为停用，5为离职）

                        recordSet.executeUpdate(insertSql, id, jobTitle, fullname, sn, status);

                    }
                }
            } else {
                baseBean.writeLog("员工获取失败: " + returnStatus);
            }
        } catch (Exception e) {
            baseBean.writeLog("获取全部人员异常： " + e);
        }
    }

    private String stateChange(String state) {
        String returnStr = "";
        if ("1".equals(state)) {
            returnStr = "正常";
        } else if ("2".equals(state)) {
            returnStr = "停用";
        } else if ("5".equals(state)) {
            returnStr = "离职";
        }

        return returnStr;
    }
}
