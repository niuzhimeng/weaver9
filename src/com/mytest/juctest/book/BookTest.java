package com.mytest.juctest.book;

public class BookTest {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    System.out.println("running====");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        thread.start();
        Thread.sleep(1000);
        thread.interrupt();
        thread.isInterrupted();
        Thread.interrupted();



    }
}

class Demo1 implements Runnable {

    @Override
    public void run() {

    }
}