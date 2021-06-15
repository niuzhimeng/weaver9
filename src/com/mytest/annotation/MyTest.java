package com.mytest.annotation;

import com.mytest.annotation.container.MyContainer;
import com.mytest.annotation.proxy.MyProxy;
import com.mytest.annotation.vo.Human;
import com.mytest.annotation.vo.impl.AnnTestVOImpl;
import com.mytest.annotation.vo.impl.Student;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

public class MyTest {


    @Test
    public void test1() {

        Human human = (Human) MyProxy.getProxy(new Student());
        System.out.println(human.getClass().getTypeName());
        human.study();
    }

    @Test
    public void test2() throws Exception {
        Class<Student> studentClass = Student.class;
        Field name1 = studentClass.getDeclaredField("name");
        name1.setAccessible(true);

        Class<?> type = name1.getType();
        System.out.println(type);

        String name = name1.getType().getName();
        System.out.println(name);

        System.out.println(name1.getName());
        name1.set(null, "nzm"); // 给静态变量赋值，第一个参数不用传
        System.out.println("====================");


    }

    @Before
    public void testBefore() {
        new MyContainer();
    }

    @Test
    public void test3() {

        AnnTestVOImpl annTestVO = new AnnTestVOImpl();
        annTestVO.myPrint();

    }

}
