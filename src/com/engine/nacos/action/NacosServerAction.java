package com.engine.nacos.action;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @Description:
 * @Author: wlj
 * @Date: 2021/3/24 14:22
 * @Modified By:
 */
public class NacosServerAction {

    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String nacos() {

        return "nacos server is OK ！";

    }
}