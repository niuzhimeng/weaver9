package com.weavernorth.bjcj.vo;

import weaver.general.BaseBean;

import java.io.Serializable;

/**
 * 分部
 */
public class BjcjHrmSubCompany extends BaseBean implements Serializable {

    /**
     * 分部编码
     */
    private String subCode;
    /**
     * 分部名称
     */
    private String subName;
    /**
     * 上级组织编码
     */
    private String supperCode;
    /**
     * 封存标识
     */
    private String status;
    private String statusOa;
    /**
     * 显示顺序
     */
    private String showOrder;
    /**
     * 上级分部id
     */
    private String supperSubId;
    /**
     * 错误信息
     */
    private String errMessage;

    @Override
    public String toString() {
        return "BjcjHrmSubCompany{" +
                "subCode='" + subCode + '\'' +
                ", subName='" + subName + '\'' +
                ", supperCode='" + supperCode + '\'' +
                ", status='" + status + '\'' +
                ", statusOa='" + statusOa + '\'' +
                ", showOrder='" + showOrder + '\'' +
                ", supperSubId='" + supperSubId + '\'' +
                ", errMessage='" + errMessage + '\'' +
                '}';
    }

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getSupperCode() {
        return supperCode;
    }

    public void setSupperCode(String supperCode) {
        this.supperCode = supperCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusOa() {
        return statusOa;
    }

    public void setStatusOa(String statusOa) {
        this.statusOa = statusOa;
    }

    public String getShowOrder() {
        return showOrder;
    }

    public void setShowOrder(String showOrder) {
        this.showOrder = showOrder;
    }

    public String getSupperSubId() {
        return supperSubId;
    }

    public void setSupperSubId(String supperSubId) {
        this.supperSubId = supperSubId;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
