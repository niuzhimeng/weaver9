package com.mytest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.poi.word.Word07Writer;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.fapiao.neon.util.Base64Utils;
import com.weavernorth.bjcj.vo.BjcjHrmResource;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import weaver.general.TimeUtil;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Test {

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
        String command = "D:\\openOffice\\program\\soffice.exe -headless -accept=\"socket,host=127.0.0.1,port=8100;urp;\"";
        Process exec = Runtime.getRuntime().exec(command);

        OpenOfficeConnection connection = new SocketOpenOfficeConnection(
                "127.0.0.1", 8100);
        connection.connect();

        // convert
        DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
        converter.convert(new File("C:\\Users\\29529\\Desktop\\2.docx"), new File("C:\\Users\\29529\\Desktop\\123.pdf"));
        connection.disconnect();
        exec.destroy();
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
    public void test18() {
        JSONArray jsonArray = JSONObject.parseArray("");
        List<BjcjHrmResource> list = JSONObject.parseArray("", BjcjHrmResource.class);

        List<BjcjHrmResource> HrmResourceList = jsonArray.toJavaList(BjcjHrmResource.class);

        List bjcjHrmResources = toJavaList(BjcjHrmResource.class);

    }

    public <T> List<T> toJavaList(Class<T> clazz) {
        List<T> list = new ArrayList<>();


        return list;
    }

    @org.junit.Test
    public void test19() {
        String json = "";
        JSONObject jsonObject = JSONObject.parseObject(json);
//        JSONArray logList = jsonObject.getJSONArray("loglist");
//        for (int i = 0; i < logList.size(); i++) {
//            JSONObject obj1 = logList.getJSONObject(i);
//            String displaydepname = obj1.getString("displaydepname");
//            String displayid = obj1.getString("displayid"); // 人员id
//            obj1.put("displaydepname", displaydepname + "-" + "一个岗位");
//        }
        jsonObject.getJSONObject("params").put("titlename", "");
        System.out.println(jsonObject.toJSONString());


    }

    @org.junit.Test
    public void test20() {
//        File file = new File("C:\\Users\\29529\\Desktop\\tt.zip");
//        File unzip = ZipUtil.unzip(file, Charset.forName("gbk"));
//        System.out.println(unzip.toString());
        File zip = ZipUtil.zip("C:\\Users\\29529\\Desktop\\开发部人天统计.xlsx");
        System.out.println(zip.toString());
    }

    @org.junit.Test
    public void test37() throws IOException {

        unZip("C:\\Users\\29529\\Desktop\\t2.zip", "C:\\Users\\29529\\Desktop\\测试3.xlsx");
//        File unzip = ZipUtil.unzip("C:\\Users\\29529\\Desktop\\qq.zip",
//                Charset.forName("gbk"));
//        System.out.println("解压完成： " + unzip);
    }

    private void unZip(String srcFilePath, String destDirPath) throws RuntimeException, IOException {
        File srcFile = new File(srcFilePath);
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
        }
        // 开始解压
        ZipFile zipFile;

        zipFile = new ZipFile(srcFile, Charset.forName("gbk"));
        Enumeration<?> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
            File targetFile = new File(destDirPath);
            // 保证这个文件的父文件夹必须要存在
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            // targetFile.createNewFile();
            // 将压缩文件内容写入到这个文件中
            InputStream is = zipFile.getInputStream(entry);
            FileOutputStream fos = new FileOutputStream(targetFile);
            int len;
            byte[] buf = new byte[4096];
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            // 关流顺序，先打开的后关闭
            fos.close();
            is.close();
        }


        if (zipFile != null) {
            try {
                zipFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @org.junit.Test
    public void test21() throws Exception {

        String userCode = "";
        String timeStamp = TimeUtil.getCurrentTimeString();

        String key = "a1234567";

//        String loginCredence = getMD5(String.format("%s;%s", userCode, timeStamp));
//        String userKey = String.format("%s;%s;%s", userCode, timeStamp, loginCredence);

        String content = String.format("%s;%s;%s", userCode, timeStamp, key);
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] result = DESEncrypt(contentBytes, keyBytes, keyBytes);
        //请求的userKey值
        String requestKey = Base64Utils.encodeToString(result);
        System.out.println("requestKey: " + requestKey);
        String url = "https://192.168.133.170/static/managerTool/index.html?groupCode=EMSETUP#/login?userKey=" + requestKey + "&source=OA&tenant_code=ccbc";


    }

    //加密方法
    private byte[] DESEncrypt(byte[] contentBytes, byte[] keyBytes, byte[] ivBytes) throws Exception {
        DESKeySpec keySpec = new DESKeySpec(keyBytes);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(keySpec);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(contentBytes);
    }

    //MD5
    private String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            throw new RuntimeException("MD5加密出现错误");
        }
    }

    public static String getMD5OfStr(String unencodeStr) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(unencodeStr.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else {
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        }
        return md5StrBuff.toString();
    }
}
