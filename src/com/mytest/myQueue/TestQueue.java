package com.mytest.myQueue;

import org.junit.Test;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

public class TestQueue {

    @Test
    public void test0(){
        ThreadLocal<String> threadLocal = ThreadLocal.withInitial(() -> "aaa");
    }

    @Test
    public void test1() throws InterruptedException {
        LinkedBlockingQueue<File> fileQueue = new LinkedBlockingQueue<>(512);
        File fileRoot = new File("E:\\WEAVER\\ecology\\classbean\\com\\weavernorth\\bjcj");

        fileQueue.put(fileRoot);
        while (fileQueue.size() > 0) {
            File currFile = fileQueue.take();
            if (currFile.isDirectory()) {
                File[] files = currFile.listFiles();
                if (files == null) break;
                for (File file : files) {
                    if (file.isDirectory()) {
                        fileQueue.put(file);
                    } else {
                        System.out.println(file.getAbsolutePath() + ": " + file.getName());
                    }
                }
            } else {
                System.out.println(currFile.getAbsolutePath() + ": " + currFile.getName());
            }
        }

    }

}
