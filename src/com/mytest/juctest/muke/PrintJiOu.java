package com.mytest.juctest.muke;

public class PrintJiOu {

    public static void main(String[] args) throws InterruptedException {
        ResourceTest resourceTest = new ResourceTest();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                resourceTest.print();
            }

        }, "奇数").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                resourceTest.print();
            }

        }, "偶数").start();


    }
}

class ResourceTest {
    private int num = 0;



    public synchronized void print() {
        String currentThreadName = Thread.currentThread().getName();
        if ("奇数".equals(currentThreadName)) {
            while (this.num % 2 == 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            while (this.num % 2 != 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(currentThreadName + ": " + this.num);
        this.num++;
        this.notifyAll();

    }


}