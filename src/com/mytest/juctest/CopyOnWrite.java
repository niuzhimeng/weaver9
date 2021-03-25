package com.mytest.juctest;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class CopyOnWrite {
    @Test
    public void main1() throws InterruptedException {
        // CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

        ArrayList<String> list = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            int count = -1;
            @Override
            public void run() {
                while (true) {
                    list.add(count++ + "");
                }
            }
        });
        //t.setDaemon(true);
        t.start();
        Thread.sleep(3);
        for (String s : list) {
            System.out.println(s);
        }
    }
}
