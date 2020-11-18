package com.mytest;


import org.apache.axis.encoding.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.docs.webservices.DocAttachment;
import weaver.docs.webservices.DocInfo;
import weaver.docs.webservices.DocServiceImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

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
    public static Integer createNewDoc(String session, String strFileRealPath, int intFileId) {
        int docId = 0;
        File file = new File(strFileRealPath);
        String strFileName = file.getName();
        try (
                InputStream input = new FileInputStream(file);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
        ) {
            int byteread;
            byte[] data = new byte[1024];

            while ((byteread = input.read(data)) != -1) {
                out.write(data, 0, byteread);
                out.flush();
            }
            byte[] content = out.toByteArray();

            // 附件对象
            DocAttachment da = new DocAttachment();
            da.setDocid(0);
            da.setImagefileid(0);
            da.setFilecontent(Base64.encode(content));
            da.setFilerealpath(strFileRealPath);
            da.setIszip(1);
            da.setFilename(strFileName);
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
            doc.setDocSubject(strFileName);
            doc.setDoccontent(strFileName);
            doc.setAttachments(new DocAttachment[]{da});

            DocServiceImpl docService = new DocServiceImpl();
            docId = docService.createDoc(doc, session);
        } catch (Exception e) {
            LOGGER.error("自定义附件上传异常： " + e);
        }
        return docId;
    }
}

