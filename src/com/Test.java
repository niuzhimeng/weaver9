package com;

import com.weavernorth.siwei.replace.ReplaceWord;

import java.io.File;

public class Test {

    @org.junit.Test
    public void test1() {
        ReplaceWord replaceWord = new ReplaceWord();
        // 解压输出


    }

    @org.junit.Test
    public void test2() {
        String filePath = "ee:\\WEAVER\\ecology\\filesystem\\201906\\K\\0ac2ad8f-a7b1-450d-8a5c-42ec6f7fbbba.zip";
        System.out.println( filePath.substring(0, filePath.lastIndexOf(File.separator) + 1) + "test.zip");

    }
}
