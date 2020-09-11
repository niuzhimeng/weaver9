package com.mytest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.interfaces.schedule.BaseCronJob;

/**
 * XXX定时任务
 */
public class MuBanCron extends BaseCronJob {

    private static final Log MY_LOG = LogFactory.getLog(MuBanCron.class);

    @Override
    public void execute() {
        MY_LOG.info("XXX定时任务 Start");
        try {

            MY_LOG.info("XXX定时任务 End");
        }catch (Exception e){
            MY_LOG.info("XXX定时任务 Error");
        }
    }
}
