package com.weavernorth.zhongDk.orgSyn.util;

import com.alibaba.fastjson.JSONObject;
import com.weaver.general.Util;
import com.weavernorth.zhongDk.orgSyn.vo.ZdkSubCompany;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.ConnStatement;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZdkConnUtil {

    private static final Log LOGGER = LogFactory.getLog(ZdkConnUtil.class);

    public static List<ZdkSubCompany> synSubCompany(List<ZdkSubCompany> subCompanyList, int count) {
        LOGGER.info("第 " + count + " 次执行分部同步=========================");
        // sunCompanyCode - id map
        Map<String, String> numIdMap = new HashMap<>();
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("select id, subcompanycode from hrmsubcompany");
        while (recordSet.next()) {
            if (!"".equals(recordSet.getString("subcompanycode"))) {
                numIdMap.put(recordSet.getString("subcompanycode"), recordSet.getString("id"));
            }
        }

        List<ZdkSubCompany> insertHrmDepartments = new ArrayList<>();
        List<ZdkSubCompany> updateHrmDepartments = new ArrayList<>();
        List<ZdkSubCompany> errorHrmDepartments = new ArrayList<>();
        for (ZdkSubCompany subCompany : subCompanyList) {
            // 分部编码
            String subCode = Util.null2String(subCompany.getOrgCode()).trim();
            // 上级编码
            String supperCode = Util.null2String(subCompany.getSupCode()).trim();
            // 分部状态 1封存，0代表有效
            String statusOa = Util.null2String(subCompany.getStatusOa()).trim();

            LOGGER.info("==========================");
            LOGGER.info("分部编码： " + subCode + ", 上级编码： " + supperCode + ", 分部名称： " +
                    subCompany.getOrgFullName() + ", 主数据分部状态： " + subCompany.getStatus() + ", 转换后分部状态： " + statusOa);

            if ("".equals(subCode)) {
                String errMes = "分部编码为空，上级编码： " + supperCode + "分部名称： " + subCompany.getOrgFullName();
                subCompany.setErrMessage(errMes);
                errorHrmDepartments.add(subCompany);
                continue;
            }
            if ("".equals(supperCode)) {
                String errMes = "分部编上级编码为空，分部编码： " + subCode + "分部名称： " + subCompany.getOrgFullName();
                subCompany.setErrMessage(errMes);
                errorHrmDepartments.add(subCompany);
                continue;
            }
            if ("".equals(subCompany.getOrgFullName())) {
                String errMes = "分部名称为空，分部编码： " + subCode + "上级编码： " + supperCode;
                LOGGER.info(errMes);
                subCompany.setErrMessage(errMes);
                errorHrmDepartments.add(subCompany);
                continue;
            }

            int subCompanyId = Util.getIntValue(numIdMap.get(supperCode), 0);
            if (!"0".equals(supperCode) && subCompanyId <= 0) {
                if (count >= 2) {
                    String errMes = "上级分部不存在 - 分部编码 - " + subCode + " - 分部名称 - " + subCompany.getOrgFullName();
                    //第3次仍有错误，插入错误信息
                    LOGGER.info(errMes);
                    subCompany.setErrMessage(errMes);
                    errorHrmDepartments.add(subCompany);
                }
                errorHrmDepartments.add(subCompany);
                continue;
            }

            subCompany.setSupperCompanyId(String.valueOf(subCompanyId));
            if (numIdMap.get(subCode) == null) {
                insertHrmDepartments.add(subCompany);
                numIdMap.put(subCode, "1");
            } else {
                updateHrmDepartments.add(subCompany);
            }

        }

        insertHrmSubCompany(insertHrmDepartments);
        updateHrmSubCompany(updateHrmDepartments);

        if (count >= 2) {
            LOGGER.error("分部同步失败数据： " + JSONObject.toJSONString(errorHrmDepartments));
        }
        return errorHrmDepartments;
    }

    private static void insertHrmSubCompany(List<ZdkSubCompany> insertHrmDepartments) {
        ConnStatement statement = new ConnStatement();
        try {
            String sql = "insert into hrmsubcompany (subcompanyname, subcompanydesc, supsubcomid, canceled, showorder, " +
                    "subcompanycode, companyid )values (?,?,?,?,?, ?,?)";
            statement.setStatementSql(sql);
            for (ZdkSubCompany subCompany : insertHrmDepartments) {
                LOGGER.info(subCompany.toString());
                statement.setString(1, subCompany.getOrgShortName());
                statement.setString(2, subCompany.getOrgFullName());
                statement.setString(3, subCompany.getSupperCompanyId());
                statement.setString(4, subCompany.getStatusOa());
                statement.setString(5, "0");

                statement.setString(6, subCompany.getOrgCode());
                statement.setString(7, "1"); // 所属总部id

                statement.executeUpdate();
            }

        } catch (Exception e) {
            new BaseBean().writeLog("insert hrmsubcompany Exception :" + e);
        } finally {
            statement.close();
            // 插入分部权限
            RecordSet recordSet = new RecordSet();
            for (ZdkSubCompany subCompany : insertHrmDepartments) {
                recordSet.executeQuery("select id from hrmsubcompany where subcompanycode = ?", subCompany.getOrgCode());
                if (recordSet.next()) {
                    insertMenuConfig(recordSet.getString("id"));
                }
            }
        }

    }

    /**
     * 更新分部
     */
    private static void updateHrmSubCompany(List<ZdkSubCompany> subCompanyList) {

        ConnStatement statement = new ConnStatement();
        try {
            String sql = "update hrmsubcompany set  subcompanyname = ?, subcompanydesc = ?, supsubcomid = ?, canceled = ? " +
                    " where subcompanycode = ?";
            statement.setStatementSql(sql);
            for (ZdkSubCompany hrmSubCompany : subCompanyList) {
                new BaseBean().writeLog(hrmSubCompany.toString());
                statement.setString(1, hrmSubCompany.getOrgShortName());
                statement.setString(2, hrmSubCompany.getOrgFullName());
                statement.setString(3, hrmSubCompany.getSupperCompanyId());
                statement.setString(4, hrmSubCompany.getStatusOa());
                statement.setString(5, hrmSubCompany.getOrgCode());

                statement.executeUpdate();
            }
        } catch (Exception e) {
            new BaseBean().writeLog("update hrmsubcompany Exception :" + e);
        } finally {
            statement.close();
        }

    }

    private static void insertMenuConfig(String id) {
        RecordSet rs = new RecordSet();
        rs.executeUpdate("insert into leftmenuconfig (userid,infoid,visible,viewindex,resourceid,resourcetype,locked,lockedbyid,usecustomname,customname,customname_e)  select  distinct  userid,infoid,visible,viewindex," + id + ",2,locked,lockedbyid,usecustomname,customname,customname_e from leftmenuconfig  where resourcetype=1  and resourceid=1");
        rs.executeUpdate("insert into mainmenuconfig (userid,infoid,visible,viewindex,resourceid,resourcetype,locked,lockedbyid,usecustomname,customname,customname_e)  select  distinct  userid,infoid,visible,viewindex," + id + ",2,locked,lockedbyid,usecustomname,customname,customname_e from mainmenuconfig where resourcetype=1  and resourceid=1");

    }


    public static List<ZdkSubCompany> synDepartment(List<ZdkSubCompany> departmentList, int count) {
        BaseBean baseBean = new BaseBean();
        baseBean.writeLog("第 " + count + " 次执行部门同步=========================");
        // departmentCode - id map
        Map<String, String> numIdMap = new HashMap<String, String>();
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("select id, departmentcode from HrmDepartment");
        while (recordSet.next()) {
            if (!"".equals(recordSet.getString("departmentcode"))) {
                numIdMap.put(recordSet.getString("departmentcode"), recordSet.getString("id"));
            }
        }

        // 部门 - id 所属分部id
        Map<Integer, String> idSubIdMap = new HashMap<Integer, String>();
        recordSet.executeQuery("select id, subcompanyid1 from hrmdepartment");
        while (recordSet.next()) {
            if (!"".equals(recordSet.getString("subcompanyid1"))) {
                idSubIdMap.put(recordSet.getInt("id"), recordSet.getString("subcompanyid1"));
            }
        }

        // 分部编码 - id map
        recordSet.executeQuery("select SUBCOMPANYCODE, ID from HRMSUBCOMPANY");
        Map<String, String> subIdMap = new HashMap<>(recordSet.getCounts() + 10);
        while (recordSet.next()) {
            if (!"".equalsIgnoreCase(recordSet.getString("SUBCOMPANYCODE").trim())) {
                subIdMap.put(recordSet.getString("SUBCOMPANYCODE"), recordSet.getString("ID"));
            }
        }

        List<ZdkSubCompany> insertHrmDepartments = new ArrayList<>();
        List<ZdkSubCompany> updateHrmDepartments = new ArrayList<>();
        List<ZdkSubCompany> errorHrmDepartments = new ArrayList<>();
        for (ZdkSubCompany department : departmentList) {
            // 部门编码
            String depcode = Util.null2String(department.getOrgCode()).trim();
            // 上级编码
            String supperCode = Util.null2String(department.getSupCode()).trim();

            baseBean.writeLog("==========================");
            baseBean.writeLog("部门编码： " + depcode + ", 上级编码： " + supperCode);
            baseBean.writeLog("部门名称： " + department.getOrgFullName() + ", 部门状态： " + department.getStatus() +
                    ", 部门状态oa: " + department.getStatusOa());

            if ("".equals(depcode)) {
                String errMes = "部门编码为空，上级编码： " + supperCode + "部门名称： " + department.getOrgFullName();
                baseBean.writeLog(errMes);
                department.setErrMessage(errMes);
                errorHrmDepartments.add(department);
                continue;
            }
            if ("".equals(supperCode)) {
                String errMes = "部门编上级编码为空，部门编码： " + depcode + "部门名称： " + department.getOrgFullName();
                baseBean.writeLog(errMes);
                department.setErrMessage(errMes);
                errorHrmDepartments.add(department);
                continue;
            }
            if ("".equals(department.getOrgFullName())) {
                String errMes = "部门名称为空，部门编码： " + depcode + "上级编码： " + supperCode;
                department.setErrMessage(errMes);
                errorHrmDepartments.add(department);
                continue;
            }

            int subCompanyId = Util.getIntValue(subIdMap.get(supperCode), 0);
            if (subCompanyId > 0) {
                // 上级是分部
                department.setSupperDepartmentId("0"); // 上级部门id设为0
                department.setSupperCompanyId(String.valueOf(subCompanyId));
            } else {
                // 上级是部门
                int subDepId = Util.getIntValue(numIdMap.get(supperCode), 0);
                if (subDepId <= 0) {
                    if (count >= 2) {
                        String errMes = "上级部门不存在，部门编码： " + depcode + ", 上级编码： " + supperCode + "部门名称： " + department.getOrgFullName();
                        baseBean.writeLog(errMes);
                        department.setErrMessage(errMes);
                        errorHrmDepartments.add(department);
                        continue;
                    }
                    errorHrmDepartments.add(department);
                    continue;
                }
                department.setSupperDepartmentId(String.valueOf(subDepId));
                department.setSupperCompanyId(idSubIdMap.get(subDepId));
            }

            if (numIdMap.get(depcode) == null) {
                insertHrmDepartments.add(department);
                numIdMap.put(depcode, "");
            } else {
                updateHrmDepartments.add(department);
            }

            baseBean.writeLog("新增部门数： " + insertHrmDepartments.size());
            insertHrmDepartment(insertHrmDepartments);

            baseBean.writeLog("更新部门数： " + updateHrmDepartments.size());
            updateHrmDepartment(updateHrmDepartments);
        }
        return errorHrmDepartments;
    }

    private static void insertHrmDepartment(List<ZdkSubCompany> insertHrmDepartments) {
        ConnStatement statement = new ConnStatement();
        try {
            String sql = "insert into HrmDepartment (departmentcode, departmentname, departmentmark," +
                    " subcompanyid1, supdepid, canceled, showorder) " +
                    "values (?,?,?,?,?,  ?,?)";
            statement.setStatementSql(sql);
            for (ZdkSubCompany department : insertHrmDepartments) {
                new BaseBean().writeLog(department.toString());
                statement.setString(1, department.getOrgCode());
                statement.setString(2, department.getOrgFullName());
                statement.setString(3, department.getOrgShortName());
                statement.setString(4, department.getSupperCompanyId());
                statement.setString(5, department.getSupperDepartmentId());
                statement.setString(6, department.getStatusOa());
                statement.setString(7, "0");
                statement.executeUpdate();
            }
        } catch (Exception e) {
            new BaseBean().writeLog("insert department Exception :" + e);
        } finally {
            statement.close();
        }

    }

    /**
     * 更新部门
     */
    private static void updateHrmDepartment(List<ZdkSubCompany> insertHrmDepartments) {

        ConnStatement statement = new ConnStatement();
        try {
            String sql = "update HrmDepartment set  departmentname = ?, departmentmark = ?, subcompanyid1 = ?, supdepid = ?," +
                    " canceled = ?, where departmentcode = ?";
            statement.setStatementSql(sql);
            for (ZdkSubCompany hrmDepartment : insertHrmDepartments) {
                new BaseBean().writeLog(hrmDepartment.toString());
                statement.setString(1, hrmDepartment.getOrgFullName());
                statement.setString(2, hrmDepartment.getOrgShortName());
                statement.setString(3, hrmDepartment.getSupperCompanyId());
                statement.setString(4, hrmDepartment.getSupperDepartmentId());
                statement.setString(5, hrmDepartment.getStatusOa());

                statement.setString(6, hrmDepartment.getOrgCode());
                statement.executeUpdate();
            }
        } catch (Exception e) {
            new BaseBean().writeLog("update department Exception :" + e);
        } finally {
            statement.close();
        }

    }

}
