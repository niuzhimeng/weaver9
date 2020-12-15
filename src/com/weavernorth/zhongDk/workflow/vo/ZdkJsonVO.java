package com.weavernorth.zhongDk.workflow.vo;

import weaver.general.BaseBean;
import weaver.general.Util;

public class ZdkJsonVO extends BaseBean {

    private String oaColName; // OA字段名
    private String ifSplit; // 是否需要分割
    private String splitFlag; // 分隔符
    private String takePart; // 取值部分

    /**
     * 根据配置表，分隔字段
     *
     * @param colValue 待处理字段值
     * @return 分割后字段值
     */
    public String handle(String colValue) {
        try {
            if (!"0".equals(this.ifSplit)) {
                return colValue;
            }
            String[] split = colValue.split(Util.null2String(this.splitFlag).trim());
            return split[Integer.parseInt(this.takePart)];
        } catch (Exception e) {
            this.writeLog("当前分隔字段信息类：" + this.toString());
            this.writeLog("字段分隔符处理异常： " + e);
            return colValue;
        }

    }

    @Override
    public String toString() {
        return "ZdkJsonVO{" +
                "oaColName='" + oaColName + '\'' +
                ", ifSplit='" + ifSplit + '\'' +
                ", splitFlag='" + splitFlag + '\'' +
                ", takePart='" + takePart + '\'' +
                '}';
    }

    public String getOaColName() {
        return oaColName;
    }

    public void setOaColName(String oaColName) {
        this.oaColName = oaColName;
    }

    public String getIfSplit() {
        return ifSplit;
    }

    public void setIfSplit(String ifSplit) {
        this.ifSplit = ifSplit;
    }

    public String getSplitFlag() {
        return splitFlag;
    }

    public void setSplitFlag(String splitFlag) {
        this.splitFlag = splitFlag;
    }

    public String getTakePart() {
        return takePart;
    }

    public void setTakePart(String takePart) {
        this.takePart = takePart;
    }
}
