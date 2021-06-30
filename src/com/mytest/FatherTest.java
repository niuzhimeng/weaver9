package com.mytest;

public class FatherTest {

    protected void method1() {
        method2();
    }

    protected void method2() {
        System.out.println("父类的method2");
    }

}
