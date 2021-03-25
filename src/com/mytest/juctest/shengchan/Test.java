package com.mytest.juctest.shengchan;

import sun.text.resources.cldr.ii.FormatData_ii;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        MyStore myStore = new MyStore();
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                for (int j = 0; j < 13; j++) {
                    myStore.add();
                    try {
                        //Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                for (int j = 0; j < 13; j++) {
                    myStore.sub();
                    try {
                       // Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }


    }


}

class MyStore {
    private int num = 0;
    Lock lock = new ReentrantLock();
    Condition condition1 = lock.newCondition();
    Condition condition2 = lock.newCondition();

    public void add() {
        lock.lock();
        try {
            while (this.num >= 3) {
                condition1.await();
            }

            this.num++;
            System.out.println(Thread.currentThread().getName() + "生产了一个,当前库存： " + this.num);

            condition2.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public void sub() {
        lock.lock();
        try {
            while (this.num <= 0) {
                condition2.await();
            }
           // Thread.sleep(1000);
            this.num--;
            System.out.println(Thread.currentThread().getName() + ", 消费了一个,库存： " + this.num);

            condition1.signal();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }
}