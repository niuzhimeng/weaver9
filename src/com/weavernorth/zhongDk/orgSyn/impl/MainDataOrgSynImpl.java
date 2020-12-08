package com.weavernorth.zhongDk.orgSyn.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weavernorth.zhongDk.orgSyn.MainDataOrgSyn;
import com.weavernorth.zhongDk.orgSyn.util.ZdkConnUtil;
import com.weavernorth.zhongDk.orgSyn.vo.ZdkResource;
import com.weavernorth.zhongDk.orgSyn.vo.ZdkSubCompany;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.hrm.company.DepartmentComInfo;
import weaver.hrm.company.SubCompanyComInfo;
import weaver.hrm.resource.ResourceComInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainDataOrgSynImpl implements MainDataOrgSyn {

    private static final Log LOGGER = LogFactory.getLog(MainDataOrgSynImpl.class);

    @Override
    public String synOrganization(String orgJsonStr) {
        LOGGER.info("分部、部门同步开始, 接收数据：" + orgJsonStr);
        try {
            JSONObject jsonObject = JSONObject.parseObject(orgJsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("LIST");
            int size = jsonArray.size();
            LOGGER.info("本次接收数据总数： " + size);

            List<ZdkSubCompany> subCompanyList = new ArrayList<>();
            List<ZdkSubCompany> departmentList = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                JSONObject orgObj = jsonArray.getJSONObject(i).getJSONObject("B01");
                ZdkSubCompany zdkSubCompany = JSONObject.toJavaObject(orgObj, ZdkSubCompany.class);
                zdkSubCompany.changeStatusOa();
                if ("单位".equals(Util.null2String(zdkSubCompany.getOrgType()).trim())) {
                    subCompanyList.add(zdkSubCompany);  // 分部
                } else {
                    departmentList.add(zdkSubCompany);     // 部门
                }
            }

            LOGGER.info("分部操作开始==============共计： " + subCompanyList.size() + " 条");
            long time1 = System.currentTimeMillis();
            int companyErrorCount = 1;
            for (int i = 1; i <= 3; i++) {
                if (companyErrorCount > 0) {
                    subCompanyList = ZdkConnUtil.synSubCompany(subCompanyList, i);
                    companyErrorCount = subCompanyList.size();
                    LOGGER.info("待处理分部数量： " + companyErrorCount);
                }
            }
            // 清空分部缓存
            new SubCompanyComInfo().removeCompanyCache();
            long time2 = System.currentTimeMillis();
            LOGGER.info("分部信息同步完成, 耗时：" + (time2 - time1) + " 毫秒。");

            LOGGER.info("部门操作开始==============共计： " + departmentList.size() + " 条");
            long time3 = System.currentTimeMillis();
            int departmentErrorCount = 1;
            for (int i = 1; i <= 3; i++) {
                if (departmentErrorCount > 0) {
                    departmentList = ZdkConnUtil.synDepartment(departmentList, i);
                    departmentErrorCount = departmentList.size();
                    LOGGER.info("待处理部门数量： " + departmentErrorCount);
                }
            }

            // 清空部门缓存
            new DepartmentComInfo().removeCompanyCache();
            long time4 = System.currentTimeMillis();
            LOGGER.info("部门信息同步完成, 耗时：" + (time4 - time3) + " 毫秒。");
            if (companyErrorCount > 0 || departmentErrorCount > 0) {
                return "0";
            }
            LOGGER.info("分部、部门同步结束==============");
        } catch (Exception e) {
            LOGGER.error("分部、部门同步异常： " + e);
        }

        return "1";
    }

    @Override
    public String synHrmResource(String manJsonStr) {
        LOGGER.info("人员同步开始, 接收数据：" + manJsonStr);
        try {
            JSONObject jsonObject = JSONObject.parseObject(manJsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("LIST");
            int size = jsonArray.size();
            LOGGER.info("本次接收数据总数： " + size);

            // 部门编码 - id map
            Map<String, String> depIdMap = new HashMap<>();
            // 部门 - id 所属分部id
            Map<Integer, Integer> idSubIdMap = new HashMap<>();

            RecordSet recordSet = new RecordSet();
            recordSet.executeQuery("select id, departmentcode, subcompanyid1 from HrmDepartment");
            while (recordSet.next()) {
                if (!"".equals(recordSet.getString("departmentcode"))) {
                    depIdMap.put(recordSet.getString("departmentcode"), recordSet.getString("id"));
                }
                if (!"".equals(recordSet.getString("subcompanyid1"))) {
                    idSubIdMap.put(recordSet.getInt("id"), recordSet.getInt("subcompanyid1"));
                }
            }

            // 岗位编码 - 岗位id
            Map<String, String> jobCodeIdMap = new HashMap<>();
            recordSet.executeQuery("select id, jobtitlecode from hrmjobtitles");
            while (recordSet.next()) {
                if (!"".equals(recordSet.getString("jobtitlecode"))) {
                    jobCodeIdMap.put(recordSet.getString("jobtitlecode"), recordSet.getString("id"));
                }
            }

            // 根据【身份证】判断人员是否存在
            List<String> existList = new ArrayList<>();
            recordSet.executeQuery("select workcode from hrmresource");
            while (recordSet.next()) {
                if (!"".equals(recordSet.getString("workcode"))) {
                    existList.add(recordSet.getString("workcode"));
                }
            }

            List<ZdkResource> insertHrmResource = new ArrayList<>();
            List<ZdkResource> updateHrmResource = new ArrayList<>();
            List<ZdkResource> errHrmResourceList = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                JSONObject orgObj = jsonArray.getJSONObject(i).getJSONObject("USRA01");
                ZdkResource hrmResource = JSONObject.toJavaObject(orgObj, ZdkResource.class);
                hrmResource.changeStatus();

                // 身份证号码（唯一标识）
                String certificatenum = Util.null2String(hrmResource.getCertificatenum()).trim();
                // 岗位编码
                String jobtitlecode = Util.null2String(hrmResource.getJobtitlecode()).trim();
                // 登录名
                String loginId = Util.null2String(hrmResource.getLoginId()).trim();
                // 所属部门编码
                String depCode = Util.null2String(hrmResource.getDepartmentCode()).trim();
                // 工作地点
                String locationStr = Util.null2String(hrmResource.getLocation()).trim();
                if ("".equals(locationStr)) {
                    locationStr = "北京";
                }
                String locationId = ZdkConnUtil.insertLocation(locationStr);
                String telPhone = Util.null2String(hrmResource.getTelephone());

                LOGGER.info("========================");
                LOGGER.info("人员姓名： " + hrmResource.getLastName() + ", 身份证号码：" + certificatenum + ", 登录名： " + loginId +
                        ", 所属部门编码： " + depCode + ", 主数据Status： " + hrmResource.getStatus() + ", 岗位编码： " + jobtitlecode +
                        ", 性别： " + hrmResource.getSex() + ", 工作地点： " + locationStr + ", 办公室电话: " + telPhone);
                // 部门ID
                int depId = Util.getIntValue(depIdMap.get(depCode), 0);

                if ("".equals(loginId)) {
                    // 登录名为空
                    String errMsg = "人员【登录名】为空，部门code： " + depCode + " ,人员编码： " + hrmResource.getWorkCode() + ", 姓名： " + hrmResource.getLastName();
                    LOGGER.info(errMsg);
                    hrmResource.setErrMessage(errMsg);
                    errHrmResourceList.add(hrmResource);
                    continue;
                }
                if ("".equals(Util.null2String(hrmResource.getLastName()).trim())) {
                    // 姓名为空
                    String errMsg = "人员【姓名】为空，部门code： " + depCode + " , 登录名： " + loginId + ", 姓名： " + hrmResource.getLastName();
                    LOGGER.info(errMsg);
                    hrmResource.setErrMessage(errMsg);
                    errHrmResourceList.add(hrmResource);
                    continue;
                }
                if (depId <= 0) {
                    //所属部门不存在
                    String errMsg = "人员所属【部门】不存在，部门code： " + depCode + " ,登录名： " + loginId + ", 姓名： " + hrmResource.getLastName();
                    LOGGER.info(errMsg);
                    hrmResource.setErrMessage(errMsg);
                    errHrmResourceList.add(hrmResource);
                    continue;
                }

                // 所属分部id
                int subId = idSubIdMap.get(depId);

                //默认密码
                hrmResource.setPassWord(Util.getEncrypt("123456"));
                hrmResource.setDepId(String.valueOf(depId));
                hrmResource.setSubId(String.valueOf(subId));
                hrmResource.setJobtitleId(jobCodeIdMap.get(jobtitlecode));
                hrmResource.setLocationId(locationId);
                //账号类型
                hrmResource.setAccounttype("0");
                //语言类型
                hrmResource.setSystemlanguage("7");
                // 安全级别默认10
                hrmResource.setSeclevel("10");
                hrmResource.setTelephone(telPhone);

                if (!existList.contains(certificatenum)) {
                    String newId = String.valueOf(ZdkConnUtil.getHrmMaxId());
                    hrmResource.setId(newId);
                    insertHrmResource.add(hrmResource);
                    existList.add(certificatenum);
                } else {
                    updateHrmResource.add(hrmResource);
                }
            }

            LOGGER.info("新增人员数： " + insertHrmResource.size());
            ZdkConnUtil.insertHrmResource(insertHrmResource);

            LOGGER.info("更新人员数： " + updateHrmResource.size());
            ZdkConnUtil.updateHrmResource(updateHrmResource);

            LOGGER.info("人员同步失败数据： " + JSONObject.toJSONString(errHrmResourceList));

            // 清空人员缓存
            new ResourceComInfo().removeResourceCache();

        } catch (Exception e) {
            LOGGER.error("人员同步异常： " + e);
        }
        LOGGER.info("人员同步结束==============");
        return "1";
    }
}
