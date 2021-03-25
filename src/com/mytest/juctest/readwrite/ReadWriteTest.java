package com.mytest.juctest.readwrite;

import com.alibaba.fastjson.JSONObject;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteTest {


    public static void main(String[] args) throws InterruptedException {
//        ExecutorService executorService = Executors.newFixedThreadPool(5);
//
//        ReadWriteResource readWriteResource = new ReadWriteResource();
//
//        executorService.execute(readWriteResource::read);
//        Thread.sleep(200);
//
//        executorService.execute(readWriteResource::write);
//        executorService.execute(readWriteResource::read);
//        executorService.execute(readWriteResource::read);
//
//        executorService.shutdown();

        int[] nums = {2, 5, 8, 3, 11, 656, 34, 7, 3, 7, 8, 3};

        int length = nums.length - 1;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length - i; j++) {
                if (nums[j] > nums[j + 1]) {
                    int temp;
                    temp = nums[j];
                    nums[j] = nums[j + 1];
                    nums[j + 1] = temp;
                }
            }

        }

        System.out.println(JSONObject.toJSONString(nums));

    }
}

class ReadWriteResource {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public void read() {
        lock.readLock().lock();
        try {
            String currentThreadName = Thread.currentThread().getName();
            System.out.println(currentThreadName + " 读锁开始============");
            Thread.sleep(1000);
            System.out.println(currentThreadName + " 读锁结束============");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
    }


    public void write() {
        lock.writeLock().lock();
        try {
            String currentThreadName = Thread.currentThread().getName();
            System.out.println(currentThreadName + " 写锁开始");
            Thread.sleep(1000);
            System.out.println(currentThreadName + " 写锁结束");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
