package com.mytest.easypoi;

import cn.afterturn.easypoi.entity.ImageEntity;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.word.WordExportUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EasyPoiTest {
    private Logger logger = LoggerFactory.getLogger(EasyPoiTest.class);

    @Test
    public void test2() {
        logger.info("213");
    }

    @Test
    public void test1() {
        try (
                // 文件输出路径
                FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\86157\\Desktop\\test.xls");
        ) {
            TemplateExportParams params = new TemplateExportParams("C:\\Users\\86157\\Desktop\\整合员工信息模板(1).xls", true);
            ImageEntity image = new ImageEntity();
            image.setRowspan(5);//向下合并三行
            //image.setColspan(2);//向右合并两列
            // byte[] imageBytes = getImage("C:\\Users\\86157\\Desktop\\mk7.jpg");
            //image.setData(imageBytes);
            image.setUrl("C:\\Users\\86157\\Desktop\\mk7.jpg");
            String kbStr = "                ";
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 14; i++) {
                stringBuilder.append("入学时间").append(i).append(kbStr)
                        .append("毕业时间").append(i).append(kbStr)
                        .append("哈尔滨师范大学").append(i).append(kbStr)
                        .append(" \r\n");
            }

            Map<String, Object> map = new HashMap<>();
            map.put("name", "牛智萌12");
            map.put("workCode", "1-00002");
            map.put("sex", "男");
            map.put("email", "xxx@qq.com");
            map.put("year", 2020);
            map.put("month", 11);
            map.put("day", 25);
            map.put("info", stringBuilder.toString());
            map.put("photo", image);

            // 集合导出
            List<Map<String, String>> listMap = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                Map<String, String> lm = new HashMap<>();
                lm.put("name", "牛智萌" + i);
                lm.put("school", "哈尔滨师范大学" + i);
                listMap.add(lm);
            }
            map.put("maplist", listMap);

            List<Map<String, String>> listMap1 = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                Map<String, String> lm = new HashMap<>();
                lm.put("number", "学号" + i);
                lm.put("sex", "男" + i);
                listMap1.add(lm);
            }
            map.put("listMap1", listMap1);

            String[] sheetNames = {"员工信息"};
            params.setSheetName(sheetNames);
            Workbook workbook = ExcelExportUtil.exportExcel(params, map);

            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            workbook.write(byteOutputStream);

            fileOutputStream.write(byteOutputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * docx标签替换
     */
    @Test
    public void test3() {
        try (
                // 文件输出路径
                FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\86157\\Desktop\\test.docx");
        ) {
            Map<String, Object> map = new HashMap<>();
            map.put("year", "2021");

            XWPFDocument doc = WordExportUtil.exportWord07("C:\\Users\\86157\\Desktop\\testWord.docx", map);

            doc.write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
