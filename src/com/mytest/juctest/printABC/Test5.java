package com.mytest.juctest.printABC;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Test5 {


    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            executorService.execute(PrintTest::printA);
            executorService.execute(PrintTest::printB);
            executorService.execute(PrintTest::printC);
        }


    }
}

class PrintTest {
    public static int i = 1;
    public static Lock lock = new ReentrantLock();
    static Condition condition1 = lock.newCondition();
    static Condition condition2 = lock.newCondition();
    static Condition condition3 = lock.newCondition();

    public static void printA() {
        lock.lock();
        try {
            while (i != 1) {
                condition1.await();
            }
            System.out.println("A");
            i = 2;
            condition2.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void printB() {
        lock.lock();
        try {
            while (i != 2) {
                condition2.await();
            }
            System.out.println("B");
            i = 3;
            condition3.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void printC() {
        lock.lock();
        try {
            while (i != 3) {
                condition3.await();
            }
            System.out.println("C");
            i = 1;
            condition1.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
