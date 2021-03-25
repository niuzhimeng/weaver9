package com.mytest.juctest;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Test2 {


    @Test
    public void test1() {
        PrintTest printTest = new PrintTest();

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                printTest.print_A();
            }).start();
        }


        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                printTest.print_B();
            }).start();
        }


        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                printTest.print_C();
            }).start();
        }

    }
}

class PrintTest {

    int num = 1;
    private final Lock lock = new ReentrantLock();
    Condition condition1 = lock.newCondition();
    Condition condition2 = lock.newCondition();
    Condition condition3 = lock.newCondition();

    public void print_A() {
        lock.lock();
        try {
            // 判断
            while (num != 1) {
                condition1.await();
            }

            // 打印
            System.out.println("A");

            // 唤醒
            num = 2;
            condition2.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void print_B() {
        lock.lock();
        try {
            while (num != 2) {
                condition2.await();
            }
            for (int i = 0; i < 2; i++) {
                System.out.println("B");
            }

            num = 3;
            condition3.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void print_C() {
        lock.lock();
        try {
            while (num != 3) {
                condition3.await();
            }
            for (int i = 0; i < 3; i++) {
                System.out.println("C");
            }
            System.out.println("================");

            num = 1;
            condition1.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


}


