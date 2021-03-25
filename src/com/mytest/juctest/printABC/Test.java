package com.mytest.juctest.printABC;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Test {
    public static void main(String[] args) {

        print print = new print();

        for (int i = 0; i < 3; i++) {
            new Thread(print::printA).start();
        }

        for (int i = 0; i < 3; i++) {
            new Thread(print::printB).start();
        }

        for (int i = 0; i < 3; i++) {
            new Thread(print::printC).start();
        }

    }

}


class print {
    private int num = 1;
    Lock lock = new ReentrantLock();
    Condition condition1 = lock.newCondition();
    Condition condition2 = lock.newCondition();
    Condition condition3 = lock.newCondition();

    public void myPrint(int target, Condition c1, Condition c2, String str) {
        lock.lock();
        try {
            while (num != target) {
                c1.await();
            }
            for (int i = 0; i < this.num; i++) {
                System.out.println(str);
            }

            this.num = add(num);
            c2.signal();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printA() {
        myPrint(1, condition1, condition2, "A");
    }

    public void printB() {
        myPrint(2, condition2, condition3, "B");
    }

    public void printC() {
        myPrint(3, condition3, condition1, "C");
    }

    public int add(int num) {
        num++;
        if (num > 3) {
            return 1;
        } else {
            return num;
        }
    }

}