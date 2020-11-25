package com.mytest;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.RecordSet;
import weaver.formmode.customjavacode.AbstractModeExpandJavaCodeNew;
import weaver.general.Util;
import weaver.hrm.User;
import weaver.soa.workflow.request.Cell;
import weaver.soa.workflow.request.DetailTable;
import weaver.soa.workflow.request.RequestInfo;
import weaver.soa.workflow.request.Row;

import java.util.HashMap;
import java.util.Map;

/**
 * 批量导入后执行的接口
 * 所有数据会先入库，然后每一行数据，执行一遍接口
 * 可从入参对象中获取主表-明细表数据
 */
public class ModeExpandTemplate extends AbstractModeExpandJavaCodeNew {

    private static final Log LOGGER = LogFactory.getLog(ModeExpandTemplate.class);

    /**
     * 执行模块扩展动作
     *
     * @param param param包含(但不限于)以下数据
     *              user 当前用户
     *              importtype 导入方式(仅在批量导入的接口动作会传输) 1 追加，2覆盖,3更新，获取方式(int)param.get("importtype")
     *              导入链接中拼接的特殊参数(仅在批量导入的接口动作会传输)，比如a=1，可通过param.get("a")获取参数值
     *              页面链接拼接的参数，比如b=2,可以通过param.get("b")来获取参数
     * @return
     */
    @Override
    public Map<String, String> doModeExpand(Map<String, Object> param) {
        Map<String, String> result = new HashMap<>();
        try {
            LOGGER.info("导入后执行动作==== " + JSONObject.toJSONString(param));
            User user = (User) param.get("user");
            RequestInfo requestInfo = (RequestInfo) param.get("RequestInfo");
            int billId = Util.getIntValue(requestInfo.getRequestid()); // 主表数据id

            DetailTable detailTable = requestInfo.getDetailTableInfo().getDetailTable(0);
            Row[] rows = detailTable.getRow();

            int tot = 0;
            for (Row row : rows) {
                Cell[] cells = row.getCell();
                for (Cell cell : cells) {
                    if ("sl".equals(cell.getName())) {
                        tot += Integer.parseInt(cell.getValue());
                        break;
                    }
                }
            }

            RecordSet recordSet = new RecordSet();
            recordSet.executeUpdate("update uf_money_check set hj = ? where id = ?", tot, billId);

        } catch (Exception e) {
            result.put("errmsg", "自定义出错信息");
            result.put("flag", "false");
        }
        return result;
    }

}