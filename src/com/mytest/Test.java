package com.mytest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.word.Word07Writer;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.fapiao.neon.util.AESUtil;
import com.weavernorth.meitanzy.util.MeiTanConfigInfo;
import com.weavernorth.meitanzy.util.MeiTanZyFtpUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import weaver.general.Util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * @author nzm
 */
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
        Function<Integer, String> function = (a) -> "1";

        Function<Integer, Integer> myFun = (x) -> {
            return x + 1;
        };

        Integer apply = myFun.apply(2);
        System.out.println(apply);
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
    public void test47() {
        try (
                BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\86157\\Desktop\\photo(1).txt"));
                FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\86157\\Desktop\\ceshi.jpg")
        ) {
            String str;
            StringBuilder stringBuilder = new StringBuilder();
            while ((str = reader.readLine()) != null) {
                stringBuilder.append(str);
            }
            String s = stringBuilder.toString();
            byte[] decode = hexStr2bytes(s);
            if (decode != null) {
                fileOutputStream.write(decode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static byte[] hexStr2bytes(String hexStr) {
        if (StringUtils.isBlank(hexStr)) {
            return null;
        }
        if (hexStr.length() % 2 != 0) {//长度为单数
            hexStr = "0" + hexStr;//前面补0
        }
        char[] chars = hexStr.toCharArray();
        int len = chars.length / 2;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            int x = i * 2;
            bytes[i] = (byte) Integer.parseInt(String.valueOf(new char[]{chars[x], chars[x + 1]}), 16);
        }
        return bytes;
    }

    @org.junit.Test
    public void test48() throws Exception {
        String fileUrl = "C:\\Users\\86157\\Desktop\\建模页面密码校验模块.xlsx";
        int i = fileUrl.lastIndexOf(".");
        String suffix = fileUrl.substring(i); // 文件后缀名

        System.out.println(suffix);
    }

    public void read1() {
        byte[] data;
        try (
                FileInputStream fis = new FileInputStream("E:\\WEAVER\\Ecology9.00.2002.06.exe");
                ByteArrayOutputStream baos = new ByteArrayOutputStream(fis.available());
        ) {
            int len;
            byte[] buffer = new byte[2048];
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            data = baos.toByteArray();
            System.out.println("data长度： " + data.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void read2() {
        try (
                BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\86157\\Desktop\\ceshi(1).txt"));
        ) {
            StringBuilder stringBuilder = new StringBuilder();
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                stringBuilder.append(str);
            }
            System.out.println(stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void test49() throws Exception {

//        String encode = URLEncoder.encode("http://101.37.168.17/ccteg", "utf-8");
//        System.out.println(encode);
        String join = String.join(",", "hello", "word");
        System.out.println(join);

    }


    public Key getKey(String key) {
        try {
            // 生成KEY
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128, new SecureRandom(key.getBytes()));
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] keyBytes = secretKey.getEncoded();
            // 转换KEY
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @org.junit.Test
    public void test65() throws Exception {

        String middleKey = "password";  //就是所谓的密钥，加密和解密双方都需要
        String password = "{\"MenuType\":null,\"LoginName\":\"shmk_admin\",\"Password\":\"123456\"}";//需要被加密的内容

        // 生成KEY
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(new SecureRandom(middleKey.getBytes()));
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] byteKey = secretKey.getEncoded();
        Key key = new SecretKeySpec(byteKey, "AES");

        // 加密
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] result = cipher.doFinal(password.getBytes());
        System.out.println("加密后：" + Base64.getEncoder().encodeToString(result));

        String encrypt = AESUtil.encrypt(password, middleKey);
        System.out.println("系统方法： " + encrypt);
    }

    @org.junit.Test
    public void test66() {
        long time1 = System.currentTimeMillis();
        FTPClient ftpClient = MeiTanZyFtpUtil.getFtpClient(MeiTanConfigInfo.FTP_URL.getValue(), Integer.parseInt(MeiTanConfigInfo.FTP_PORT.getValue()),
                MeiTanConfigInfo.FTP_USERNAME.getValue(), MeiTanConfigInfo.FTP_PASSWORD.getValue());
        String name = "市政三公司增资方案.pptx";
        String imagefilenameFtp = new String(name.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        try (
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream("C:\\Users\\86157\\Desktop\\" + name));
        ) {
            boolean upload = MeiTanZyFtpUtil.upload("/home/document/mkzy/202011/576/fzzj1", imagefilenameFtp, inputStream, ftpClient);

        } catch (Exception e) {
            e.printStackTrace();
        }
        long time2 = System.currentTimeMillis();
        System.out.println("耗时： " + (time2 - time1));

    }

    public static String getElementP(String html) {
        if (html == null || "".equals(html)) {
            return null;
        }
        if (!html.contains("<p>") || !html.contains("</p>")) {
            html = "<p>" + html + "</p>";
        }
        Document doc = Jsoup.parse(html);
        Elements p = doc.getElementsByTag("p");
        return p.text();
    }

    @org.junit.Test
    public void test67(){

    }



}
