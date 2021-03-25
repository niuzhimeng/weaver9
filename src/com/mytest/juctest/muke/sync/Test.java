package com.mytest.juctest.muke.sync;

public class Test {

    public static void main(String[] args) {
        demo1 demo1 = new demo1();
        new Thread(demo1).start();
        new Thread(demo1).start();

        while (Thread.activeCount() > 2) {

        }
    }
}

class demo1 implements Runnable {

    final Object obj1 = new Object();
    final Object obj2 = new Object();

    @Override
    public void run() {
        synchronized (obj1) {
            try {
                System.out.println(Thread.currentThread().getName() + " 开始=====obj1");
//                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " 结束=====obj1");
        }

        synchronized (obj2) {
            try {
                System.out.println(Thread.currentThread().getName() + " 开始=====222");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " 结束=====222");
        }
    }
}
