package com.weavernorth.apiTest.action;


import weaver.conn.RecordSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GetDataTest {

    @GET
    @Path("/getDataTest")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, Object> getDataTest(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        RecordSet recordSet = new RecordSet();
        Map<String, Object> objMap = new HashMap<>();
        recordSet.executeQuery("select * from hrmresource");
        List<Map<String, String>> returnList = new ArrayList<>();
        while (recordSet.next()) {
            Map<String, String> returnMap = new HashMap<>();
            returnMap.put("lastname", recordSet.getString("lastname"));
            returnMap.put("id", recordSet.getString("id"));
            returnMap.put("sex", recordSet.getString("sex"));
            returnList.add(returnMap);
        }
        objMap.put("data", returnList);

        return objMap;


    }

    @GET
    @Path("/ajaxTest")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> ajaxTest(@Context HttpServletRequest request, @Context HttpServletResponse response) throws InterruptedException {
        String time = request.getParameter("myTime");
        int longTime = Integer.parseInt(time) * 1000;
        Thread.sleep(longTime);

        RecordSet recordSet = new RecordSet();
        Map<String, Object> objMap = new HashMap<>();
        recordSet.executeQuery("select * from hrmresource");
        List<Map<String, String>> returnList = new ArrayList<>();
        while (recordSet.next()) {
            Map<String, String> returnMap = new HashMap<>();
            returnMap.put("lastname", recordSet.getString("lastname"));
            returnMap.put("id", recordSet.getString("id"));
            returnMap.put("sex", recordSet.getString("sex"));
            returnList.add(returnMap);
        }
        objMap.put("data", returnList);

        return objMap;


    }
}
