package com.mytest.juctest.sisuo;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * 死锁demo
 */
public class SiSuoDemo {


    public static void main(String[] args) throws InterruptedException {

        new Thread(A::method, "Thread A").start();

        new Thread(B::method, "Thread B").start();

        Thread.sleep(3000);
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
        for (long id : deadlockedThreads) {
            ThreadInfo threadInfo = threadMXBean.getThreadInfo(id);
            System.out.println("死锁了： "+ threadInfo.getThreadName());
        }
    }
}

class A {

    public synchronized static void method() {
        System.out.println("method from A");
        try {
            Thread.sleep(1000);//为了让另外一个线程有机会拿到锁
            B.method();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class B {

    public synchronized static void method() {
        System.out.println("method from B");
        try {
            Thread.sleep(1000);
            A.method();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

