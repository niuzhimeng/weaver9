package com.mytest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.word.Word07Writer;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weavernorth.meitanzy.util.MeiTanConfigInfo;
import com.weavernorth.meitanzy.util.MeiTanZyFtpUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import weaver.general.TimeUtil;
import weaver.general.Util;

import java.awt.*;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoUnit.DAYS;

public class Test {


    private static final Pattern pattern = Pattern.compile("<[a-zA-Z]+.*?>([\\s\\S]*?)</[a-zA-Z]*>");
    private static final Pattern pattern1 = Pattern.compile(">(.*?)</");

    @org.junit.Test
    public void test1() {
        List<String> list = new CopyOnWriteArrayList<>();
        list.add("牛智萌");
        list.forEach(a -> System.out.println(a));

    }

    @org.junit.Test
    public void test2() throws Exception {
        String encode = URLEncoder.encode("牛智萌", "utf8");
        System.out.println(encode);
        String decode = URLDecoder.decode(encode, "utf-8");
        System.out.println(decode);
    }

    @org.junit.Test
    public void test3() {
        Map<String, Integer> map = new TreeMap<>();
        for (int i = 0; i < 10; i++) {
            map.put(i + "", 23);
        }
        map = Collections.unmodifiableMap(map);

        map.put("100", 100);
        map.forEach((k, v) -> System.out.println(k + ", " + v));

    }

    @org.junit.Test
    public void test4() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = "2019-04-19";

        LocalDate parse = LocalDate.parse(date, formatter);
        LocalDate localDateAfter = parse.plusMonths(3);
        System.out.println("增加后日期： " + localDateAfter.toString());

        LocalDate nowDate = LocalDate.now();
        boolean equals = nowDate.equals(localDateAfter);
        System.out.println(equals);
    }

    @org.junit.Test
    public void test5() {
        try (OutputStream out = new FileOutputStream("C:\\Users\\29529\\Desktop\\withoutHead.xlsx")) {
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX, true);
            Sheet sheet1 = new Sheet(1, 0);
            sheet1.setSheetName("sheet1");
            List<List<String>> data = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                List<String> item = new ArrayList<>();
                item.add("item0" + i);
                item.add("item1" + i);
                item.add("item2" + i);
                data.add(item);
            }
            List<List<String>> head = new ArrayList<List<String>>();
            List<String> headCoulumn1 = new ArrayList<String>();
            List<String> headCoulumn2 = new ArrayList<String>();
            List<String> headCoulumn3 = new ArrayList<String>();
            headCoulumn1.add("第一列");
            headCoulumn2.add("第二列");
            headCoulumn3.add("第三列");
            head.add(headCoulumn1);
            head.add(headCoulumn2);
            head.add(headCoulumn3);
            Table table = new Table(1);
            table.setHead(head);
            writer.write0(data, sheet1, table);
            writer.finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void test14() throws Exception {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet xssfSheet = xssfWorkbook.createSheet("1");
        for (int i = 0; i < 5; i++) {
            XSSFRow row = xssfSheet.createRow(i);
            for (int j = 0; j < 8; j++) {
                XSSFCell cell = row.createCell(j);
                cell.setCellValue("第：" + i + "行， " + j + " 列");
            }
        }
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("C:\\Users\\29529\\Desktop\\1.xlsx"));
        xssfWorkbook.write(bufferedOutputStream);
        bufferedOutputStream.close();
    }

    /**
     * word 转pdf
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test15() {
        OfficeManager officeManager = null;
        try {
            officeManager = startService();
            OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
            converter.convert(new File("C:\\Users\\29529\\Desktop\\1.docx"), new File("C:\\Users\\29529\\Desktop\\123.pdf"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (officeManager != null) {
                officeManager.stop();
            }
        }

    }

    private OfficeManager startService() {
        DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
        OfficeManager officeManager = null;
        try {
            configuration.setOfficeHome("D:\\openOffice");//设置OpenOffice.org安装目录
            configuration.setPortNumbers(8100); //设置转换端口，默认为8100
            configuration.setTaskExecutionTimeout(1000 * 60 * 5L);//设置任务执行超时为5分钟
            configuration.setTaskQueueTimeout(1000 * 60 * 60 * 24L);//设置任务队列超时为24小时

            officeManager = configuration.buildOfficeManager();
            officeManager.start();// 启动服务
        } catch (Exception ce) {
            ce.printStackTrace();
        }
        return officeManager;
    }


    @org.junit.Test
    public void test16() throws Exception {
//        String command = "D:\\openOffice\\program\\soffice.exe -headless -accept=\"socket,host=127.0.0.1,port=8100;urp;\"";
//        Process exec = Runtime.getRuntime().exec(command);
//
//        OpenOfficeConnection connection = new SocketOpenOfficeConnection(
//                "127.0.0.1", 8100);
//        connection.connect();
//
//        // convert
//        DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
//        converter.convert(new File("C:\\Users\\29529\\Desktop\\2.docx"), new File("C:\\Users\\29529\\Desktop\\123.pdf"));
//        connection.disconnect();
//        exec.destroy();
    }


    @org.junit.Test
    public void test17() throws Exception {
        Word07Writer writer = new Word07Writer();

// 添加段落（标题）
        writer.addText(new Font("方正小标宋简体", Font.PLAIN, 22), "我是第一部分", "我是第二部分");
// 添加段落（正文）
        writer.addText(new Font("宋体", Font.PLAIN, 22), "我是正文第一部分", "我是正文第二部分");
// 写出到文件
        writer.flush(FileUtil.file("C:\\Users\\29529\\Desktop\\wordWrite.docx"));
// 关闭
        writer.close();


    }

    @org.junit.Test
    public void test44() throws Exception {
        String str = "<p &quot;;&#39;{}[]【】【{}】、||>ok</p>";
        String s = Util.delHtml(str);
        System.out.println(s);
//        String remarkStr = getRemarkStr(str);
//        System.out.println(remarkStr);
    }


    public static String getRemarkStr(String beforeStr) {
        String returnStr = "";
        if (beforeStr.startsWith("<p")) {
            StringBuilder stringBuilder = new StringBuilder();
            String[] split = beforeStr.split("<p");
            for (String s : split) {
                if ("".equals(s)) {
                    continue;
                }
                if (s.contains("<span")) {
                    Matcher matcher = pattern.matcher(s);
                    if (matcher.find()) {
                        stringBuilder.append(matcher.group(1).trim());
                    }
                } else {
                    Matcher matcher = pattern1.matcher(s);
                    if (matcher.find()) {
                        stringBuilder.append(matcher.group(1).trim());
                    }
                }
            }
            returnStr = stringBuilder.toString();
        } else {
            returnStr = beforeStr;
        }

        return returnStr.replace("&nbsp;", " ")
                .replaceAll("(?i)(<br/>)", " ")
                .replaceAll("\\s+", " ")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
                .replace("&#39;", "'");
    }

    @org.junit.Test
    public void myInit() {
        LocalDate parse = LocalDate.parse("2020-09-28");
        LocalDate beforeParse = LocalDate.parse("2020-09-26");

        long daysBetween = DAYS.between(beforeParse, parse);
        System.out.println(daysBetween);
    }


    public void myClose() {
        LinkedBlockingQueue<String> strings = new LinkedBlockingQueue<>();
        System.out.println("close执行");
    }

    @org.junit.Test
    public void test45() {
        // LocalDate localDate = LocalDate.now();
        LocalDate localDate = LocalDate.of(2020, 1, 24);
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        System.out.println(year);
        System.out.println(month);

        LocalDate beforeDate = localDate.minus(1, ChronoUnit.MONTHS);
        int beforeYear = beforeDate.getYear();
        int beforeMonth = beforeDate.getMonthValue();
        System.out.println(beforeYear);
        System.out.println(beforeMonth);
    }

    @org.junit.Test
    public void test46() {
        // 法定节假日与调休配置表 KQ_HolidaySet
        //LocalDate localDate = LocalDate.now();
        //LocalDate localDate = LocalDate.of(2020, 9, 4);
        LocalDate localDate = LocalDate.parse("2020-09-04");
        DayOfWeek week = localDate.getDayOfWeek();
        if (week == DayOfWeek.SATURDAY || week == DayOfWeek.SUNDAY) {
            System.out.println(localDate.toString() + " ->周末");
        } else {
            System.out.println(localDate.toString() + " ->工作日");
        }
    }

    @org.junit.Test
    public void test47() throws FileNotFoundException {

        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("contractUniqueId", "合同唯一标识");
        jsonObject.put("contractType", "合同类型");
        jsonObject.put("contractSubject", "合同标的");
        jsonObject.put("contractName", "合同名称");
        jsonObject.put("contractSelfCode", "合同自编号");

        jsonObject.put("buyMethod", "采购方式");
        jsonObject.put("bidFile", "中标通知书");
        jsonObject.put("contractAmount", "合同金额/暂估金额");
        jsonObject.put("valuationMode", "计价方式");
        jsonObject.put("currencyName", "币种");

        jsonObject.put("exchangeRate", "汇率");
        jsonObject.put("amountExplain", "合同金额说明");
        jsonObject.put("paymentDirection", "收支方向");
        jsonObject.put("paymentType", "合同收/付款类型");
        jsonObject.put("paymentMethod", "合同收/付款方式");

        jsonObject.put("signingSubject", "我方签约主体");
        jsonObject.put("signingSubjectCode", "签约主体编码");
        jsonObject.put("creatorAccount", "经办人账号");
        jsonObject.put("creatorName", "经办人名称");
        jsonObject.put("creatorDeptCode", "经办部门编码");

        jsonObject.put("creatorDeptName", "经办部门");
        jsonObject.put("performAddress", "合同履行地");
        jsonObject.put("signAddress", "合同签署地");
        jsonObject.put("contractPeriod", "合同期限类型");
        jsonObject.put("performPeriod", "合同履行期限");

        jsonObject.put("periodExplain", "期限说明");
        jsonObject.put("ourIsAuth", "是否授权（我方）");
        jsonObject.put("authType", "授权类型");

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

    @org.junit.Test
    public void test48() throws Exception {
        String s = TimeUtil.getCurrentTimeString().replace("-", "")
                .replace(":", "").replaceAll("\\s*", "");
        System.out.println(s);
    }


    @org.junit.Test
    public void test49() throws IOException {
        try {
            File file = new File("d:\\20201027_合同管理_1200.docx");
            FileInputStream fileInputStream = new FileInputStream(file);

            String name = file.getName();
            name = new String(name.getBytes("GBK"), StandardCharsets.ISO_8859_1);
            String currentDate = "202010";
            String htbm = "006";
            String fieldName = "filedname";
            String savePath = "/home/document/" + MeiTanConfigInfo.DWBM.getValue() + "/" + currentDate + "/" + htbm + "/" + fieldName + "/";

            // 获取ftp对象
//            FTPClient ftpClient = MeiTanZyFtpUtil.getFtpClient(MeiTanConfigInfo.FTP_URL.getValue(), Integer.parseInt(MeiTanConfigInfo.FTP_PORT.getValue()),
//                    MeiTanConfigInfo.FTP_USERNAME.getValue(), MeiTanConfigInfo.FTP_PASSWORD.getValue());
            FTPClient ftpClient = MeiTanZyFtpUtil.getFtpClient(MeiTanConfigInfo.FTP_URL.getValue(), Integer.parseInt(MeiTanConfigInfo.FTP_PORT.getValue()),
                    "cqyjyftp", "cqyjyftp");

            // 上传文件
            boolean upload = MeiTanZyFtpUtil.upload(savePath, name, fileInputStream, ftpClient);
            System.out.println("上传： " + upload);

            // 关闭ftp连接
            MeiTanZyFtpUtil.disconnect(ftpClient);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
