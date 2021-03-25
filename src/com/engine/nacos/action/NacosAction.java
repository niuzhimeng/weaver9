package com.engine.nacos.action;

import com.cloudstore.api.util.Util_Ehcache;
import com.engine.common.util.ParamUtil;
import com.engine.nacos.instance.NacosBeat;
import com.engine.nacos.instance.NacosInstanceList;
import com.engine.nacos.instance.NacosInstanceRegister;
import com.engine.nacos.util.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import weaver.general.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * @Description:
 * @Author: wlj
 * @Date: 2021/3/23 16:54
 * @Modified By:
 */

public class NacosAction {

    @GET
    @Path("/data/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String nacos() {

        return NacosInstanceList.get();

    }


    @GET
    @Path("/data/instance")
    @Produces(MediaType.TEXT_PLAIN)
    public String instance() {

        NacosInstanceRegister.register();

        return "true";

    }


    @GET
    @Path("/data/beat")
    @Produces(MediaType.TEXT_PLAIN)
    public String beat() {

        NacosBeat.beat();

        return "true";

    }


    @GET
    @Path("/data/service1")
    @Produces(MediaType.TEXT_PLAIN)
    public String service1(@Context HttpServletRequest request, @Context HttpServletResponse response) {


        String str = "微服务weaver-service1获取到weaver-service2的参数为";


        Object object = Util_Ehcache.getIstance().get("weaver-service-rpc");

        if(object != null){
            str = str+ Util.null2String(object);
        }


        return str;

    }




    @GET
    @Path("/data/service2")
    @Produces(MediaType.TEXT_PLAIN)
    public String service2(@Context HttpServletRequest request, @Context HttpServletResponse response) {

        Map<String, Object> params = ParamUtil.request2Map(request);

        String str = "微服务weaver-service2修改数据为：";

        String data = Util.null2String(params.get("data"));

        if (StringUtils.isNotBlank(data)){
            str = str+ data;

            Util_Ehcache.getIstance().put("weaver-service-rpc",data);
        }

        return str;

    }


}