package com.mytest.vo.impl;

public class MyThreadTest implements Runnable {
    @Override
    public void run() {

        try {
            for (int i = 0; i < 5; i++) {
                Thread.sleep(500);
                System.out.println(Thread.currentThread().getName() + "; " + i);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Deprecated()
    public void test1(){
        System.out.println("test1执行");
    }
}
