package com.mytest;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.cloudstore.dev.api.util.HttpManager;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import weaver.conn.BatchRecordSet;
import weaver.file.AESCoder;
import weaver.rsa.security.RSA;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Test {
    volatile int i = 0;

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
//        Word07Writer writer = new Word07Writer();
//
//// 添加段落（标题）
//        writer.addText(new Font("方正小标宋简体", Font.PLAIN, 22), "我是第一部分", "我是第二部分");
//// 添加段落（正文）
//        writer.addText(new Font("宋体", Font.PLAIN, 22), "我是正文第一部分", "我是正文第二部分");
//// 写出到文件
//        writer.flush(FileUtil.file("C:\\Users\\29529\\Desktop\\wordWrite.docx"));
//// 关闭
//        writer.close();


    }

    @org.junit.Test
    public void test18() throws UnsupportedEncodingException {
        HttpManager httpManager = new HttpManager();
        Map<String, String> map = new HashMap<>();
        map.put("name", "310771351@qq.com");
        map.put("password", "123456");
        // String s = HTTPUtil.doPost("http://bfpt.yunkeonline.cn/sso/api/login", map);
        String s = httpManager.postData("http://bfpt.yunkeonline.cn/sso/api/login", map);
        System.out.println(s);


    }

    @org.junit.Test
    public void getPropValue() throws Exception {
        String encrptKey = "EAVERECOLOGYDBENCODER";

        String encrypt = AESCoder.encrypt("牛智萌", encrptKey);
        System.out.println("加密后： " + encrypt);

        String sValue = AESCoder.decrypt(encrypt, encrptKey);
        System.out.println("解密后： " + sValue);


    }

    @org.junit.Test
    public void test19() throws Exception {
        URL url = new URL("http://111.160.2.51:8888/gysDoc/test.docx");
        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
        urlCon.setConnectTimeout(6000);
        urlCon.setReadTimeout(6000);
        int code = urlCon.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            throw new Exception("文件读取失败");
        }

        //读文件流
        InputStream input = urlCon.getInputStream();
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\86157\\Desktop\\test.docx");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int byteread;
        byte[] data = new byte[1024];
        while ((byteread = input.read(data)) != -1) {
            out.write(data, 0, byteread);
            out.flush();
        }
        byte[] content = out.toByteArray();
        out.close();
        input.close();

        fileOutputStream.write(content);
        fileOutputStream.close();
    }

    @org.junit.Test
    public void test20() {
        File file = null;
        try {
            file = new File("E:\\WEAVER\\测试的文件你23ABC.pdf");

        } catch (Exception e) {
            System.out.println(e);
        }

        String mt = new MimetypesFileTypeMap().getContentType(file);
        System.out.println(mt);
        System.out.println(file.length() / 1024);

        byte[] bytes = new byte[1];

        StreamingOutput streamingOutput = new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException, WebApplicationException {

            }
        };

        try {
            StreamingOutput streamingoutput1 = outputStream -> outputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void test21() throws Exception {
        String cpk = RSA.getRSA_PUB();
        //System.out.println(cpk);

        System.out.println(Runtime.getRuntime().availableProcessors());

    }

}
