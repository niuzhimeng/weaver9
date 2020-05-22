package com.weavernorth.downfile;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

public class FileTest {

    @GET
    @Path("/getDataTest")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JSONObject getDataTest(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "牛智萌");
        return jsonObject;


    }
}
