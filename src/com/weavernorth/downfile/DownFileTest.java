package com.weavernorth.downfile;

import org.apache.commons.io.FileUtils;

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

public class DownFileTest {

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
                .header("Content-disposition",
                        "attachment;filename=" + fileName)
                .header("Cache-Control", "no-cache").build();

    }

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
            StreamingOutput streamingOutput = outputStream -> outputStream.write(FileUtils.readFileToByteArray(file));
            //Response.ok((StreamingOutput) output -> output.write(FileUtils.readFileToByteArray(file)));
            return Response.ok(streamingOutput, contentType)
                    .header("Content-disposition", "attachment;filename=" + fileName)
                    .header("Cache-Control", "no-cache").build();
        }

    }
}
