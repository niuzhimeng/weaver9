package com.weavernorth.meirui.impl;

import com.alibaba.fastjson.JSONObject;
import com.weaverboot.frame.ioc.anno.classAnno.WeaIocReplaceComponent;
import com.weaverboot.frame.ioc.anno.methodAnno.WeaReplaceAfter;
import com.weaverboot.frame.ioc.handler.replace.weaReplaceParam.impl.WeaAfterReplaceParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.general.BaseBean;

import java.util.Map;

@WeaIocReplaceComponent()
public class ReplaceWeaTableContent11 {

    private static final Log MY_LOG = LogFactory.getLog(ReplaceWeaTableContent11.class);
    BaseBean baseBean = new BaseBean();

    /**
     * 注意方法名应该是  after  replace不生效
     * @param weaAfterReplaceParam
     * @return
     */
    @WeaReplaceAfter(value = "/api/workflow/reqform/getRequestLogList", order = 1)
    public String replace(WeaAfterReplaceParam weaAfterReplaceParam) {
        baseBean.writeLog("待办table内容重写/api/workflow/reqform/getRequestLogList： " + JSONObject.toJSONString(weaAfterReplaceParam));

        Map paramMap = weaAfterReplaceParam.getParamMap();
        MY_LOG.info("paramMap: " + JSONObject.toJSONString(paramMap));
        baseBean.writeLog("basparamMap: " + JSONObject.toJSONString(paramMap));

        String data = weaAfterReplaceParam.getData();
        try {

            MY_LOG.info("data: " + data);
        } catch (Exception e) {
            MY_LOG.error("待办table内容重写异常: " + e);
        }
        //将原有接口报文返回
        return data;
    }


}