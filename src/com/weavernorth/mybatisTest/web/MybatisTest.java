package com.weavernorth.mybatisTest.web;

import com.weavernorth.mybatisTest.entity.HrmVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MybatisTest {
    @GET
    @Path("/getAllUser")
    @Produces(MediaType.APPLICATION_JSON)
    //@Consumes(MediaType.APPLICATION_JSON)
    public List<HrmVO> getDataTest(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        String id = request.getParameter("id");
        Map<String, String> map = new HashMap<>();
        map.put("id", id);

//        MyBatisConnection myBatisConnection = new MyBatisConnection();
//        // 通过会话工厂创建会话
//        SqlSession session = myBatisConnection.getSession();
//        List<HrmVO> listVideo = session.selectList("ExampleHrmResource.findUserAll", map);
//        //关闭会话（必须！！）
//        myBatisConnection.closeSession();
        return null;


    }
}
