package com.weavernorth;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @Description:
 * @Author: wlj
 * @Date: 2020/11/13 11:49
 * @Modified By:
 */
@RefreshScope
@Api("流程微服务演示接口") // e9中该注解没有实际效果
public class WorkflowController {

    @GET
    @Path("/doc")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation("根据id获取文档服务") // e9中该注解没有实际效果
    public String doc(@PathVariable @ApiParam("文档主键") String id, @RequestParam String name) { // e9中@PathVariable @ApiParam @RequestParam注解都没有实际效果

        return "测试Nacos";
    }
}