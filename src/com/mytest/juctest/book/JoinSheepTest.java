package com.mytest.juctest.book;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class JoinSheepTest {
    public static void main(String[] args) throws InterruptedException {
        MyResource myResource = new MyResource();
        Thread thread = new Thread(() -> {
            myResource.test1();
        });

        System.out.println(thread.getState());
    }
}

class MyResource {

    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    public void test1() {
        lock.lock();
        try {
            System.out.println("statr----");
            //  condition.await(2, TimeUnit.SECONDS);

            System.out.println("end------");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
