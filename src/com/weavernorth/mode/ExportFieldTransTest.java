package com.weavernorth.mode;

import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import weaver.formmode.interfaces.ExportFieldTransAction;
import weaver.hrm.User;

import java.util.Map;

public class ExportFieldTransTest implements ExportFieldTransAction {

    private final BaseBean baseBean = new BaseBean();

    @Override
    public String getTransValue(Map<String, Object> map, User user) {
// 获取当前登录人员ID
        Integer userId = user.getUID();
        // 获取模块ID
        int modeId = Util.getIntValue(map.get("modeid").toString());
        //表单id
        int formId = Util.getIntValue(map.get("formid").toString());
        //当前字段id
        String fieldid = String.valueOf(map.get("fieldid"));
        //查询列表id
        String customid = String.valueOf(map.get("customid"));
        //当前列名称(明细表会有d_前缀)
        String columnname = String.valueOf(map.get("columnname"));
        //当前列数据
        String value = String.valueOf(map.get("value"));
        //当前行数据
        Map data = (Map) map.get("data");

        baseBean.writeLog(userId + " " + modeId + " " + formId + " " + fieldid + " " + customid + " " + columnname + " " + value);
        baseBean.writeLog("当前行数据： " + JSONObject.toJSONString(map.get("data")));
        // 获取当前单元格
        value += "1";
        return value;

    }
}
