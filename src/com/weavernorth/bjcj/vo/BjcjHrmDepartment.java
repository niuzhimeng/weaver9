package com.weavernorth.bjcj.vo;

import weaver.general.BaseBean;

import java.io.Serializable;

/**
 * 部门
 */
public class BjcjHrmDepartment extends BaseBean implements Serializable {

    /**
     * 部门编码
     */
    private String depCode;
    /**
     * 部门名称
     */
    private String depName;
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
    private String supSubId;
    /**
     * 上级部门id
     */
    private String supDepId;

    private String errMessage;

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
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

    public String getSupSubId() {
        return supSubId;
    }

    public void setSupSubId(String supSubId) {
        this.supSubId = supSubId;
    }

    public String getSupDepId() {
        return supDepId;
    }

    public void setSupDepId(String supDepId) {
        this.supDepId = supDepId;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
