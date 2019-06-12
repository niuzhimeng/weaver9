package com.weavernorth.siwei.replace;


import com.google.gson.Gson;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.*;
import weaver.conn.RecordSet;
import weaver.general.TimeUtil;
import weaver.general.Util;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * doc、docx 页眉标记替换
 */
public class ReplaceWord extends BaseAction {
    /**
     * 替换标记
     */
    private static final String BH_FLAG = "{number}";
    /**
     * 附件字段 例：1,2,3
     */
    private String fjStr;
    /**
     * 编号字段（取值字段）
     */
    private String bhStr;

    /**
     * 编号值
     */
    private String bhStrValue;

    private Gson gson = new Gson();

    private Pattern pattern = Pattern.compile("\\{(number)}", Pattern.CASE_INSENSITIVE);

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

        this.writeLog("页眉标记替换 Start requestid --- " + requestId + "  时间 --- " + TimeUtil.getCurrentTimeString()
                + "   fromTable --- " + tableName + ", operateType: " + operateType);
        try {
            this.writeLog("fjStr: " + fjStr);
            // 附件字段名数组
            String[] fjStrs = fjStr.split(",");
            this.writeLog("fjStrs: " + gson.toJson(fjStrs));

            // 字段名 - 附件id数组
            Map<String, String[]> fileMap = new HashMap<>();
            // 查询主表
            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            recordSet.next();
            bhStrValue = recordSet.getString(bhStr);
            String tempStr;
            for (String str : fjStrs) {
                tempStr = Util.null2String(recordSet.getString(str));
                if (!"".equals(tempStr)) {
                    fileMap.put(str, tempStr.split(","));
                }
            }
            this.writeLog("字段名 - 附件id数组: " + gson.toJson(fileMap));

            // 查询附件 名称 - 所在路径
            RecordSet pathSet = new RecordSet();
            // 解压后输出位置
            String outputPath;
            // 系统加密后的名字
            String fileName;
            for (Map.Entry<String, String[]> entry : fileMap.entrySet()) {
                String changedId = shuZuChange(entry.getValue());
                String pathSql = "SELECT im.imagefileid,im.imagefilename,im.filerealpath FROM ImageFile im LEFT JOIN DocImageFile df ON df.imagefileid = im.imagefileid WHERE df.docid IN ( " + changedId + " )";
                this.writeLog("拼接后sql： " + pathSql);

                pathSet.executeQuery(pathSql);
                RecordSet updateSet = new RecordSet();

                while (pathSet.next()) {
                    String filePath = pathSet.getString("filerealpath");
                    this.writeLog("变更前filerealpath: " + pathSet.getString("filerealpath") + " ,imagefilename: " + pathSet.getString("imagefilename"));
                    outputPath = filePath.substring(0, filePath.lastIndexOf(File.separator) + 1) + UUID.randomUUID().toString() + ".zip";

                    fileName = outputPath.substring(outputPath.lastIndexOf(File.separator) + 1, outputPath.lastIndexOf("."));
                    this.writeLog("变更后outputPath============= " + outputPath);
                    this.writeLog("fileName============= " + fileName);
                    // 解压输出
                    if (pathSet.getString("imagefilename").contains("docx")) {
                        this.writeLog("替换docx文件======");
                        unZip(pathSet.getString("filerealpath"), outputPath, "docx", fileName);
                    } else {
                        this.writeLog("替换doc文件======");
                        unZip(pathSet.getString("filerealpath"), outputPath, "doc", fileName);
                    }
                    // 更新路径
                    outputPath = outputPath.replace("\\", "\\\\");
                    String updateSql = "update ImageFile set filerealpath = '" + outputPath + "' where imagefileid = " + pathSet.getString("imagefileid");
                    this.writeLog("更新sql： " + updateSql);
                    updateSet.execute(updateSql);
                }
            }

            this.writeLog("页眉标记替换 End ===============" + TimeUtil.getCurrentTimeString());
        } catch (Exception e) {
            this.writeLog("页眉标记替换 异常： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("添加合同编号 异常： " + e);
            return "0";
        }

        return "1";
    }


    /**
     * docx 替换页眉
     */
    private void replaceDocx(InputStream inputStream, String outPath, String fileName) {
        try (
                OutputStream outputStream = new FileOutputStream(outPath);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(outputStream)
        ) {
            XWPFDocument docx = new XWPFDocument(inputStream);
            List<XWPFHeader> headerList = docx.getHeaderList();
            for (XWPFHeader xwpfHeader : headerList) {
                this.writeLog("页眉： " + xwpfHeader.getText());
                for (Object paragraph : xwpfHeader.getParagraphs()) {
                    replaceText((XWPFParagraph) paragraph);
                }
            }

            //导出到文件
            docx.write(byteArrayOutputStream);
            zos.putNextEntry(new ZipEntry(fileName));
            zos.write(byteArrayOutputStream.toByteArray());
            zos.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void replaceText(XWPFParagraph para) {
        Matcher matcher;
        if (pattern.matcher(para.getParagraphText()).find()) {
            List<XWPFRun> runs = para.getRuns();
            int size = runs.size();
            for (int i = 0; i < size; i++) {
                XWPFRun run = runs.get(i);
                int fontSize = run.getFontSize();
                String runText = run.toString();
                matcher = pattern.matcher(runText);
                if (matcher.find()) {
                    while ((matcher = pattern.matcher(runText)).find()) {
                        runText = matcher.replaceFirst(bhStrValue);
                    }
                    para.removeRun(i);

                    XWPFRun xwpfRun = para.insertNewRun(i);
                    xwpfRun.setUnderline(UnderlinePatterns.SINGLE);
                    xwpfRun.setFontSize(fontSize);
                    xwpfRun.setText(runText);
                }
            }
        }
    }

    /**
     * doc替换页眉
     */
    private void replaceDoc(InputStream inputStream, String outPath, String fileName) {
        try (
                OutputStream outputStream = new FileOutputStream(outPath);
                ZipOutputStream zos = new ZipOutputStream(outputStream);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        ) {
            HWPFDocument doc = new HWPFDocument(inputStream);
            Range range = doc.getHeaderStoryRange();
            range.replaceText(BH_FLAG, bhStrValue);

            //导出到文件
            doc.write(byteArrayOutputStream);
            zos.putNextEntry(new ZipEntry(fileName));
            zos.write(byteArrayOutputStream.toByteArray());
            zos.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 解压 替换标签并输出
     *
     * @param srcFilePath 源文件位置
     * @param outPath     输出路径
     * @param type        doc 或者 docx
     * @throws RuntimeException
     */
    private void unZip(String srcFilePath, String outPath, String type, String fileName) throws RuntimeException {
        InputStream is = null;
        File srcFile = new File(srcFilePath);
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
        }
        // 开始解压
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(srcFile, Charset.forName("gbk"));
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                System.out.println(entry.getName());
                // 将压缩文件内容写入到这个文件中
                is = zipFile.getInputStream(entry);
            }
            // 替换标签并输出
            if ("doc".equals(type)) {
                replaceDoc(is, outPath, fileName);
            } else {
                replaceDocx(is, outPath, fileName);
            }


        } catch (Exception e) {
            throw new RuntimeException("unzip error from ZipUtils", e);
        } finally {
            if (zipFile != null) {
                try {
                    is.close();
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    private String shuZuChange(String[] strs) {
        StringJoiner joiner = new StringJoiner(",");
        for (String str : strs) {
            joiner.add(str);
        }
        return joiner.toString();
    }

    public String getFjStr() {
        return fjStr;
    }

    public void setFjStr(String fjStr) {
        this.fjStr = fjStr;
    }

    public String getBhStr() {
        return bhStr;
    }

    public void setBhStr(String bhStr) {
        this.bhStr = bhStr;
    }
}
