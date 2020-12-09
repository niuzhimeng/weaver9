package com.weavernorth.zhongDk.orgSyn.vo;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

/**
 * 人员信息
 */
public class ZdkResource extends BaseBean {
    private static final Log LOGGER = LogFactory.getLog(ZdkResource.class);

    @JSONField(name = "UNIQUE_ID")
    private String uniqueId; // 唯一标识码
    @JSONField(name = "A01BF")
    private String certificatenumType; // 证件类型
    @JSONField(name = "A0177")
    private String certificatenum; // 身份证号码

    @JSONField(name = "A0121")
    private String folk; // 民族

    @JSONField(name = "ID")
    private String workCode;//员工编号
    @JSONField(name = "A0101")
    private String lastName;//姓名
    @JSONField(name = "A01AK")
    private String loginId;//系统登陆帐号
    @JSONField(name = "A0182")
    private String status;//员工状态
    @JSONField(name = "A0107")
    private String sex; // 性别

    @JSONField(name = "DATASTATUS")
    private String dataStatus; // 数据状态 0是停用，1是启用
    @JSONField(name = "E0122")
    private String departmentCode; // 所属部门编码
    @JSONField(name = "A0111")
    private String birthday; // 生日
    @JSONField(name = "A0114")
    private String nativePlace; // 籍贯
    @JSONField(name = "A01AG")
    private String birthplace; // 出生地

    @JSONField(name = "A0440")
    private String degree; // 学位
    @JSONField(name = "A2205")
    private String policy; // 政治面貌
    @JSONField(name = "A0142")
    private String companystartdate; // 公司开始日期
    @JSONField(name = "A0141")
    private String workstartdate; // 工作开始日期


    private String location;//工作地点
    private String email;//电子邮件
    @JSONField(name = "A0148")
    private String phone; //手机
    private String dsporder;//人员排序

    private String depId;//部门id
    private String subId;//分部id
    private String jobtitleId;//岗位id
    private String managerIdReal;//上级id
    private String managerstr;//所有上级

    private String managerCode;//直接上级员工编号
    private String seclevel;//安全级别
    private String accounttype;//账号类型
    private String belongto;//所属主账号

    private String systemlanguage;//语言
    private String passWord;//密码
    private String id;
    private String statusOa; //员工状态
    private String sexOa; // 性别OA
    private String locationId;

    private String jobtitlecode; //岗位编码
    private String errMessage;

    public void changeStatus(Map<String, String> statusMap) {
        if ("男".equals(this.sex)) {
            this.sexOa = "0";
        } else {
            this.sexOa = "1";
        }

        if (!"居民身份证".equals(Util.null2String(this.certificatenumType).trim())) {
            this.certificatenum = "";
        }

        if ("0".equals(this.dataStatus)) {
            this.statusOa = "7"; // 无效
        } else {
            this.statusOa = statusMap.get(this.status);
        }
    }

    public String getWorkCode() {
        return workCode;
    }

    public void setWorkCode(String workCode) {
        this.workCode = workCode;
    }

    public String getJobtitlecode() {
        return jobtitlecode;
    }

    public void setJobtitlecode(String jobtitlecode) {
        this.jobtitlecode = jobtitlecode;
    }


    /**
     * 获取所有上级String
     *
     * @param managerid 上级id
     * @return 所有上级id
     */
    public String getManagerIdAndStr(String managerid) {
        RecordSet rs = new RecordSet();
        String returnStr = "";
        if (null == managerid || "".equals(managerid)) {
            return returnStr;
        }
        try {
            rs.executeQuery("select managerstr from HrmResource where id = " + managerid);
            String managerstr = "";
            if (rs.next()) {
                managerstr = Util.null2String(rs.getString("managerstr"));
                if ("".equals(managerstr)) {
                    managerstr = "" + managerid;
                } else {
                    managerstr = managerstr + "," + managerid;
                }
            }
            returnStr = replaceStr(managerstr) + ",";
        } catch (Exception e) {
            LOGGER.error("get hrmresource all manager Exception :" + e);
        }
        return returnStr;
    }

    private static String replaceStr(String str) {
        ArrayList list = Util.TokenizerString(str, ",");
        StringBuilder temp = new StringBuilder();
        for (Object o : list) {
            if ("".equals(temp.toString())) {
                temp = new StringBuilder((String) o);
            } else {
                temp.append(",").append((String) o);
            }
        }
        return temp.toString();
    }

    /**
     * 新增人员初始化权限设置
     */
    public void updaterights(String maxid) {
        try {
            char separator = Util.getSeparator();
            Calendar todaycal = Calendar.getInstance();
            String today = Util.add0(todaycal.get(Calendar.YEAR), 4) + "-" + Util.add0(todaycal.get(Calendar.MONTH) + 1, 2) + "-" + Util.add0(todaycal.get(Calendar.DAY_OF_MONTH), 2);
            String userpara = "" + 1 + separator + today;
            RecordSet rs = new RecordSet();
            rs.executeProc("HrmResource_CreateInfo", "" + maxid + separator + userpara + separator + userpara);
            rs.executeSql("select hrmid from HrmInfoStatus where hrmid=" + maxid);
            if (!rs.next()) {
                String sql_1 = "insert into HrmInfoStatus (itemid,hrmid,status) values(1," + maxid + ",1)";
                rs.executeSql(sql_1);
                String sql_2 = "insert into HrmInfoStatus (itemid,hrmid) values(2," + maxid + ")";
                rs.executeSql(sql_2);
                String sql_3 = "insert into HrmInfoStatus (itemid,hrmid) values(3," + maxid + ")";
                rs.executeSql(sql_3);
                String sql_10 = "insert into HrmInfoStatus (itemid,hrmid) values(10," + maxid + ")";
                rs.executeSql(sql_10);
            }
        } catch (Exception e) {
            LOGGER.error("update rights Exception :" + e);
        }
    }

    public String getCertificatenum() {
        return certificatenum;
    }

    public void setCertificatenum(String certificatenum) {
        this.certificatenum = certificatenum;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getSexOa() {
        return sexOa;
    }

    public void setSexOa(String sexOa) {
        this.sexOa = sexOa;
    }

    public String getStatusOa() {
        return statusOa;
    }

    public void setStatusOa(String statusOa) {
        this.statusOa = statusOa;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFolk() {
        return folk;
    }

    public void setFolk(String folk) {
        this.folk = folk;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(String dataStatus) {
        this.dataStatus = dataStatus;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDsporder() {
        return dsporder;
    }

    public void setDsporder(String dsporder) {
        this.dsporder = dsporder;
    }

    public String getDepId() {
        return depId;
    }

    public void setDepId(String depId) {
        this.depId = depId;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public String getJobtitleId() {
        return jobtitleId;
    }

    public void setJobtitleId(String jobtitleId) {
        this.jobtitleId = jobtitleId;
    }

    public String getManagerIdReal() {
        return managerIdReal;
    }

    public void setManagerIdReal(String managerIdReal) {
        this.managerIdReal = managerIdReal;
    }

    public String getManagerstr() {
        return managerstr;
    }

    public void setManagerstr(String managerstr) {
        this.managerstr = managerstr;
    }

    public String getManagerCode() {
        return managerCode;
    }

    public void setManagerCode(String managerCode) {
        this.managerCode = managerCode;
    }

    public String getSeclevel() {
        return seclevel;
    }

    public void setSeclevel(String seclevel) {
        this.seclevel = seclevel;
    }

    public String getAccounttype() {
        return accounttype;
    }

    public void setAccounttype(String accounttype) {
        this.accounttype = accounttype;
    }

    public String getBelongto() {
        return belongto;
    }

    public void setBelongto(String belongto) {
        this.belongto = belongto;
    }

    public String getSystemlanguage() {
        return systemlanguage;
    }

    public void setSystemlanguage(String systemlanguage) {
        this.systemlanguage = systemlanguage;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getCertificatenumType() {
        return certificatenumType;
    }

    public void setCertificatenumType(String certificatenumType) {
        this.certificatenumType = certificatenumType;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getCompanystartdate() {
        return companystartdate;
    }

    public void setCompanystartdate(String companystartdate) {
        this.companystartdate = companystartdate;
    }

    public String getWorkstartdate() {
        return workstartdate;
    }

    public void setWorkstartdate(String workstartdate) {
        this.workstartdate = workstartdate;
    }
}
