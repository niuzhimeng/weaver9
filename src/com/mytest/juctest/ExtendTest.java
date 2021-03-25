package com.mytest.juctest;

public class ExtendTest {

    public  void test1() {
        System.out.println("1");
    }
}

class Son extends ExtendTest {

    public static void main(String[] args) {
        Son Son = new Son();
        Son.test1();
    }

    @Override
    public void test1() {
        super.test1();
        System.out.println("子类方法");
    }
}


