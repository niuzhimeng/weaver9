package com.weavernorth.zhongDk.orgSyn.vo;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;
import weaver.general.BaseBean;

import java.io.Serializable;

/**
 * 分部
 */
public class ZdkSubCompany extends BaseBean implements Serializable {

    @JSONField(name = "B01AN")
    private String orgType; // 组织类型 传的是汉字：是分部，其他是部门

    @JSONField(name = "CODEITEMID")
    private String orgCode; // 组织编码

    @JSONField(name = "B01AM")
    private String supCode; // 上级组织编码

    @JSONField(name = "B0105")
    private String orgFullName; // 组织名称

    @JSONField(name = "CODEITEMDESC")
    private String orgShortName; // 组织简称

    @JSONField(name = "DATASTATUS")
    private String status; // 数据状态 0是停用，1是启用
    private String statusOa;

    @JSONField(name = "UNIQUE_ID")
    private String uniqueId; // 唯一标识码

    private String ErrMessage; // 错误信息
    private String supperCompanyId; // 上级分部id
    private String supperDepartmentId; // 上级部门id

    public void changeStatusOa() {
        if ("0".equals(this.status)) {
            // 停用
            this.statusOa = "1";
        } else {
            this.statusOa = "0";
        }

        if (StringUtils.isBlank(this.orgFullName)) {
            this.orgFullName = this.orgShortName;
        }

    }

    @Override
    public String toString() {
        return "组织类型='" + orgType + '\'' +
                ", 组织编码='" + orgCode + '\'' +
                ", 上级组织编码='" + supCode + '\'' +
                ", 组织名称='" + orgFullName + '\'' +
                ", 组织简称='" + orgShortName + '\'' +
                ", 数据状态='" + status + '\'' +
                ", 数据状态Oa='" + statusOa + '\'' +
                ", 上级分部id='" + supperCompanyId + '\'' +
                ", 上级部门id='" + supperDepartmentId + '\'';
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getSupperDepartmentId() {
        return supperDepartmentId;
    }

    public void setSupperDepartmentId(String supperDepartmentId) {
        this.supperDepartmentId = supperDepartmentId;
    }

    public String getSupperCompanyId() {
        return supperCompanyId;
    }

    public void setSupperCompanyId(String supperCompanyId) {
        this.supperCompanyId = supperCompanyId;
    }

    public String getErrMessage() {
        return ErrMessage;
    }

    public void setErrMessage(String errMessage) {
        ErrMessage = errMessage;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getSupCode() {
        return supCode;
    }

    public void setSupCode(String supCode) {
        this.supCode = supCode;
    }

    public String getOrgFullName() {
        return orgFullName;
    }

    public void setOrgFullName(String orgFullName) {
        this.orgFullName = orgFullName;
    }

    public String getOrgShortName() {
        return orgShortName;
    }

    public void setOrgShortName(String orgShortName) {
        this.orgShortName = orgShortName;
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
}
