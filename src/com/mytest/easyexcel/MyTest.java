package com.mytest.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.merge.LoopMergeStrategy;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.mytest.easyexcel.vo.ClsModal;
import com.mytest.easyexcel.vo.ModeTest;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTest {

    @Test
    public void test1() {
        //实现excel写的操作
        //1 设置写入文件夹地址和excel文件名称
        String filename = "C:\\Users\\86157\\Desktop\\write.xlsx";
        // 2 调用easyexcel里面的方法实现写操作

        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景设置为红色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 20);
        headWriteCellStyle.setWriteFont(headWriteFont);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        // 背景绿色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short) 20);
        contentWriteCellStyle.setWriteFont(contentWriteFont);

        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

        // write方法两个参数：第一个参数文件路径名称，第二个参数实体类class
        EasyExcel.write(filename, ClsModal.class)
                .registerWriteHandler(horizontalCellStyleStrategy)
                .sheet("学生列表").doWrite(getData());

//        ExcelWriter excelWriter = EasyExcel.write(filename, ClsModal.class).build();
//        WriteSheet writeSheet = EasyExcel.writerSheet("模板").build();
//        excelWriter.write(getData(), writeSheet);

        //excelWriter.finish();
    }

    private static List<ClsModal> getData() {
        File file = null;
        try {
            file = new File("C:\\Users\\86157\\Desktop\\mk7_2.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ClsModal> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ClsModal data = new ClsModal();
            data.setSno(i);
            data.setSname("lucy" + i);
            data.setPhotoFile(file);
            list.add(data);
        }
        return list;
    }

    @Test
    public void test2() throws Exception {
        File file = new File("C:\\Users\\86157\\Desktop\\mk7.jpg");
        List<Map<String, String>> dateList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            HashMap<String, String> objectObjectHashMap = new HashMap<>();
            objectObjectHashMap.put("date", "2020-01-01");
            objectObjectHashMap.put("date1", "2020-01-02");
            objectObjectHashMap.put("school", "哈尔滨师范大学");
            dateList.add(objectObjectHashMap);
        }

//        Map<String, Object> map = new HashMap<>();
//        map.put("workCode", 1001);
//        map.put("name", "牛智萌");
//        map.put("photo", file);

        FileInputStream fileInputStream = new FileInputStream(file);
        int available = fileInputStream.available();
        byte[] bytes = new byte[available];
        fileInputStream.read(bytes);
        fileInputStream.close();

        ModeTest modeTest = new ModeTest();
        modeTest.setWorkCode("100001");
        modeTest.setName("牛智萌");
        modeTest.setPhoto(bytes);

        String templateFileName = "C:\\Users\\86157\\Desktop\\muban.xlsx";
        String fileName = "C:\\Users\\86157\\Desktop\\modeTest.xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        //EasyExcel.write(fileName, ModeTest.class).withTemplate(templateFileName).sheet().doWrite(tests1);
        //EasyExcel.write(fileName).withTemplate(templateFileName).sheet().doFill(map);
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景设置为红色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 20);
        headWriteCellStyle.setWriteFont(headWriteFont);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        // 背景绿色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short) 20);
        contentWriteCellStyle.setWriteFont(contentWriteFont);

        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

        LoopMergeStrategy loopMergeStrategy = new LoopMergeStrategy(5, 1, 2);
        ExcelWriter excelWriter = EasyExcel.write(fileName).withTemplate(templateFileName)
                .registerWriteHandler(horizontalCellStyleStrategy)
//                .registerWriteHandler(new ExcelFillImageStrategy())
                .build();
        Workbook workbook = excelWriter.writeContext().writeWorkbookHolder().getWorkbook();
        workbook.setForceFormulaRecalculation(true);

        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        excelWriter.fill(modeTest, writeSheet);
        // excelWriter.fill(new FillWrapper(dateList), writeSheet);
        // 千万别忘记关闭流
        excelWriter.finish();

    }
}
