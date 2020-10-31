package com.weavernorth.meitanzy.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mytest.SftpUtil;
import com.weavernorth.meitanzy.service.PushService;
import com.weavernorth.meitanzy.util.ConnUtil;
import com.weavernorth.meitanzy.util.MeiTanConfigInfo;
import com.weavernorth.meitanzy.util.MtHttpUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.RecordSet;
import weaver.general.TimeUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 合同签订信息上报接口
 */
public class HtqdService implements PushService {

    private static final Log LOGGER = LogFactory.getLog(HtqdService.class);

    @Override
    public String push(String ids, String token) {
        LOGGER.info("合同签订信息上报接口Start ================== 此次推送数据id： " + ids);
        // 状态改为推送中
        changeToPushing(ids);

        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("select * from uf_Mkzy_htgl where id in (" + ids + ")");
        while (recordSet.next()) {
            String mainId = recordSet.getString("id");
            JSONObject jsonObject = new JSONObject(true);
            String htzbh = recordSet.getString("htzbh");
            jsonObject.put("contractUniqueId", htzbh); // 合同唯一标识
            jsonObject.put("contractType", ConnUtil.getGgxzk(MeiTanConfigInfo.HTGL.getValue(), recordSet.getString("sjhtfl"))); // 合同类型
            jsonObject.put("contractSubject", ConnUtil.getGgxzk(MeiTanConfigInfo.HTGL.getValue(), recordSet.getString("sjhtflcphjsmc"))); // 合同标的
            jsonObject.put("contractName", recordSet.getString("htmc")); // 合同名称
            jsonObject.put("contractSelfCode", htzbh); // 合同自编号

            jsonObject.put("buyMethod", recordSet.getString("cgfs")); // 采购方式
            jsonObject.put("bidFile", pushFileToSftp(recordSet.getString("zbtzs"), htzbh, "zbtzs")); // 中标通知书
            jsonObject.put("contractAmount", recordSet.getString("htjezgje")); // 合同金额/暂估金额
            jsonObject.put("valuationMode", recordSet.getString("jjfs")); // 计价方式
            jsonObject.put("currencyName", recordSet.getString("bz")); // 币种

            jsonObject.put("exchangeRate", recordSet.getString("hl")); // 汇率
            jsonObject.put("amountExplain", recordSet.getString("htjesm")); // 合同金额说明
            jsonObject.put("paymentDirection", recordSet.getString("szfx")); // 收支方向
            jsonObject.put("paymentType", recordSet.getString("szfx")); // 合同收/付款类型
            jsonObject.put("paymentMethod", recordSet.getString("htsfkfs")); // 合同收/付款方式

            jsonObject.put("signingSubject", recordSet.getString("wfqyzt")); // 我方签约主体
            jsonObject.put("signingSubjectCode", recordSet.getString("qyztbm")); // 签约主体编码
            jsonObject.put("creatorAccount", recordSet.getString("jbrzh")); // 经办人账号
            jsonObject.put("creatorName", ConnUtil.getSysByFiled("lastname", "hrmresource", recordSet.getString("jbrzh"))); // 经办人名称
            jsonObject.put("creatorDeptCode", recordSet.getString("jbbmbm")); // 经办部门编码

            jsonObject.put("creatorDeptName", ConnUtil.getSysByFiled("departmentname", "hrmdepartment", recordSet.getString("jbbm"))); // 经办部门
            jsonObject.put("performAddress", recordSet.getString("htlxd")); // 合同履行地
            jsonObject.put("signAddress", recordSet.getString("htqsd")); // 合同签署地
            jsonObject.put("contractPeriod", recordSet.getString("htqxlx")); // 合同期限类型
            jsonObject.put("performPeriod", recordSet.getString("htlxqx")); // 合同履行期限

            jsonObject.put("periodExplain", recordSet.getString("htqxsm")); // 期限说明
            jsonObject.put("ourIsAuth", recordSet.getString("wfsfsq")); // 是否授权（我方）
            jsonObject.put("authType", recordSet.getString("sqlx")); // 授权类型

            // 合同正文
            JSONArray contractText = new JSONArray();
            JSONObject zwObj = new JSONObject(true);
            zwObj.put("filename", "正文名称");
            zwObj.put("filepath", "附件路径");
            zwObj.put("createtime", "createtime");
            zwObj.put("num", "1");
            contractText.add(zwObj);
            jsonObject.put("contractText", contractText);

            // 合同审批单
            JSONArray contractApprovalForm = new JSONArray();
            JSONObject spdObj = new JSONObject(true);
            spdObj.put("filename", "正文名称");
            spdObj.put("filepath", "附件路径");
            spdObj.put("createtime", "createtime");
            spdObj.put("num", "1");
            contractApprovalForm.add(spdObj);
            jsonObject.put("contractApprovalForm", contractApprovalForm);

            // 相对方联系人
            JSONArray relOppositeInfoList = new JSONArray();
            JSONObject xdfObj = new JSONObject(true);
            xdfObj.put("oppositeUniqueId", "相对方唯一标识");
            xdfObj.put("oppositeName", "相对方名称");
            xdfObj.put("oppositeRelName", "相对方联系人");
            relOppositeInfoList.add(xdfObj);
            jsonObject.put("relOppositeInfoList", relOppositeInfoList);

            JSONObject allObj = new JSONObject();
            allObj.put("contractInfo", jsonObject);
            System.out.println(allObj.toJSONString());
        }

        // 发送文件
        com.mytest.SftpUtil sftpUtil = new com.mytest.SftpUtil(MeiTanConfigInfo.FTP_USERNAME.getValue(), MeiTanConfigInfo.FTP_PASSWORD.getValue(),
                MeiTanConfigInfo.FTP_URL.getValue(), Integer.parseInt(MeiTanConfigInfo.FTP_PORT.getValue()));

        // 发送请求
        Map<String, String> headerMap = ConnUtil.getHeader(token);
        String returnStr = MtHttpUtil.postJsonHeader(MeiTanConfigInfo.QIAN_DING_URL.getValue(), "", headerMap);
        LOGGER.info("合同签订信息上报接口返回信息： " + returnStr);

        LOGGER.info("合同签订信息上报接口End ==================");

        return "";
    }

    /**
     * 将数据状态变为 推送中
     *
     * @param ids 要推送的id数组
     */
    public void changeToPushing(String ids) {
        RecordSet recordSet = new RecordSet();
        recordSet.executeUpdate("update uf_xxx set xxx = 1 where id in (" + ids + ")");
    }
    /**
     * /home/document/单位编码/YYYYMM/合同编码/附件字段名称/
     * 推送文件至ftp服务器
     *
     * @param ids       附件id
     * @param htbm      合同编码
     * @param fieldName 附件字段名
     */
    public JSONArray pushFileToSftp(String ids, String htbm, String fieldName) {
        LOGGER.info("pushFileToSftp开始==========");
        JSONArray jsonArray = new JSONArray();
        if (StringUtils.isBlank(ids)) {
            return jsonArray;
        }

        com.mytest.SftpUtil sftpUtil = new SftpUtil(MeiTanConfigInfo.FTP_USERNAME.getValue(), MeiTanConfigInfo.FTP_PASSWORD.getValue(),
                MeiTanConfigInfo.FTP_URL.getValue(), Integer.parseInt(MeiTanConfigInfo.FTP_PORT.getValue()));
        sftpUtil.login();

        String currentDate = TimeUtil.getCurrentDateString().substring(0, 8).replace("-", "").replaceAll("\\s*", "");
        String savePath = "/home/document/" + MeiTanConfigInfo.DWBM.getValue() + "/" + currentDate + "/" + htbm + "/" + fieldName + "/";
        LOGGER.info("附件上传路径： " + savePath);

        RecordSet recordSet = new RecordSet();
        String pathSql = "SELECT im.imagefileid,im.imagefilename,im.filerealpath FROM ImageFile im LEFT JOIN " +
                "DocImageFile df ON df.imagefileid = im.imagefileid WHERE df.docid IN ( " + ids + " )";
        recordSet.executeQuery(pathSql);
        while (recordSet.next()) {
            JSONObject jsonObject = new JSONObject();
            String imagefilename = recordSet.getString("imagefilename");
            String filerealpath = recordSet.getString("filerealpath");
            try {
                File srcFile = new File(filerealpath);
                // 开始解压
                ZipFile zipFile = new ZipFile(srcFile);
                Enumeration<?> entries = zipFile.entries();
                if (entries.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry) entries.nextElement();
                    // 将压缩文件内容写入到这个文件中
                    InputStream is = zipFile.getInputStream(entry);
                    sftpUtil.upload(savePath, imagefilename, is);

                    is.close();
                }
                zipFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            jsonObject.put("filename", imagefilename);
            jsonObject.put("filepath", savePath + imagefilename);
            jsonObject.put("createtime", "");
            jsonObject.put("num", 1);
            jsonArray.add(jsonObject);
        }
        sftpUtil.logout();
        return jsonArray;
    }





    private void unZip(String srcFilePath, String destDirPath) throws RuntimeException, IOException {
        File srcFile = new File(srcFilePath);
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
        }
        // 开始解压
        ZipFile zipFile = new ZipFile(srcFile);

        Enumeration<?> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            // 将压缩文件内容写入到这个文件中
            InputStream is = zipFile.getInputStream(entry);


            is.close();
        }

        try {
            zipFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
