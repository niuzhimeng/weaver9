package com.api.workflowTest;


import com.alibaba.fastjson.JSONObject;
import com.engine.common.util.ParamUtil;
import com.engine.common.util.ServiceUtil;
import com.engine.workflow.util.CommonUtil;
import weaver.hrm.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;


public class Action {

    @GET
    @Path("/getDataTest")
    @Produces(MediaType.TEXT_PLAIN)
    public String getDataTest(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        User userByRequest = CommonUtil.getUserByRequest(request, response);
        MyServiceImpl myService = ServiceUtil.getService(MyServiceImpl.class, userByRequest);
        return JSONObject.toJSONString(myService.getTestData(ParamUtil.request2Map(request))) ;

    }
}
