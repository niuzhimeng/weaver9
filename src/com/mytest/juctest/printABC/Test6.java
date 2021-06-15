package com.mytest.juctest.printABC;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Test6 {

    public static void main(String[] args) {
        MyResource myResource = new MyResource();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                myResource.printA();
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                myResource.printB();
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                myResource.printC();
            }
        }).start();

    }
}


class MyResource {

    public int num = 1;
    Lock lock = new ReentrantLock();
    Condition condition1 = lock.newCondition();
    Condition condition2 = lock.newCondition();
    Condition condition3 = lock.newCondition();

    public void printA() {
        lock.lock();
        try {
            while (num != 1) {
                condition1.await();
            }
            this.num++;
            System.out.println("A");
            condition2.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public void printB() {
        lock.lock();
        try {
            while (num != 2) {
                condition2.await();
            }
            this.num++;
            System.out.println("B");
            condition3.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public void printC() {
        lock.lock();
        try {
            while (num != 3) {
                condition3.await();
            }
            this.num = 1;
            System.out.println("C");
            condition1.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }


}