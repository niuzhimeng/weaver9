package com.mytest.juctest.printABC;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 两个线程交替打印奇偶数
 */
public class Test4 {
    public static void main(String[] args) throws InterruptedException {

        new Thread(() -> {

        }) {
            @Override
            public void run() {
                super.run();
            }
        };


        // ExecutorService executorService = Executors.newFixedThreadPool(2);
        MyPrint4 myPrint4 = new MyPrint4();

//        executorService.execute(myPrint4::printJi);
//        executorService.execute(myPrint4::printOu);

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                myPrint4.printJi();
            }

        }, "奇数线程：").start();

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                myPrint4.printOu();
            }
        }, "偶").start();


//        while (Thread.activeCount() > 2) {
//            Thread.yield();
//        }
        Thread.sleep(3000);
    }
}

class MyPrint4 {
    private int num = 1;
    Lock lock = new ReentrantLock();
    Condition condition1 = lock.newCondition();
    Condition condition2 = lock.newCondition();

    public void printJi() {
        lock.lock();
        try {
            while (this.num % 2 == 0) {
                condition1.await();
            }
            String currentThreadName = Thread.currentThread().getName();
            System.out.println(currentThreadName + ": " + this.num);
            this.num++;
            condition2.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


    public void printOu() {
        lock.lock();
        try {
            while (this.num % 2 != 0) {
                condition2.await();
            }
            String currentThreadName = Thread.currentThread().getName();
            System.out.println(currentThreadName + ": " + this.num);
            this.num++;
            condition1.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}
