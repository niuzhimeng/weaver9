package com.weavernorth.meitanzy.util;

import weaver.conn.RecordSet;

public class ConnUtil {

    /**
     * 查询公共选择框的汉字显示
     *
     * @param mainId   公共选择框id
     * @param disorder 选项id
     */
    public static String getGgxzk(String mainId, String disorder) {
        RecordSet recordSet = new RecordSet();
        String returnStr = "";
        recordSet.executeQuery(" SELECT NAME FROM MODE_SELECTITEMPAGEDETAIL WHERE MAINID = '" + mainId + "' and DISORDER = '" + disorder + "'");
        if (recordSet.next()) {
            returnStr = recordSet.getString("NAME");
        }
        return returnStr;
    }

    /**
     * 根据某一字段查另一个字段
     *
     * @param resultField 查询的字段名
     * @param tableName   查询表名
     * @param selField    条件字段名
     */
    public static String getSysByFiled(String resultField, String tableName, String selField) {
        RecordSet recordSet = new RecordSet();
        String returnStr = "";
        recordSet.executeQuery("select " + resultField + " from " + tableName + " where id = '" + selField + "'");
        if (recordSet.next()) {
            returnStr = recordSet.getString(resultField);
        }
        return returnStr;
    }
}
