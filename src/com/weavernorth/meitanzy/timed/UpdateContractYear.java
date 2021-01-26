package com.weavernorth.meitanzy.timed;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;

/**
 * 更新合同年份
 */
public class UpdateContractYear extends BaseCronJob {

    private static final Log MY_LOG = LogFactory.getLog(UpdateContractYear.class);

    @Override
    public void execute() {
        MY_LOG.info("更新合同年份定时任务 Start");
        RecordSet recordSet = new RecordSet();
        RecordSet updateSet = new RecordSet();
        try {
            recordSet.executeQuery("select id, nf, qdrq  from uf_Mkzy_htgl");
            while (recordSet.next()) {
                String id = recordSet.getString("id");
                String nf = Util.null2String(recordSet.getString("nf")); // 年份
                String qdrq = recordSet.getString("qdrq"); // 签订日期 - 开始日期
                if (StringUtils.isNotBlank(qdrq)) {
                    String subNf = qdrq.substring(0, 4);
                    if (!nf.equals(subNf)) {
                        // 【年份】与数据库中【签订日期】不一致，进行更新操作
                        updateSet.executeUpdate("update uf_Mkzy_htgl set nf = ? where id = ?", subNf, id);
                    }
                }

            }
            MY_LOG.info("更新合同年份定时任务 End");
        } catch (Exception e) {
            MY_LOG.info("更新合同年份定时任务 Error");
        }
    }

}
