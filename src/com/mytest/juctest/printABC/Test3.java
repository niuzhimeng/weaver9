package com.mytest.juctest.printABC;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Resource {
    private int num = 1;
    private Lock lock = new ReentrantLock();
    private Condition condition1 = lock.newCondition();
    private Condition condition2 = lock.newCondition();
    private Condition condition3 = lock.newCondition();

    public void print() {
        lock.lock();
        try {
            String currentThreadName = Thread.currentThread().getName();
            if ("a".equalsIgnoreCase(currentThreadName)) {
                while (this.num != 1) {
                    condition1.await();
                }
                this.num = 2;

                System.out.println(currentThreadName);

                condition2.signal();
            } else if ("b".equalsIgnoreCase(currentThreadName)) {
                while (this.num != 2) {
                    condition2.await();
                }
                this.num = 3;
                for (int i = 0; i < 2; i++) {
                    System.out.println(currentThreadName + " " + i);
                }
                condition3.signal();
            } else {
                while (this.num != 3) {
                    condition3.await();
                }
                this.num = 1;
                for (int i = 0; i < 3; i++) {
                    System.out.println(currentThreadName + " " + i);
                }
                System.out.println("=================");
                condition1.signal();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

}

public class Test3 {

    public static void main(String[] args) {
        Resource resource = new Resource();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                resource.print();
            }
        }, "A").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                resource.print();
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                resource.print();
            }
        }, "C").start();

    }
}
