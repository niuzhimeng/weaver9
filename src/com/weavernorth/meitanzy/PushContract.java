package com.weavernorth.meitanzy;

import com.alibaba.fastjson.JSONObject;
import com.engine.common.util.ParamUtil;
import com.weavernorth.meitanzy.service.PushService;
import com.weavernorth.meitanzy.service.impl.HtqdService;
import com.weavernorth.meitanzy.service.impl.KxInfoService;
import com.weavernorth.meitanzy.service.impl.SrInfoService;
import com.weavernorth.meitanzy.util.ConnUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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
        String token = ConnUtil.getToken();
        if ("".equals(token)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("myState", false);
            jsonObject.put("message", "获取token异常，请查看日志分析错误原因。");
            return jsonObject.toJSONString();
        }

        LOGGER.info("合同签订信息上报接口-----------开始");
        long time1 = System.currentTimeMillis();
        PushService htqdService = new HtqdService();
        htqdService.push(ids, token);
        long time2 = System.currentTimeMillis();
        LOGGER.info("合同签订信息上报接口-----------结束，耗时：" + (time2 - time1) + " 毫秒");

        LOGGER.info("款项信息上报接口-----------开始");
        PushService kxInfoService = new KxInfoService();
        kxInfoService.push(ids, token);
        long time3 = System.currentTimeMillis();
        LOGGER.info("款项信息上报接口-----------结束，耗时：" + (time3 - time2) + " 毫秒");

        LOGGER.info("收入信息上报接口-----------开始");
        PushService srInfoService = new SrInfoService();
        srInfoService.push(ids, token);
        long time4 = System.currentTimeMillis();
        LOGGER.info("收入信息上报接口-----------结束，耗时：" + (time4 - time3) + " 毫秒");

        LOGGER.info("合同推送接口End ==============================结束，总耗时：" + (time4 - time1) + " 毫秒");
        return JSONObject.toJSONString(objectMap);


    }

}
