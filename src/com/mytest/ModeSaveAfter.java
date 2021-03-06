package com.mytest;

import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import weaver.formmode.customjavacode.AbstractModeExpandJavaCodeNew;
import weaver.hrm.User;
import weaver.soa.workflow.request.RequestInfo;

import java.util.HashMap;
import java.util.Map;

public class ModeSaveAfter extends AbstractModeExpandJavaCodeNew {

    private BaseBean baseBean = new BaseBean();

    /**
     * 执行模块扩展动作
     *
     * @param param param包含(但不限于)以下数据
     *              user 当前用户
     *              importtype 导入方式(仅在批量导入的接口动作会传输) 1 追加，2覆盖,3更新，获取方式(int)param.get("importtype")
     *              导入链接中拼接的特殊参数(仅在批量导入的接口动作会传输)，比如a=1，可通过param.get("a")获取参数值
     *              页面链接拼接的参数，比如b=2,可以通过param.get("b")来获取参数
     * @return
     */
    public Map<String, String> doModeExpand(Map<String, Object> param) {
        Map<String, String> result = new HashMap<>();
        try {
            baseBean.writeLog("保存扩展动作： " + JSONObject.toJSONString(param));
            User user = (User) param.get("user");
            int billid = -1;//数据id
            int modeid = -1;//模块id
            RequestInfo requestInfo = (RequestInfo) param.get("RequestInfo");
            if (requestInfo != null) {
                billid = Util.getIntValue(requestInfo.getRequestid());
                modeid = Util.getIntValue(requestInfo.getWorkflowid());
                if (billid > 0 && modeid > 0) {
                    //------请在下面编写业务逻辑代码------
                    String jsonStr = (String) param.get("JSONStr");

                    JSONObject sonObj = JSONObject.parseObject(jsonStr);
                    String ejmm = sonObj.getString("field6324");
                    baseBean.writeLog("二级密码： " + ejmm);
                    if ("1".equals(ejmm)) {
                        result.put("errmsg", "自定义出错信息");
                        result.put("flag", "false");
                    }
                }
            }
        } catch (Exception e) {
            result.put("errmsg", "自定义出错信息");
            result.put("flag", "false");
        }
        return result;
    }
}
