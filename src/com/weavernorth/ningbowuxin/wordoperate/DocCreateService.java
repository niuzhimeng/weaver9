package com.weavernorth.ningbowuxin.wordoperate;

import org.apache.axis.encoding.Base64;
import weaver.docs.webservices.DocAttachment;
import weaver.docs.webservices.DocInfo;
import weaver.docs.webservices.DocService;
import weaver.docs.webservices.DocServiceImpl;
import weaver.general.BaseBean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class DocCreateService {
    private static DocService service = null;
    private static String strLoginId = "";
    private static String strPassword = "";
    private static String strIP = "127.0.0.1";
    private static String strSession = "";

    private static BaseBean baseBean = new BaseBean();

    /**
     * 有参构造
     *
     * @param strLoginIdPa  登录账号
     * @param strPassWordPa 登录密码
     */
    public DocCreateService(String strLoginIdPa, String strPassWordPa) {
        try {

            service = new DocServiceImpl();
            DocCreateService.strLoginId = strLoginIdPa;
            DocCreateService.strPassword = strPassWordPa;
            int logintype = 0;
            //登陆验证
            strSession = getSession(strLoginId, strPassword, logintype, strIP);
        } catch (Exception e) {
            e.printStackTrace();
			baseBean.writeLog("==登陆验证异常==>" + e);
        }
    }

    /**
     * @param strFileName     文档名称
     * @param strFileRealPath 文档路径
     */
    public String init(String strFileName, String strFileRealPath, String strFileId, String strRequestid) {
        //创建文档
        String strCreateNewDoc = "-1";
        try {
            strCreateNewDoc = createNewDoc(strSession, strFileName, strFileRealPath, strFileId, strRequestid);
        } catch (Exception e) {
			baseBean.writeLog("创建文档异常" + e);
        }

        return strCreateNewDoc;
    }

    /**
     * 验证登陆
     *
     * @return session
     * @throws Exception
     */
    private static String getSession(String loginid, String password, int logintype, String ip) throws Exception {
        // 登陆 //用户名，密码，登陆方式(0 数据库验证;1 动态密码验证;2 LDAP验证)，ip
        String session = service.login(loginid, password, 0, ip);
        return session;
    }


    /**
     * 创建新文档
     *
     * @param session         登陆session
     * @param strFileRealPath 文档实际存放路径 -绝对路径
     * @throws Exception
     */
    public static String createNewDoc(String session, String strFileName, String strFileRealPath, String strFileId, String strRequestid) throws Exception {
        byte[] content = new byte[102400];
        // 上传附件，创建html文档
        int intFileId = Integer.parseInt(strFileId);
        content = null;


        try {
            int byteread;
            byte data[] = new byte[1024];
            InputStream input = new FileInputStream(new File(strFileRealPath));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while ((byteread = input.read(data)) != -1) {
                out.write(data, 0, byteread);
                out.flush();
            }
            content = out.toByteArray();
            input.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DocAttachment da = new DocAttachment();

        da.setDocid(0);
        da.setImagefileid(0);
        da.setFilecontent(Base64.encode(content));
        da.setFilerealpath(strFileRealPath);
        da.setIszip(1);
        da.setFilename(strFileName);

        da.setDocfiletype("3");
        da.setIsextfile("0");
        DocInfo doc = new DocInfo();//创建文档
        doc.setDoccreaterid(3);//
        doc.setDoccreatertype(0);
        doc.setAccessorycount(1);
        //doc.setMaincategory(10);//主目录id
        //doc.setSubcategory(6);//分目录id

        doc.setSeccategory(intFileId);//子目录id
        doc.setOwnerid(3);
        doc.setDocStatus(1);
        doc.setId(0);
        doc.setDocType(2);
        doc.setDocSubject(strFileName);
        doc.setDoccontent(strFileName);
        doc.setAttachments(new DocAttachment[]{da});
        int strdocid = service.createDoc(doc, session);
		baseBean.writeLog("新文档id:" + strdocid);
//		RecordSet rs = new RecordSet();
//		
//		//获取流程的表名
//		String strTableName =SelectUtil.getTableName(strRequestid);
//		String strFile = rs.getPropValue("ftpreport","filed");
//		//获取流程的正文转PDF字段
//		//String strFile =rs.getPropValue("TZWebPDFWorkflowInfo","file");
//		/*rs.executeSql("select imagefileid from DocImageFile where docid = "+service.createDoc(doc, session));
//		if(rs.next()){
//			strImagefileid = Util.null2String(rs.getString("imagefileid"));
//		}*/
//		rs.executeSql("update "+strTableName+ " set "+strFile+" = '"+strdocid+"' where requestid = "+strRequestid);
//		LogUtil.debugLog("update "+strTableName+ " set "+strFile+" = '"+strdocid+"' where requestid = "+strRequestid);
        return String.valueOf(strdocid);
    }
}

