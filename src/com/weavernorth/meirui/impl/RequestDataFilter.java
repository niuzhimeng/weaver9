package com.weavernorth.meirui.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weaverboot.frame.ioc.anno.classAnno.WeaIocReplaceComponent;
import com.weaverboot.frame.ioc.anno.methodAnno.WeaReplaceAfter;
import com.weaverboot.frame.ioc.handler.replace.weaReplaceParam.impl.WeaAfterReplaceParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.RecordSet;

import java.util.Map;

@WeaIocReplaceComponent()
public class RequestDataFilter {

    private static final Log log = LogFactory.getLog(RequestDataFilter.class);

    @WeaReplaceAfter(value = "/api/ec/dev/table/datas", order = 1)
    public String after(WeaAfterReplaceParam weaAfterReplaceParam) {
        Map paramMap = weaAfterReplaceParam.getParamMap();
        log.info("请求参数： " + JSONObject.toJSONString(paramMap));
        // 接口执行完的报文
        String data = weaAfterReplaceParam.getData();
        log.info("/api/ec/dev/table/datas接口返回json： " + data);

        JSONObject jsonObject = JSONObject.parseObject(data);
        JSONArray columsList = jsonObject.getJSONArray("columns");
        // 新加一列
        JSONObject colJsonObj = new JSONObject();
        colJsonObj.put("dataIndex", "je");
        colJsonObj.put("display", "true");
        colJsonObj.put("oldWidth", "10%");
        colJsonObj.put("title", "金额");

        columsList.add(colJsonObj);

        JSONArray dataList = jsonObject.getJSONArray("datas");
        int size = dataList.size();
        for (int i = 0; i < size; i++) {
            JSONObject obj = dataList.getJSONObject(i);
            obj.put("je", "1.11");
            obj.put("jespan", "1.11");
        }
        String s = jsonObject.toJSONString();
        log.info("修改后：" + s);
        return s;
    }


    public String getTableNameByRequestId(String requestId) {
        String tableName = "";
        String selSql = "SELECT tablename FROM workflow_bill WHERE id = (SELECT billformid FROM workflow_form WHERE requestid = ?)";
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery(selSql, requestId);
        if (recordSet.next()) {
            tableName = recordSet.getString("tablename");
        }
        return tableName;
    }

}
