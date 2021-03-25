package com.mytest.juctest.muke.thread;

public class MuKeTest1 {
    public static void main(String[] args) {
        new Thread(new MyKe()).start();
    }
}

class MyKe implements Runnable {
    @Override
    public void run() {

    }
}
