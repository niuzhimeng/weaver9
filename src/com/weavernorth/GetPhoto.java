package com.weavernorth;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetPhoto {

    @GET
    @Path("/getRandomPhoto")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject getDataTest(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        File photoFiles = new File("E:\\WEAVER\\ecology\\idea背景图");
        File[] files = photoFiles.listFiles();
        JSONObject jsonObject = new JSONObject();
        // 创建装路径的数组
        List<String> pathList = new ArrayList<>();
        boolean flag;
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    pathList.add(file.toString());
                }
            }
            flag = true;
        } else {
            flag = false;
        }
        Collections.shuffle(pathList);
        List<String> strings = pathList.subList(0, 8);
        jsonObject.put("status", flag);
        jsonObject.put("paths", strings);

        return jsonObject;
    }
}
