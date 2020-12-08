package com.weavernorth.zhongDk.orgSyn.util;

import com.alibaba.fastjson.JSONObject;
import com.weaver.general.TimeUtil;
import com.weaver.general.Util;
import com.weavernorth.zhongDk.orgSyn.vo.ZdkResource;
import com.weavernorth.zhongDk.orgSyn.vo.ZdkSubCompany;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.ConnStatement;
import weaver.conn.RecordSet;

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
            // 唯一标识码
            String uniqueId = Util.null2String(subCompany.getUniqueId()).trim();
            // 分部编码
            String subCode = Util.null2String(subCompany.getOrgCode()).trim();
            // 上级编码
            String supperCode = Util.null2String(subCompany.getSupCode()).trim();
            // 分部状态 1封存，0代表有效
            String statusOa = Util.null2String(subCompany.getStatusOa()).trim();

            LOGGER.info("==========================");
            LOGGER.info("分部编码： " + subCode + ", 上级编码： " + supperCode + ", 分部名称： " +
                    subCompany.getOrgShortName() + ", 主数据分部状态： " + subCompany.getStatus() + ", 转换后分部状态： " + statusOa +
                    ", UNIQUE_ID: " + uniqueId);

            if ("".equals(subCode)) {
                String errMes = "分部编码为空，上级编码： " + supperCode + "分部名称： " + subCompany.getOrgFullName();
                subCompany.setErrMessage(errMes);
                errorHrmDepartments.add(subCompany);
                continue;
            }

            if ("".equals(uniqueId)) {
                String errMes = "分部唯一标识码为空，上级编码： " + supperCode + "分部名称： " + subCompany.getOrgFullName() +
                        ", 分部编码: " + subCompany.getOrgCode();
                subCompany.setErrMessage(errMes);
                errorHrmDepartments.add(subCompany);
                continue;
            }

            if ("".equals(subCompany.getOrgShortName())) {
                String errMes = "分部名称为空，分部编码： " + subCode + "上级编码： " + supperCode;
                LOGGER.info(errMes);
                subCompany.setErrMessage(errMes);
                errorHrmDepartments.add(subCompany);
                continue;
            }

            int subCompanyId = Util.getIntValue(numIdMap.get(supperCode), 0);
            if (!"".equals(supperCode) && subCompanyId <= 0) {
                if (count >= 3) {
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

        LOGGER.info("新增分部数： " + insertHrmDepartments.size());
        insertHrmSubCompany(insertHrmDepartments);
        LOGGER.info("更新分部数： " + updateHrmDepartments.size());
        updateHrmSubCompany(updateHrmDepartments);

        if (count >= 3) {
            LOGGER.error("分部同步失败数据： " + JSONObject.toJSONString(errorHrmDepartments));
            insertErrLog(errorHrmDepartments, "分部");
        }
        return errorHrmDepartments;
    }

    private static void insertHrmSubCompany(List<ZdkSubCompany> insertHrmDepartments) {
        ConnStatement statement = new ConnStatement();
        try {
            String sql = "insert into hrmsubcompany (subcompanyname, subcompanydesc, supsubcomid, canceled, showorder, " +
                    "subcompanycode, companyid, UNIQUE_ID)values (?,?,?,?,?, ?,?,?)";
            statement.setStatementSql(sql);
            for (ZdkSubCompany subCompany : insertHrmDepartments) {
                statement.setString(1, subCompany.getOrgShortName());
                statement.setString(2, subCompany.getOrgFullName());
                statement.setString(3, subCompany.getSupperCompanyId());
                statement.setString(4, subCompany.getStatusOa());
                statement.setString(5, "0");

                statement.setString(6, subCompany.getOrgCode());
                statement.setString(7, "1"); // 所属总部id
                statement.setString(8, subCompany.getUniqueId()); // 所属总部id

                statement.executeUpdate();
            }

        } catch (Exception e) {
            LOGGER.error("insert hrmsubcompany Exception :" + e);
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
            String sql = "update hrmsubcompany set  subcompanyname = ?, subcompanydesc = ?, supsubcomid = ?, canceled = ?, " +
                    " subcompanycode = ? where UNIQUE_ID = ?";
            statement.setStatementSql(sql);
            for (ZdkSubCompany hrmSubCompany : subCompanyList) {
                statement.setString(1, hrmSubCompany.getOrgShortName());
                statement.setString(2, hrmSubCompany.getOrgFullName());
                statement.setString(3, hrmSubCompany.getSupperCompanyId());
                statement.setString(4, hrmSubCompany.getStatusOa());
                statement.setString(5, hrmSubCompany.getOrgCode());

                statement.setString(6, hrmSubCompany.getUniqueId());

                statement.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error("update hrmsubcompany Exception :" + e);
        } finally {
            statement.close();
        }

    }

    private static void insertMenuConfig(String id) {
        RecordSet rs = new RecordSet();
        rs.executeUpdate("insert into leftmenuconfig (userid,infoid,visible,viewindex,resourceid,resourcetype,locked,lockedbyid,usecustomname,customname,customname_e)  select  distinct  userid,infoid,visible,viewindex," + id + ",2,locked,lockedbyid,usecustomname,customname,customname_e from leftmenuconfig  where resourcetype=1  and resourceid=1");
        rs.executeUpdate("insert into mainmenuconfig (userid,infoid,visible,viewindex,resourceid,resourcetype,locked,lockedbyid,usecustomname,customname,customname_e)  select  distinct  userid,infoid,visible,viewindex," + id + ",2,locked,lockedbyid,usecustomname,customname,customname_e from mainmenuconfig where resourcetype=1  and resourceid=1");

    }

    /**
     * 部门同步方法
     *
     * @param departmentList 部门数据集合
     * @param count          第N次执行
     * @return 同步失败集合
     */
    public static List<ZdkSubCompany> synDepartment(List<ZdkSubCompany> departmentList, int count) {
        LOGGER.info("第 " + count + " 次执行部门同步=========================");

        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("select id, departmentcode, subcompanyid1 from HrmDepartment");
        // 部门编码 - id map
        Map<String, String> numIdMap = new HashMap<>(recordSet.getCounts());
        // 部门id 所属分部id
        Map<Integer, String> idSubIdMap = new HashMap<>(recordSet.getCounts());
        while (recordSet.next()) {
            if (!"".equals(recordSet.getString("departmentcode"))) {
                numIdMap.put(recordSet.getString("departmentcode"), recordSet.getString("id"));
            }
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
            // 唯一标识码
            String uniqueId = Util.null2String(department.getUniqueId()).trim();
            // 部门编码
            String depCode = Util.null2String(department.getOrgCode()).trim();
            // 上级编码
            String supperCode = Util.null2String(department.getSupCode()).trim();

            LOGGER.info("==========================");
            LOGGER.info("部门编码： " + depCode + ", 上级编码： " + supperCode +
                    ", 部门名称： " + department.getOrgFullName() + ", 部门状态： " + department.getStatus() +
                    ", 部门状态oa: " + department.getStatusOa());

            if ("".equals(uniqueId)) {
                String errMes = "部门唯一标识码为空，上级编码： " + supperCode + "部门名称： " + department.getOrgFullName() +
                        ", 部门编码： " + depCode;
                department.setErrMessage(errMes);
                errorHrmDepartments.add(department);
                continue;
            }
            if ("".equals(depCode)) {
                String errMes = "部门编码为空，上级编码： " + supperCode + "部门名称： " + department.getOrgFullName();
                department.setErrMessage(errMes);
                errorHrmDepartments.add(department);
                continue;
            }
            if ("".equals(supperCode)) {
                String errMes = "部门编上级编码为空，部门编码： " + depCode + "部门名称： " + department.getOrgFullName();
                department.setErrMessage(errMes);
                errorHrmDepartments.add(department);
                continue;
            }
            if ("".equals(department.getOrgShortName())) {
                String errMes = "部门名称为空，部门编码： " + depCode + "上级编码： " + supperCode;
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
                    if (count >= 3) {
                        String errMes = "上级部门不存在，部门编码： " + depCode + ", 上级编码： " + supperCode + "部门名称： " + department.getOrgFullName();
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

            if (numIdMap.get(depCode) == null) {
                insertHrmDepartments.add(department);
                numIdMap.put(depCode, "");
            } else {
                updateHrmDepartments.add(department);
            }
        }

        LOGGER.info("新增部门数： " + insertHrmDepartments.size());
        insertHrmDepartment(insertHrmDepartments);

        LOGGER.info("更新部门数： " + updateHrmDepartments.size());
        updateHrmDepartment(updateHrmDepartments);

        if (count >= 3) {
            LOGGER.error("部门同步失败数据： " + JSONObject.toJSONString(errorHrmDepartments));
            insertErrLog(errorHrmDepartments, "部门");
        }
        return errorHrmDepartments;
    }

    private static void insertHrmDepartment(List<ZdkSubCompany> insertHrmDepartments) {
        ConnStatement statement = new ConnStatement();
        try {
            String sql = "insert into HrmDepartment (departmentcode, departmentname, departmentmark," +
                    " subcompanyid1, supdepid, canceled, showorder, UNIQUE_ID) " +
                    "values (?,?,?,?,?,  ?,?,?)";
            statement.setStatementSql(sql);
            for (ZdkSubCompany department : insertHrmDepartments) {
                statement.setString(1, department.getOrgCode());
                statement.setString(2, department.getOrgFullName());
                statement.setString(3, department.getOrgShortName());
                statement.setString(4, department.getSupperCompanyId());
                statement.setString(5, department.getSupperDepartmentId());

                statement.setString(6, department.getStatusOa());
                statement.setString(7, "0");
                statement.setString(8, department.getUniqueId());
                statement.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error("insert department Exception :" + e);
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
                    " canceled = ?, departmentcode = ? where UNIQUE_ID = ?";
            statement.setStatementSql(sql);
            for (ZdkSubCompany hrmDepartment : insertHrmDepartments) {
                statement.setString(1, hrmDepartment.getOrgFullName());
                statement.setString(2, hrmDepartment.getOrgShortName());
                statement.setString(3, hrmDepartment.getSupperCompanyId());
                statement.setString(4, hrmDepartment.getSupperDepartmentId());
                statement.setString(5, hrmDepartment.getStatusOa());

                statement.setString(6, hrmDepartment.getOrgCode());
                statement.setString(7, hrmDepartment.getUniqueId());
                statement.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error("update department Exception :" + e);
        } finally {
            statement.close();
        }

    }


    public static int getHrmMaxId() {
        int maxID = 1;
        RecordSet rs = new RecordSet();
        try {
            rs.executeProc("HrmResourceMaxId_Get", "");
            if (rs.next()) {
                maxID = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxID;
    }

    /**
     * 新增人员
     */
    public static void insertHrmResource(List<ZdkResource> insertHrmResourceList) {
        ConnStatement statement = new ConnStatement();
        try {
            String sql = "insert into hrmresource (workcode, lastname, loginid, status, sex," +
                    " locationid, email, mobile, managerid, seclevel, " +
                    "departmentid, subcompanyid1, jobtitle, dsporder, id," +
                    "password, accounttype, belongto, systemlanguage, telephone, " +
                    "startdate, certificatenum ) " +
                    "values (?,?,?,?,?,  ?,?,?,?,?,  ?,?,?,?,?,  ?,?,?,?,?, ?,?)";
            statement.setStatementSql(sql);
            int stnCount = 0;
            for (ZdkResource hrmResource : insertHrmResourceList) {
                if (stnCount % 500 == 0) {
                    statement.close();
                    statement = new ConnStatement();
                    statement.setStatementSql(sql);
                }
                statement.setString(1, hrmResource.getWorkCode());
                statement.setString(2, hrmResource.getLastName());
                statement.setString(3, hrmResource.getLoginId());
                statement.setString(4, hrmResource.getStatusOa());
                statement.setString(5, hrmResource.getSexOa());

                statement.setString(6, hrmResource.getLocationId());
                statement.setString(7, hrmResource.getEmail());
                statement.setString(8, hrmResource.getPhone());
                statement.setString(9, hrmResource.getManagerIdReal());
                statement.setString(10, hrmResource.getSeclevel());

                statement.setString(11, hrmResource.getDepId());
                statement.setString(12, hrmResource.getSubId());
                statement.setString(13, hrmResource.getJobtitleId());
                statement.setString(14, hrmResource.getDsporder());
                statement.setString(15, hrmResource.getId());

                statement.setString(16, hrmResource.getPassWord());
                statement.setString(17, hrmResource.getAccounttype());
                statement.setString(18, hrmResource.getBelongto());
                statement.setString(19, hrmResource.getSystemlanguage());
                statement.setString(20, hrmResource.getTelephone());

                statement.setString(21, hrmResource.getStartdate());
                statement.setString(22, hrmResource.getCertificatenum());

                statement.executeUpdate();
                hrmResource.updaterights(hrmResource.getId());
                stnCount++;
            }
        } catch (Exception e) {
            LOGGER.error("insert HrmResource Exception :" + e);
        } finally {
            statement.close();
        }
    }

    /**
     * 更新人员
     */
    public static void updateHrmResource(List<ZdkResource> updateHrmResourceList) {

        ConnStatement statement = new ConnStatement();
        try {
            String sql = "update hrmresource set lastname = ?, status = ?, sex = ?, locationid = ?, mobile = ?, " +
                    "departmentid = ?, subcompanyid1 = ?, email = ?, workcode = ?, telephone = ?, " +
                    "loginid = ?, jobtitle = ? where certificatenum = ?";
            statement.setStatementSql(sql);
            int stnCount = 0;
            for (ZdkResource hrmResource : updateHrmResourceList) {
                if (stnCount % 500 == 0) {
                    statement.close();
                    statement = new ConnStatement();
                    statement.setStatementSql(sql);
                }
                statement.setString(1, hrmResource.getLastName());
                statement.setString(2, hrmResource.getStatusOa());
                statement.setString(3, hrmResource.getSexOa());
                statement.setString(4, hrmResource.getLocationId());
                statement.setString(5, hrmResource.getPhone());

                statement.setString(6, hrmResource.getDepId());
                statement.setString(7, hrmResource.getSubId());
                statement.setString(8, hrmResource.getEmail());
                statement.setString(9, hrmResource.getWorkCode());
                statement.setString(10, hrmResource.getTelephone());

                statement.setString(11, hrmResource.getLoginId());
                statement.setString(12, hrmResource.getJobtitleId());
                statement.setString(13, hrmResource.getCertificatenum());

                statement.executeUpdate();
                stnCount++;
            }
        } catch (Exception e) {
            LOGGER.error("update HrmResource Exception :" + e);
        } finally {
            statement.close();
        }
    }

    public static String insertLocation(String locationname) {
        if (null == locationname || "".equals(locationname)) {
            return "";
        }
        String locationId = locationIsRepeat(locationname);

        if ("".equals(locationId)) {
            RecordSet rs = new RecordSet();
            rs.executeUpdate("insert into HrmLocations(locationname,locationdesc,countryid) values ('" + locationname + "','" + locationname + "','0')");
            rs.executeQuery("select max(id) as id from HrmLocations");
            if (rs.next()) {
                locationId = Util.null2String(rs.getString("id"));
            }
        }
        return locationId;
    }

    private static String locationIsRepeat(String locationname) {
        String locationId = "";
        try {
            RecordSet rs = new RecordSet();
            rs.executeQuery("select id from HrmLocations where locationname = '" + locationname + "'");
            if (rs.next()) {
                locationId = Util.null2String(rs.getString("id"));
            }
        } catch (Exception e) {
            LOGGER.error("check location is Repeat Exception" + e);
        }
        return locationId;
    }

    /**
     * 插入错误日志
     *
     * @param errOrgList 错误数据集合
     * @param type       数据类型（分部、部门，人员）
     */
    private static void insertErrLog(List<ZdkSubCompany> errOrgList, String type) {
        String[] dates = TimeUtil.getCurrentTimeString().split(" ");
        RecordSet recordSet = new RecordSet();
        String insertSql = "insert into uf_zzxxtbrz(rq, sj, lx, cwxx, sjxx) values(?,?,?,?,?)";
        for (ZdkSubCompany company : errOrgList) {
            recordSet.executeUpdate(insertSql,
                    dates[0].trim(), dates[1].trim(), type, company.getErrMessage(), company.toString());
        }

    }

}
