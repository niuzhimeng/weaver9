package com.weavernorth.meirui.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weaverboot.frame.ioc.anno.classAnno.WeaIocReplaceComponent;
import com.weaverboot.frame.ioc.anno.methodAnno.WeaReplaceAfter;
import com.weaverboot.frame.ioc.handler.replace.weaReplaceParam.impl.WeaAfterReplaceParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@WeaIocReplaceComponent()
public class RequestLogFilter {

    private static final Log log = LogFactory.getLog(RequestLogFilter.class);

    @WeaReplaceAfter(value = "/api/workflow/reqform/getRequestLogList", order = 1)
    public String after(WeaAfterReplaceParam weaAfterReplaceParam) {
// api/ec/dev/table/datas
        String data = weaAfterReplaceParam.getData();//这个就是接口执行完的报文

        log.info("RequestLogFilter接口返回json： " + data);
        JSONObject jsonObject = JSONObject.parseObject(data);
        JSONArray logList = jsonObject.getJSONArray("loglist");
        JSONArray newArray = new JSONArray();
        int size = logList.size();

        for (int i = 0; i < size; i++) {
            JSONObject obj = logList.getJSONObject(i);
            String displaydepname = obj.getString("displaydepname");
            String displayid = obj.getString("displayid"); // 人员id

            String receiveUser = obj.getString("receiveUser");
            if ("张三".equals(receiveUser)) {
                continue;
            }
            newArray.add(obj);

        }
        jsonObject.put("loglist", newArray);
        return jsonObject.toJSONString();
    }
}
