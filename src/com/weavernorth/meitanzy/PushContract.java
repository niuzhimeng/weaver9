package com.weavernorth.meitanzy;

import com.alibaba.fastjson.JSONObject;
import com.engine.common.util.ParamUtil;
import com.weavernorth.meitanzy.service.PushService;
import com.weavernorth.meitanzy.service.impl.SrInfoService;
import com.weavernorth.meitanzy.util.MeiTanConfigInfo;
import com.weavernorth.meitanzy.util.MtHttpUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

public class PushContract {

    private static final Log LOGGER = LogFactory.getLog(PushContract.class);

    @POST
    @Path("/pushContract")
    @Produces(MediaType.TEXT_PLAIN)
    public String pushContract(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        LOGGER.info("合同推送接口Start ==============================");
        Map<String, Object> objectMap = ParamUtil.request2Map(request);
        LOGGER.info("接收到参数： " + JSONObject.toJSONString(objectMap));
        String ids = (String) objectMap.get("ids");

        // 获取token
//        Map<String, String> bodyMap = new HashMap<>();
//        bodyMap.put("appuser", MeiTanConfigInfo.appuser.getValue()); // 应用授权代码
//        bodyMap.put("secretkey", MeiTanConfigInfo.secretkey.getValue());// 秘钥
//        String tokenStr = MtHttpUtil.postBody(MeiTanConfigInfo.LOGIN_URL.getValue(), bodyMap);
//        LOGGER.info("本次获取token接口返回： " + tokenStr);
//        if ("".equals(tokenStr)) {
//            JSONObject returnObj = new JSONObject();
//            returnObj.put("myState", false);
//            returnObj.put("message", tokenStr);
//            return returnObj.toJSONString();
//        }
//        JSONObject jsonObject = JSONObject.parseObject(tokenStr);
       // String token = jsonObject.getString("token");
        String token = "testtoken";

//        // 合同签订信息上报接口
//        PushUtil pushUtil = new PushUtil();
//        pushUtil.registerContractInfo(ids, token);

        // 收入信息上报接口
        PushService srInfoService = new SrInfoService();
        srInfoService.push(ids, token);
        LOGGER.info("合同推送接口End ==============================");
        return JSONObject.toJSONString(objectMap);


    }

}
