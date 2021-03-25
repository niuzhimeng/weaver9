package com.mytest.juctest.semaphoreTest;

import java.util.concurrent.Semaphore;

/**
 * Semaphore /ˈseməfɔː(r)/  信号量
 *  acquire /əˈkwaɪə(r)/ 获得
 *  release   /rɪˈliːs/   释放
 */
public class MySemaphoreTest {

    public static void main(String[] args) {

        Demo demo = new Demo();

        for (int i = 0; i < 4; i++) {
            new Thread(() -> {
                demo.execute();
            }).start();
        }
    }

}

class Demo {
    Semaphore semaphore = new Semaphore(2);

    public void execute() {
        String name = Thread.currentThread().getName();
        int i = semaphore.availablePermits();
        try {
            semaphore.acquire();
            System.out.println(name + " 开始====");
            Thread.sleep(3000);
            semaphore.release();
            System.out.println(name + " 结束====");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
