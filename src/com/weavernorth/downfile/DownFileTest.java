package com.weavernorth.downfile;

import com.alibaba.fastjson.JSONObject;
import com.engine.common.util.ParamUtil;
import com.weavernorth.downfile.vo.TestPerson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.hrm.HrmUserVarify;
import weaver.hrm.User;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class DownFileTest {
    private static final Log MY_LOG = LogFactory.getLog(DownFileTest.class);

    //好使
    @GET
    @Path("/getDataTest")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response getDataTest(@Context HttpServletRequest request, @Context HttpServletResponse response) throws UnsupportedEncodingException {
        File file = new File("E:\\WEAVER\\测试的文件你23ABC.pdf");
        String fileName = file.getName();
        fileName = URLEncoder.encode(fileName, "utf-8");
        String contentType = new MimetypesFileTypeMap().getContentType(file);
        return Response
                .ok(file, contentType)
                .header("Content-disposition", "attachment;filename=" + fileName)
                .header("Cache-Control", "no-cache").build();

    }

    // 好使
    @GET
    @Path("/getDataTest1")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response getDataTest1(@Context HttpServletRequest request, @Context HttpServletResponse response) throws UnsupportedEncodingException {
        File file = new File("E:\\WEAVER\\测试的文件你23ABC.pdf");
        String fileName = file.getName();
        fileName = URLEncoder.encode(fileName, "utf-8");
        String contentType = new MimetypesFileTypeMap().getContentType(file);
        if (!file.exists()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {

            StreamingOutput a =  new StreamingOutput() {
                @Override
                public void write(OutputStream output) throws IOException, WebApplicationException {
                    output.write(FileUtils.readFileToByteArray(file));
                }
            };

            StreamingOutput streamingOutput = (OutputStream outputStream) -> outputStream.write(FileUtils.readFileToByteArray(file));
            //Response.ok((StreamingOutput) output -> output.write(FileUtils.readFileToByteArray(file)));
            return Response.ok(streamingOutput, contentType)
                    .header("Content-disposition", "attachment;filename=" + fileName)
                    .header("Cache-Control", "no-cache").build();
        }

    }

    //
    @GET
    @Path("/getDataTest2")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response getDataTest2(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        String fileName = "测试的文件你23ABC.pdf";

        File file = new File("E:\\WEAVER\\" + fileName);
        fileName = URLEncoder.encode(fileName, "utf-8");
        String mimeType = request.getSession().getServletContext().getMimeType(fileName);
        String contentType = new MimetypesFileTypeMap().getContentType(file);
        MY_LOG.info("mimeType: " + mimeType);
        MY_LOG.info("contentType: " + contentType);


        return Response.ok()
                .header("Content-disposition", "attachment;filename=" + fileName)
                .header("Cache-Control", "no-cache").build();
    }


    @POST
    @Path("/updatePerson")
    @Produces(MediaType.APPLICATION_JSON)
    public TestPerson updatePerson(@Context HttpServletRequest request, @Context HttpServletResponse response,
                                   TestPerson testPerson) {
        try {
            User user = HrmUserVarify.getUser(request, response);
            MY_LOG.info("当前登录人对象： " + JSONObject.toJSON(user));
            Map<String, Object> objectMap = ParamUtil.request2Map(request);

            MY_LOG.info("ParamUtil接收到参数： " + JSONObject.toJSONString(objectMap));
            MY_LOG.info("updatePerson接收到参数： " + JSONObject.toJSONString(testPerson));
        } catch (Exception e) {
            MY_LOG.error("updatePerson异常： " + e);
        }

        return testPerson;

    }

}
