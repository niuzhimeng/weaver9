package com.engine.nacos.task;

import java.util.Timer;

/**
 * @Description:
 * @Author: wlj
 * @Date: 2021/3/24 11:07
 * @Modified By:
 */
public class NacosTimerSchedule {

    private static Timer logTimer;

    public static void startSave() {
        logTimer = new Timer();
        NacosTask nacosTask = new NacosTask();
        logTimer.schedule(nacosTask, 1 * 1000, 1 * 1000);// 每5秒保存一次
    }

    public static void endLog() {
        logTimer.cancel();
    }
}
