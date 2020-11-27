package com.weavernorth.meitanzy.timed;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;

import java.time.LocalDate;

/**
 * 更新合同执行状态定时任务
 */
public class UpdateContractState extends BaseCronJob {

    private static final Log MY_LOG = LogFactory.getLog(UpdateContractState.class);

    @Override
    public void execute() {
        MY_LOG.info("更新合同执行状态定时任务 Start");
        RecordSet recordSet = new RecordSet();
        RecordSet updateSet = new RecordSet();
        LocalDate now = LocalDate.now();
        try {
            recordSet.executeQuery("select id, qdrq, htlxqx, zt from uf_Mkzy_htgl where qdrq is not null and htlxqx is not null");
            while (recordSet.next()) {
                String id = recordSet.getString("id");
                String zt = Util.null2String(recordSet.getString("zt")); // 合同状态- 未执行,执行中,已完成
                String qdrq = recordSet.getString("qdrq"); // 签订日期 - 开始日期
                String htlxqx = recordSet.getString("htlxqx"); // 合同履行期限 - 结束日期

                String state = getState(qdrq, htlxqx, now);
                if (!zt.equals(state)) {
                    // 与数据库中状态不一致，进行更新操作
                    updateSet.executeUpdate("update uf_Mkzy_htgl set zt = ? where id = ?", state, id);
                }
            }
            MY_LOG.info("更新合同执行状态定时任务 End");
        } catch (Exception e) {
            MY_LOG.info("更新合同执行状态定时任务 Error");
        }
    }

    /**
     * 根据开始、结束日期获取合同状态
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param now       当前日期
     */
    private String getState(String startDate, String endDate, LocalDate now) {
        String returnState = "";
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            return returnState;
        }
        try {
            LocalDate startObj = LocalDate.parse(startDate);
            LocalDate endObj = LocalDate.parse(endDate);
            if (now.isBefore(startObj)) {
                // 未执行
                returnState = "0";
            } else if (now.isAfter(endObj)) {
                // 已完成
                returnState = "2";
            } else {
                // 执行中
                returnState = "1";
            }
        } catch (Exception e) {
            MY_LOG.error("日期格式化异常： " + e);
        }

        return returnState;
    }

}
