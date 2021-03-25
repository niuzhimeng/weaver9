package com.mytest.juctest.reentrant;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RunnableTest implements Runnable {

    Lock lock = new ReentrantLock();
    Lock lock1 = new ReentrantLock();

    @Override
    public void run() {
        set();
    }

    public void set(){
        try {
            lock.lock();
            System.out.println(Thread.currentThread().getName()+" 执行set===");
            get();
        }finally {
            lock.unlock();
        }
    }

    public void get(){
        try {
            lock1.lock();
            System.out.println(Thread.currentThread().getName()+" 执行get=========");
        }finally {
            lock1.unlock();
        }
    }
}
