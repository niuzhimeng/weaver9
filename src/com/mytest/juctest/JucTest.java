package com.mytest.juctest;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class JucTest {

    @Test
    public void test1() {
        CountDownLatch countDownLatch = new CountDownLatch(5);
        CountDownLatchTest countDownLatchTest = new CountDownLatchTest(countDownLatch);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            new Thread(countDownLatchTest).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();

        System.out.println("耗时=================： " + (end - start));
    }
}

class CountDownLatchTest implements Runnable {

    private final CountDownLatch countDownLatch;

    public CountDownLatchTest(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                if (i % 2 == 0) {
                    System.out.println(i);
                }
            }
        } finally {
            countDownLatch.countDown();
        }
    }
}
