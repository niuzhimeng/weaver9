package com.mytest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.word.Word07Writer;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String str = "<p>&quot;;&#39;{}[]【】【{}】、||\\》</p>";
        String remarkStr = getRemarkStr(str);
        System.out.println(remarkStr);
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


}
