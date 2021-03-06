package com.weavernorth.meitanzy.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import weaver.conn.RecordSet;
import weaver.general.TimeUtil;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ConnUtil {

    private static final Log LOGGER = LogFactory.getLog(ConnUtil.class);

    /**
     * 查询流程下拉框汉字显示
     *
     * @param fieldId     下拉框择框字段
     * @param selectValue 表单中下拉框的值
     */
    public static String getWorkflowSelect(String fieldId, String selectValue) {
        RecordSet recordSet = new RecordSet();
        String returnStr = "";
        recordSet.executeQuery(" SELECT selectname FROM workflow_selectitem WHERE fieldid = '" + fieldId + "' and selectvalue = '" + selectValue + "'");
        if (recordSet.next()) {
            returnStr = recordSet.getString("selectname");
        }
        return returnStr;
    }

    /**
     * 查询公共选择框的汉字显示
     *
     * @param mainId   公共选择框id
     * @param disorder 选项id
     */
    public static String getGgxzk(String mainId, String disorder) {
        RecordSet recordSet = new RecordSet();
        String returnStr = "";
        recordSet.executeQuery(" SELECT NAME FROM MODE_SELECTITEMPAGEDETAIL WHERE MAINID = '" + mainId + "' and DISORDER = '" + disorder + "'");
        if (recordSet.next()) {
            returnStr = recordSet.getString("NAME");
        }
        return returnStr;
    }

    /**
     * 根据某一字段查另一个字段
     *
     * @param resultField 查询的字段名
     * @param tableName   查询表名
     * @param selField    条件字段名
     */
    public static String getSysByFiled(String resultField, String tableName, String selField) {
        RecordSet recordSet = new RecordSet();
        String returnStr = "";
        recordSet.executeQuery("select " + resultField + " from " + tableName + " where id = '" + selField + "'");
        if (recordSet.next()) {
            returnStr = recordSet.getString(resultField);
        }
        return returnStr;
    }

    /**
     * 根据城市id获取汉字  如：黑龙江省齐齐哈尔市
     *
     * @param cityId 城市id
     * @return 省 + 市名字
     */
    public static String getProvinceAndCityNameById(String cityId) {
        if (StringUtils.isBlank(cityId)) {
            return "";
        }
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("select b.provincedesc, a.cityname from hrmcity a left join hrmprovince b on a.provinceid = b.id where a.id = " + cityId);
        recordSet.next();
        return recordSet.getString("provincedesc") + recordSet.getString("cityname");
    }

    /**
     * 获取请求头
     */
    public static Map<String, String> getHeader(String token) {
        String currentTime = TimeUtil.getCurrentTimeString().replace("-", "")
                .replace(":", "").replaceAll("\\s*", "");
        // 请求头map拼接
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("appuser", MeiTanConfigInfo.appuser.getValue());
        headerMap.put("token", token);
        headerMap.put("timestamp", currentTime);

        return headerMap;
    }

    /**
     * 推送附件至ftp
     *
     * @param ids       附件id  1,2,3
     * @param htbm      合同编码
     * @param fieldName 附件字段名
     * @return
     */
    public static JSONArray pushFileToFtp(String ids, String htbm, String fieldName) {
        JSONArray jsonArray = new JSONArray();
        if (StringUtils.isBlank(ids)) {
            return jsonArray;
        }

        String currentDate = TimeUtil.getCurrentDateString().substring(0, 8).replace("-", "").replaceAll("\\s*", "");
        String savePath = "/home/document/" + MeiTanConfigInfo.DWBM.getValue() + "/" + currentDate + "/" + htbm + "/" + fieldName + "/";
        LOGGER.info("附件上传路径： " + savePath);

        FTPClient ftpClient = MeiTanZyFtpUtil.getFtpClient(MeiTanConfigInfo.FTP_URL.getValue(), Integer.parseInt(MeiTanConfigInfo.FTP_PORT.getValue()),
                MeiTanConfigInfo.FTP_USERNAME.getValue(), MeiTanConfigInfo.FTP_PASSWORD.getValue());

        RecordSet recordSet = new RecordSet();
        String pathSql = "SELECT im.imagefileid,im.imagefilename,im.filerealpath FROM ImageFile im LEFT JOIN " +
                "DocImageFile df ON df.imagefileid = im.imagefileid WHERE df.docid IN ( " + ids + " )";
        recordSet.executeQuery(pathSql);
        try {
            while (recordSet.next()) {
                JSONObject jsonObject = new JSONObject();
                String imagefilename = recordSet.getString("imagefilename");
                String filerealpath = recordSet.getString("filerealpath");
                LOGGER.info("文件名： " + imagefilename + "; 文件路径： " + filerealpath);

                // 防止中文名乱码
                String imagefilenameFtp = new String(imagefilename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
                File srcFile = new File(filerealpath);
                // 开始解压
                ZipFile zipFile = new ZipFile(srcFile);
                Enumeration<?> entries = zipFile.entries();
                if (entries.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry) entries.nextElement();
                    // 将压缩文件内容写入到这个文件中
                    InputStream inputStream = zipFile.getInputStream(entry);
                    // 上传文件
                    boolean upload = MeiTanZyFtpUtil.upload(savePath, imagefilenameFtp, inputStream, ftpClient);
                    LOGGER.info("文件 " + imagefilename + "上传结果： " + upload);
                    inputStream.close();
                }
                zipFile.close();
                jsonObject.put("filename", imagefilename);
                jsonObject.put("filepath", savePath + imagefilename);
                jsonObject.put("createtime", TimeUtil.getCurrentDateString());
                jsonObject.put("num", 1);
                jsonArray.add(jsonObject);
            }
        } catch (Exception e) {
            LOGGER.error("文件上传异常： " + e);
        } finally {
            // 关闭ftp连接
            MeiTanZyFtpUtil.disconnect(ftpClient);
        }
        return jsonArray;
    }

    /**
     * 获取接口请求token
     */
    public static String getToken() {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("appuser", MeiTanConfigInfo.appuser.getValue()); // 应用授权代码
        headerMap.put("secretkey", MeiTanConfigInfo.secretkey.getValue());// 秘钥
        String tokenStr = MtHttpUtil.postHeader(MeiTanConfigInfo.LOGIN_URL.getValue(), headerMap);
        LOGGER.info("本次获取token接口返回： " + tokenStr);

        String token = "";
        try {
            JSONObject jsonObject = JSONObject.parseObject(tokenStr);
            token = jsonObject.getString("token");
        } catch (Exception e) {
            LOGGER.error("getToken()异常： " + e);
        }

        return token;
    }

    /**
     * 解析接口返回数据
     *
     * @param returnStr [状态，信息]
     */
    public static String[] parseReturnJson(String returnStr) {
        String state = "1"; // 0成功；1失败
        String message;
        try {
            JSONObject returnObj = JSONObject.parseObject(returnStr);
            String code = returnObj.getString("code");
            if ("200".equals(code)) {
                state = "0";
            }
            message = code + " " + returnObj.getString("message");
        } catch (Exception e) {
            message = "接口返回异常：" + returnStr;
            state = "1";
        }

        return new String[]{state, message};
    }

}
