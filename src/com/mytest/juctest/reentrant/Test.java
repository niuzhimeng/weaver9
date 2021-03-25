package com.mytest.juctest.reentrant;

public class Test {
    /**
     * 可重入锁 demo
     * <p>
     * 可重入锁：同一线程外层函数获得锁后，内层函数仍能获取该锁代码。
     * 在同一线程外层方法获取锁的时候，在进入内层方法会自动获取锁；
     * <p>
     * 线程可以进入任何一个它已经拥有的锁 所同步着的代码块，最大好处是：避免死锁
     */
    @org.junit.Test
    public void test1() throws InterruptedException {
//        RunnableTest runnableTest = new RunnableTest();
//        new Thread(runnableTest, "t1").start();
//        new Thread(runnableTest, "t2").start();

//        while (Thread.activeCount() > 2) {
//
//        }
//        new Thread(Demo1::test, "bbbb").start();
//        Thread.sleep(1000);
        for (int i = 0; i < 2; i++) {
            Thread thread = new Thread(() -> {
                Widget widget = new LoggingWidget();
                widget.doSomething();
            });
            thread.start();
        }

    }

    public class Widget {
        public synchronized void doSomething() {
            System.out.println(this.toString() + "---------------------");
        }
    }

    public class LoggingWidget extends Widget {
        public synchronized void doSomething() {
            System.out.println(this.toString() + ": calling doSomething");
            super.doSomething();
        }
    }





}


