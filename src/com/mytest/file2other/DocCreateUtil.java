package com.mytest.file2other;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.axis.encoding.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.docs.webservices.DocAttachment;
import weaver.docs.webservices.DocInfo;
import weaver.docs.webservices.DocServiceImpl;

import java.util.List;
import java.util.StringJoiner;

public class DocCreateUtil {

    private static final Log LOGGER = LogFactory.getLog(DocCreateUtil.class);

    /**
     * 获取上传文档所属session
     *
     * @param userName 登录名
     * @param password 密码
     * @return session
     */
    public static String getDocSession(String userName, String password) {
        String sessionStr = "";
        try {
            sessionStr = new DocServiceImpl().login(userName, password, 0, "127.0.0.1");
        } catch (Exception e) {
            LOGGER.error("获取文档session异常： " + e);
        }
        return sessionStr;
    }

    /**
     * 创建新文档
     *
     * @param session         登陆session
     * @param strFileRealPath 文档实际存放路径 -绝对路径
     * @param intFileId       子目录id
     * @return docId
     */
    public static String createNewDoc(String session, String strFileRealPath, int intFileId) {
        StringJoiner stringJoiner = new StringJoiner(",");
        try {
            List<String> list = JSONObject.parseObject(strFileRealPath, List.class);
            for (String path : list) {
                String[] split = path.split("#nzm#");
                String fileName = split[0]; // 压缩文件真实名  Java异常体系.png
                String pathStr = split[1]; // 压缩文件全路径 E:\WEAVER\cus_file\2021\04\12\4dfaa437-2432-4c08-bf33-4eec303f5627.zip

                String zipName = FileUtil.getName(pathStr); // 压缩文件的文件名 4dfaa437-2432-4c08-bf33-4eec303f5627.zip
                String zipNameNoSuffix = zipName.substring(0, zipName.lastIndexOf(".")); // 无后缀名的文件名
                LOGGER.info(fileName + ", " + pathStr);

                byte[] bytes = ZipUtil.unzipFileBytes(pathStr, zipNameNoSuffix);

                // 附件对象
                DocAttachment da = new DocAttachment();
                da.setDocid(0);
                da.setImagefileid(0);
                da.setFilecontent(Base64.encode(bytes));
                da.setFilerealpath(strFileRealPath);
                da.setIszip(1);
                da.setFilename(fileName);
                da.setDocfiletype("3");
                da.setIsextfile("0");

                // 文档对象
                DocInfo doc = new DocInfo();
                doc.setDoccreaterid(3);
                doc.setDoccreatertype(0);
                doc.setAccessorycount(1);
                doc.setSeccategory(intFileId);
                doc.setOwnerid(3);
                doc.setDocStatus(1);
                doc.setId(0);
                doc.setDocType(2);
                doc.setDocSubject(fileName);
                doc.setDoccontent(fileName);
                doc.setAttachments(new DocAttachment[]{da});

                DocServiceImpl docService = new DocServiceImpl();
                int docId = docService.createDoc(doc, session);
                stringJoiner.add(String.valueOf(docId));
            }

        } catch (Exception e) {
            LOGGER.error("自定义附件上传异常： " + e);
        }
        return stringJoiner.toString();
    }
}

