package com.weavernorth.learning.io;

import com.caucho.xpath.expr.Var;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class IoTest {

    @Test
    public void test1() {
        String path = "E:\\WEAVER\\ecology\\classbean\\com\\weavernorth";
        String pathSeparator = File.pathSeparator; // window 分号 Linux 冒号
        String separator = File.separator; // windows 反斜杠  linux 正斜杠

//        System.out.println(pathSeparator);
//        System.out.println(separator);
//
//        File file = new File("E:/WEAVER/ecology/classbean");
//        String a = file+"1";
//        System.out.println(a);

        File file = new File("C:\\Users\\86157\\Desktop\\services.xml");
        System.out.println(file.length());


    }

    @Test
    public void test2() throws IOException {
        try (
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream("C:\\Users\\86157\\Desktop\\a.txt"));
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("C:\\Users\\86157\\Desktop\\b.txt"));
        ) {
            int len;
            byte[] bytes = new byte[1024];
            while ((len = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test3() {
        try (
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("C:\\Users\\86157\\Desktop\\c.txt", true));
        ) {
            for (int i = 0; i < 5; i++) {
                bufferedWriter.write(i + "、 你好啊，世界");
                bufferedWriter.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test4() {
        try (
                BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\86157\\Desktop\\a.txt"));
        ) {

            String str = "";
            while ((str = bufferedReader.readLine()) != null) {
                System.out.println(new String(str.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test5() {
        // 转换流 解决乱码
        try (
                // new BufferedReader()
                InputStreamReader inputStreamReader = new InputStreamReader(
                        new FileInputStream("C:\\Users\\86157\\Desktop\\a.txt"), "gbk");

        ) {
            char[] chr = new char[1024];
            int len;
            while ((len = inputStreamReader.read(chr)) != -1) {
                System.out.println(new String(chr, 0, len));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test6() {
        // 字节流 解决乱码
        try (
                FileInputStream fileInputStream = new FileInputStream("C:\\Users\\86157\\Desktop\\a.txt");
        ) {
            byte[] bytes = new byte[1024];
            int len;
            while ((len = fileInputStream.read(bytes)) != -1) {
                System.out.println(new String(bytes, 0, len, "gbk"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test7() throws Exception {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(""));
        objectOutputStream.writeObject("");
    }

}
