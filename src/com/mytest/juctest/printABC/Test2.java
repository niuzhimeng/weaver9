package com.mytest.juctest.printABC;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Test2 {

    public static void main(String[] args) {

    }
}


class MyPrint {
    int num = 0;
    private Lock lock = new ReentrantLock();
    Condition condition1 = lock.newCondition();
    Condition condition2 = lock.newCondition();
    Condition condition3 = lock.newCondition();

    public void execute() {

        lock.lock();
       try {

       } catch (Exception e) {
           e.printStackTrace();
       } finally {
           lock.unlock();
       }


    }
}