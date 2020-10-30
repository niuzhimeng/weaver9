package com.weavernorth.meitanzy.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weavernorth.meitanzy.service.PushService;
import com.weavernorth.meitanzy.util.MeiTanConfigInfo;
import com.weavernorth.meitanzy.util.MeiTanZyFtpUtil;
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

/**
 * 收入信息上报接口
 */
public class SrInfoService implements PushService {

    private static final Log LOGGER = LogFactory.getLog(SrInfoService.class);

    @Override
    public String push(String ids, String token) {
        LOGGER.info("收入信息上报接口Start========推送数据id： " + ids);
        String headerTime = TimeUtil.getCurrentTimeString().replace("-", "")
                .replace(":", "").replaceAll("\\s*", "");
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("appuser", MeiTanConfigInfo.appuser.getValue());
        headerMap.put("token", token);
        headerMap.put("timestamp", headerTime);
        LOGGER.info("收入信息上报接口请求头： " + JSONObject.toJSONString(headerMap));

        RecordSet recordSet = new RecordSet();
        RecordSet recordSet_Detail = new RecordSet();
        recordSet.executeQuery("select * from uf_Mkzy_htgl where id in (" + ids + ")");
        while (recordSet.next()) {
            String mainId = recordSet.getString("id");
            String htzbh = recordSet.getString("htzbh");
            String ljskje = recordSet.getString("ljskje");

            JSONObject allObj = new JSONObject();
            JSONObject jsonObject = new JSONObject(true);
            JSONArray jsonArray = new JSONArray();
            jsonObject.put("contractUniqueId", htzbh); // 合同唯一标识
            jsonObject.put("incomeTotalAmount", ljskje); // 累计收款金额
            recordSet_Detail.executeQuery("select * from uf_Mkzy_htgl_dt2 where mainid = " + mainId);
            while (recordSet_Detail.next()) {
                JSONObject object = new JSONObject(true);
                object.put("incomeId", recordSet_Detail.getString("srfkid")); // 收入ID
                object.put("incomeAmount", recordSet_Detail.getString("yqrsrje")); // 已确认收入金额
                object.put("assistEvidence", pushFileToFtp(recordSet_Detail.getString("fzzj1"), htzbh, "fzzj1")); // 辅助证据
                object.put("currentPeriodAmount", recordSet_Detail.getString("dqkpje")); // 当期开票金额
                jsonArray.add(object);
                jsonObject.put("incomeInfoList", jsonArray);
            }
            allObj.put("registerIncomeInfo", jsonObject);
            LOGGER.info("传输json： " + allObj.toJSONString());
            // 调用接口
//            String returnStr = MtHttpUtil.postJsonHeader(MeiTanConfigInfo.SHOU_RU_URL.getValue(), allObj.toJSONString(), headerMap);
//            LOGGER.info("收入信息上报接口返回数据： " + returnStr);
        }

        return null;
    }

    /**
     * 推送附件至ftp
     *
     * @param ids       附件id  1,2,3
     * @param htbm      合同编码
     * @param fieldName 附件字段名
     * @return
     */
    public JSONArray pushFileToFtp(String ids, String htbm, String fieldName) {
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
                String imagefilenameFtp = new String(imagefilename.getBytes("GBK"), StandardCharsets.ISO_8859_1);
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


    public static void main(String[] args) {
        JSONObject allObj = new JSONObject();
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("contractUniqueId", ""); // 合同唯一标识
        jsonObject.put("incomeTotalAmount", ""); // 累计收款金额

        JSONArray jsonArray = new JSONArray();
        JSONObject object = new JSONObject(true);
        object.put("incomeId", ""); // 收入ID
        object.put("incomeAmount", ""); // 已确认收入金额
        object.put("assistEvidence", ""); // 辅助证据
        object.put("currentPeriodAmount", ""); // 当期开票金额
        jsonArray.add(object);
        jsonObject.put("incomeInfoList", jsonArray);


        allObj.put("registerIncomeInfo", jsonObject);

        System.out.println(allObj.toJSONString());
    }
}
