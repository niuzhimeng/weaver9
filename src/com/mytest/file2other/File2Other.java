package com.mytest.file2other;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.TimeUtil;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件另存为
 */
public class File2Other extends BaseAction {

    private static final String SEPARATOR = File.separator;

    @Override
    public String execute(RequestInfo requestInfo) {
        String pf = this.getPropValue("fileToOther", "pf"); // ecology所在盘符
        String requestId = requestInfo.getRequestid();
        String operateType = requestInfo.getRequestManager().getSrc();
        int formId = requestInfo.getRequestManager().getFormid();
        String tableName = "";
        RecordSet updateSet = new RecordSet();
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("SELECT tablename FROM workflow_bill WHERE id = '" + formId + "'");
        if (recordSet.next()) {
            tableName = recordSet.getString("tablename");
        }

        String updateSql = "update " + tableName + " set wjl = ? where requestid = ?";
        this.writeLog("文件另存为 Start requestid=" + requestId + "  operatetype --- " + operateType + "   fromTable --- " + tableName);
        try {
            // 拼接路径
            String now = TimeUtil.getCurrentDateString();
            String year = now.substring(0, 4);
            String month = now.substring(5, 7);
            String day = now.substring(8);
            // E:\WEAVER\cus_file\2021\04\12
            String baseStr = pf + SEPARATOR + "WEAVER" + SEPARATOR + "cus_file" + SEPARATOR + year + SEPARATOR +
                    month + SEPARATOR + day + SEPARATOR;
            this.writeLog("基础路径： " + baseStr);

            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            recordSet.next();
            // 附件
            String ids = recordSet.getString("xgfj");
            if (StringUtils.isBlank(ids)) {
                this.writeLog("无附件，不执行操作========");
                return "1";
            }

            List<String> list = new ArrayList<>();
            String pathSql = "SELECT im.imagefileid,im.imagefilename,im.filerealpath, df.docid FROM ImageFile im LEFT JOIN " +
                    "DocImageFile df ON df.imagefileid = im.imagefileid WHERE df.docid IN ( " + ids + " )";
            recordSet.executeQuery(pathSql);
            while (recordSet.next()) {
                String docId = recordSet.getString("docid");
                String imageFileName = recordSet.getString("imagefilename");
                String fileRealPath = recordSet.getString("filerealpath");

                String newPath = baseStr + FileUtil.getName(fileRealPath);
                this.writeLog("docId: " + docId + ", 文件名： " + imageFileName + "; 文件路径： " + fileRealPath +
                        ", 拼接后新路径： " + newPath);

                myFileCopy(fileRealPath, newPath);

                list.add(imageFileName + "#nzm#" + newPath);
            }
            if (list.size() > 0) {
                updateSet.executeUpdate(updateSql, JSONObject.toJSONString(list), requestId);
            }

            this.writeLog("文件另存为 End ===============");
        } catch (Exception e) {
            this.writeLog("文件另存为 Error： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("文件另存为 Error： " + e);
            return "0";
        }

        return "1";
    }


    private void myFileCopy(String inputStr, String outputStr) {
        try (
                BufferedInputStream inputStream = FileUtil.getInputStream(inputStr);
                BufferedOutputStream outputStream = FileUtil.getOutputStream(outputStr)
        ) {
            IoUtil.copy(inputStream, outputStream, IoUtil.DEFAULT_BUFFER_SIZE);
        } catch (Exception e) {
            new BaseBean().writeLog("myFileCopy复制文件异常： " + e);
        }
    }

}
