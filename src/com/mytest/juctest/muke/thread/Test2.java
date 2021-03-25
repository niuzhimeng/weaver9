package com.mytest.juctest.muke.thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Test2 {

    public static void main(String[] args) throws InterruptedException {
        MuKe2 muKe2 = new MuKe2();
        MuKe2_1 muKe2_1 = new MuKe2_1();

        Thread thread1 = new Thread(() -> {
            muKe2_1.test1();
        });
        thread1.start();
        Thread.sleep(200);

        System.out.println("thread1: "+ thread1.getState());

        Thread thread = new Thread(() -> {
            muKe2_1.test1();
        });

        thread.start();
        Thread.sleep(200);

        System.out.println(thread.getState());

        Thread.State blocked = Thread.State.BLOCKED;

    }
}

class MuKe2 {
    public synchronized void test1() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class MuKe2_1 {

    Lock lock = new ReentrantLock();

    public void test1() {
        lock.lock();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}