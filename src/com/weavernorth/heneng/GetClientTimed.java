package com.weavernorth.heneng;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weavernorth.heneng.util.HnUtil;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.interfaces.schedule.BaseCronJob;

import java.util.HashMap;
import java.util.Map;

public class GetClientTimed extends BaseCronJob {

    private BaseBean baseBean = new BaseBean();

    private Integer userId;

    @Override
    public void execute() {
        HnUtil hnUtil = new HnUtil();
        // 获取token
        // String token = hnUtil.getToken();
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0N2cwR2p6THZrRXdOUTNsVXIwMCIsInR5cGUiOjEsInRlbmFudElkIjoxNzU4ODg5LCJjcmVhdGVkQXQiOjE1ODcxMTI4ODIzNjAsImV4cCI6MTU4NzcxNzY4MiwiaWF0IjoxNTg3MTEyODgyfQ.pZyrcgz3bhRsRGcTt4nnrfdIx8uBw4E8IJXD8aPa-4E";
        if (!"".equals(token)) {
            // 拼接请求头
            Map<String, String> header = new HashMap<>();
            header.put("AccessToken", token);
            header.put("ClientId", HnUtil.clientId);
            header.put("CreateTime", String.valueOf(System.currentTimeMillis()));

            // 拼接请求体
            JSONObject body = new JSONObject();
            body.put("tenantId", HnUtil.tenantId);
            body.put("userId", userId);
            //body.put("pageNumber", 1);

            // 调用接口
            String returnStr = hnUtil.okPostJson(HnUtil.GET_CLIENT_BY_USER_URL, body.toJSONString(), header);
            baseBean.writeLog("获取某人负责的客户接口返回： " + returnStr);
            JSONObject jsonObject = JSONObject.parseObject(returnStr);
            String returnStatus = jsonObject.getString("status");
            if ("200".equals(returnStatus)) {
                String insertSql = "insert into uf_hn_client(khid, khmc, gxsj, ryid) values(?,?,?,?)";
                RecordSet recordSet = new RecordSet();
                JSONArray dataArray = jsonObject.getJSONObject("data").getJSONArray("data");
                int size = dataArray.size();
                recordSet.executeQuery("select 1 from uf_hn_client where ryid = '" + userId + "'");
                if (!recordSet.next()) {
                    for (int i = 0; i < size; i++) {
                        JSONObject arrayJSONObject = dataArray.getJSONObject(i);

                        String id = arrayJSONObject.getString("id"); // 客户id
                        String name = arrayJSONObject.getString("name"); // 客户名称
                        String updatedAt = arrayJSONObject.getString("updatedAt"); // 更新时间

                        recordSet.executeUpdate(insertSql, id, name, updatedAt, userId);

                    }
                }
            } else {
                baseBean.writeLog("员工获取失败: " + returnStatus);
            }
        } else {
            baseBean.writeLog("获取token失败");
        }


    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
