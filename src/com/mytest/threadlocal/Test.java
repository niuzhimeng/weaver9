package com.mytest.threadlocal;

import com.mytest.annotation.vo.impl.Student;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;

public class Test {

    private final ThreadLocal<String> threadLocal = ThreadLocal.withInitial(() -> "空串");
    private static ThreadLocal<Student> studentLocal = ThreadLocal.withInitial(Student::new);

    public static ThreadLocal<SimpleDateFormat> simpleDateFormatThreadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    @org.junit.Test
    public void test1() {

        for (int i = 0; i < 3; i++) {
            int finalI = i;
            new Thread(() -> {
                Thread.currentThread().setName("线程" + finalI);
                threadLocal.set("线程 " + finalI + "的变量");
                methAll();
            }).start();
        }


    }


    public void methAll() {
        meth1();
        meth2();
        meth3();
    }

    private void meth1() {
        System.out.println("meth1: " + Thread.currentThread().getName() + " " + threadLocal.get());
    }

    private void meth2() {
        System.out.println("meth2: " + Thread.currentThread().getName() + " " + threadLocal.get());
    }

    private void meth3() {
        System.out.println("meth3: " + Thread.currentThread().getName() + " " + threadLocal.get());
        threadLocal.remove();
    }

    @org.junit.Test
    public void test2() throws Exception {
        String encode = URLEncoder.encode("/newstype0/category", "utf-8");
        System.out.println(encode);
    }


}
