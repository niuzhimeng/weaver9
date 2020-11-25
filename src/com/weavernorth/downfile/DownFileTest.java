package com.weavernorth.downfile;

import cn.afterturn.easypoi.entity.ImageEntity;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.alibaba.fastjson.JSONObject;
import com.engine.common.util.ParamUtil;
import com.weavernorth.downfile.vo.TestPerson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;
import weaver.email.mime.ByteOutputStream;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

            StreamingOutput a = new StreamingOutput() {
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

    // 好使
    @GET
    @Path("/downExcel")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] getDataTest2(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        try {
            TemplateExportParams params = new TemplateExportParams("C:\\Users\\86157\\Desktop\\整合员工信息模板(1).xls", true);
            ImageEntity image = new ImageEntity();
            image.setRowspan(5);//向下合并三行
            //image.setColspan(2);//向右合并两列
            // byte[] imageBytes = getImage("C:\\Users\\86157\\Desktop\\mk7.jpg");
            //image.setData(imageBytes);
            image.setUrl("C:\\Users\\86157\\Desktop\\mk7.jpg");
            String kbStr = "                ";
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 14; i++) {
                stringBuilder.append("入学时间").append(i).append(kbStr)
                        .append("毕业时间").append(i).append(kbStr)
                        .append("哈尔滨师范大学").append(i).append(kbStr)
                        .append(" \r\n");
            }

            Map<String, Object> map = new HashMap<>();
            map.put("name", "牛智萌12");
            map.put("workCode", "1-00002");
            map.put("sex", "男");
            map.put("email", "xxx@qq.com");
            map.put("year", 2020);
            map.put("month", 11);
            map.put("day", 25);
            map.put("info", stringBuilder.toString());
            map.put("photo", image);

            // 集合导出
            List<Map<String, String>> listMap = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                Map<String, String> lm = new HashMap<>();
                lm.put("name", "牛智萌" + i);
                lm.put("school", "哈尔滨师范大学" + i);
                listMap.add(lm);
            }
            map.put("maplist", listMap);

            List<Map<String, String>> listMap1 = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                Map<String, String> lm = new HashMap<>();
                lm.put("number", "学号" + i);
                lm.put("sex", "男" + i);
                listMap1.add(lm);
            }
            map.put("listMap1", listMap1);

            String[] sheetNames = {"员工信息"};
            params.setSheetName(sheetNames);
            Workbook workbook = ExcelExportUtil.exportExcel(params, map);

            ByteOutputStream byteOutputStream = new ByteOutputStream();
            workbook.write(byteOutputStream);

            String fileName = "test.xls";
            response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));//为文件命名，为防止文件名中文乱码，需要对文件名重编码
            response.addHeader("content-type", "multipart/form-data");
            return byteOutputStream.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
