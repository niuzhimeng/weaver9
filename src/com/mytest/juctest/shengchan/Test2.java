package com.mytest.juctest.shengchan;

public class Test2 {
    public static void main(String[] args) {
        ResourceDemo resourceDemo = new ResourceDemo();
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    resourceDemo.sub();
                    try {
                      //  Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }).start();
        }

        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    resourceDemo.add();
                    try {
                      //  Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

   }
}

class ResourceDemo {
    int num = 0;

    public void add() {
        synchronized (this) {
            while (this.num >= 1) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            this.num++;
            try {
                System.out.println(Thread.currentThread().getName() + " 生产了一个库存： " + this.num);
                this.notifyAll();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void sub() {

        synchronized (this) {
            while (this.num <= 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.num--;
            try {
                System.out.println(Thread.currentThread().getName() + " 消费了一个-----当前库存： " + this.num);
                this.notifyAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }



}