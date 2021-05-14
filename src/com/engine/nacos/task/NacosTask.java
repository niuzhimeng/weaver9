package com.engine.nacos.task;

import com.engine.nacos.instance.NacosBeat;

import java.util.TimerTask;

/**
 * @Description:
 * @Author: wlj
 * @Date: 2021/3/24 11:06
 * @Modified By:
 */
public class NacosTask extends TimerTask {

    @Override
    public void run() {
        NacosBeat.beat();
    }
}
