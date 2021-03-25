package com.mytest.juctest;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VolatileTest {
    Lock lock = new ReentrantLock();
    private static int num = 0;

    int i = 0;

    /**
     * volatile 内存一致性demo
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {

        new Thread(() -> {
            try {
                while (num == 0) {
                    System.out.println(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("修改结束====");
        }).start();

        Thread.sleep(200);
        new Thread(() -> {
            num = 1;
            System.out.println("222修改结束====");
        }).start();


        System.out.println("主线程结束");
    }


    @Test
    public void test1() {
        for (int j = 0; j < 20; j++) {

            new Thread(() -> {
                for (int k = 0; k < 10000; k++) {
                    add();
                }

            }).start();
        }

        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
        System.out.println(i);
    }

    private void add() {

        lock.lock();
        i++;

        lock.unlock();
    }


    @Test
    public void test2() {

    }

}

