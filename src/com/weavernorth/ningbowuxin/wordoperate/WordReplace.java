package com.weavernorth.ningbowuxin.wordoperate;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ZipUtil;
import com.google.gson.Gson;
import weaver.conn.RecordSet;
import weaver.general.TimeUtil;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * word模板内容替换
 */
public class WordReplace extends BaseAction {
    /**
     * 附件字段(配置取值)
     */
    private String fjStr;
    /**
     * 子目录id
     */
    private static final String MU_LU_ID = "122";

    private Gson gson = new Gson();
    private Pattern pattern = Pattern.compile("\\{.*?}");

    @Override
    public String execute(RequestInfo requestInfo) {
        String requestId = requestInfo.getRequestid();
        String operateType = requestInfo.getRequestManager().getSrc();
        int formId = requestInfo.getRequestManager().getFormid();
        String tableName = "";
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("SELECT tablename FROM workflow_bill WHERE id = '" + formId + "'");
        if (recordSet.next()) {
            tableName = recordSet.getString("tablename");
        }

        this.writeLog("word模板替换 Start requestid --- " + requestId + "  时间 --- " + TimeUtil.getCurrentTimeString()
                + "   fromTable --- " + tableName + ", operateType: " + operateType);

        BufferedInputStream inputStream1 = null;
        BufferedOutputStream outputStream1 = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        BufferedInputStream inputStream2 = null;
        BufferedOutputStream outputStream2 = null;

        try {
            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            recordSet.next();
            this.writeLog("word标记替换开始 ；========");
            // 将模板复制一份 zip格式
            inputStream1 = FileUtil.getInputStream("d:/weaver/ecology/wordTemplate/1.docx");

            File file = new File("d:/weaver/ecology/temp/1.zip");

            outputStream1 = FileUtil.getOutputStream(file);
            long copySize = IoUtil.copy(inputStream1, outputStream1, IoUtil.DEFAULT_BUFFER_SIZE);
            this.writeLog("另存一份zip文件完成，copySize: " + copySize);

            // 解压该zip文件 读取document.xml文件
            File unzip = ZipUtil.unzip(file);
            this.writeLog("解压完成： " + unzip);

            String unPathStr = unzip.toString() + File.separatorChar + "word" + File.separatorChar + "document.xml";
            this.writeLog("xml路径： " + unPathStr);

            bufferedReader = FileUtil.getReader(unPathStr, "utf-8");

            String xmlStr = IoUtil.read(bufferedReader);
            this.writeLog("读取到的xml： " + xmlStr);
            this.writeLog("xml长度： " + xmlStr.length());

            // 替换xml
            Map<String, String> map = new HashMap<>();
            Matcher matcher = pattern.matcher(xmlStr);
            while (matcher.find()) {
                String group = matcher.group();
                String substring = group.substring(1, group.length() - 1);
                map.put(group, substring);
            }
            this.writeLog("拼接好的Map<标记, value>： " + gson.toJson(map));

            for (Map.Entry<String, String> entry : map.entrySet()) {
                xmlStr = xmlStr.replace(entry.getKey(), recordSet.getString(entry.getValue()));
            }

            // 输出新的xml false 表示不追加
            bufferedWriter = FileUtil.getWriter(unPathStr, "utf-8", false);
            bufferedWriter.write(xmlStr);
            bufferedWriter.flush();

            // 重新压缩
            File zip = ZipUtil.zip(unzip);
            String inPath = zip.toString();
            this.writeLog("重新压缩后的路径： " + inPath);

            // 复制一份为 .docx的文件
            String outPath = inPath.substring(0, inPath.lastIndexOf(".")) + ".docx";
            inputStream2 = FileUtil.getInputStream(inPath);
            outputStream2 = FileUtil.getOutputStream(outPath);
            IoUtil.copy(inputStream2, outputStream2, IoUtil.DEFAULT_BUFFER_SIZE);

            this.writeLog("模板填充完成： " + outPath);
            // 上传附件
            DocCreateService service = new DocCreateService("sysadmin", "1");
            String strDocId = service.init("生成的附件" + ".docx", outPath, MU_LU_ID, "");

            // 更新流程表单
            RecordSet updateSet = new RecordSet();
            updateSet.executeUpdate("update " + tableName + " set " + fjStr + " = '" + strDocId + "' where requestid = " + requestId);

            this.writeLog("word模板替换 End ===============" + TimeUtil.getCurrentTimeString());
        } catch (Exception e) {
            this.writeLog("word模板替换 Err: " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("word模板替换 Err： " + e);
            return "0";
        } finally {
            myClose(inputStream1, outputStream1, bufferedReader, bufferedWriter, inputStream2, outputStream2);
        }

        return "1";
    }

    private void myClose(Closeable... closeableList) {
        for (Closeable closeable : closeableList) {
            try {
                if (closeable != null) {
                    closeable.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getFjStr() {
        return fjStr;
    }

    public void setFjStr(String fjStr) {
        this.fjStr = fjStr;
    }
}
