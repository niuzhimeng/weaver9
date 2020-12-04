package com.weavernorth.zhongDk.orgSyn.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weavernorth.zhongDk.orgSyn.MainDataOrgSyn;
import com.weavernorth.zhongDk.orgSyn.util.ZdkConnUtil;
import com.weavernorth.zhongDk.orgSyn.vo.ZdkSubCompany;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.general.Util;
import weaver.hrm.company.DepartmentComInfo;
import weaver.hrm.company.SubCompanyComInfo;

import java.util.ArrayList;
import java.util.List;

public class MainDataOrgSynImpl implements MainDataOrgSyn {

    private static final Log LOGGER = LogFactory.getLog(MainDataOrgSynImpl.class);

    @Override
    public void synOrganization(String orgJsonStr) {
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
                LOGGER.info("当前对象： " + zdkSubCompany.toString());
                zdkSubCompany.changeStatusOa();
                if ("10".equals(Util.null2String(zdkSubCompany.getOrgType()).trim())) {
                    subCompanyList.add(zdkSubCompany);  // 分部
                } else {
                    departmentList.add(zdkSubCompany);     // 部门
                }
            }

            LOGGER.info("分部操作开始==============");
            int companySize = subCompanyList.size(); // 分部总量
            long time1 = System.currentTimeMillis();

            int companyErrorCount = 1;
            for (int i = 0; i < 3; i++) {
                if (companyErrorCount > 0) {
                    subCompanyList = ZdkConnUtil.synSubCompany(subCompanyList, i);
                    companyErrorCount = subCompanyList.size();
                    LOGGER.info("待插入分部数量： " + companyErrorCount);
                }
            }
            // 清空分部缓存
            new SubCompanyComInfo().removeCompanyCache();
            long time2 = System.currentTimeMillis();
            LOGGER.info("分部信息同步完成，同步数量： " + companySize + ", 耗时：" + (time2 - time1) + " 毫秒。");

            LOGGER.info("部门操作开始==============");
            long time3 = System.currentTimeMillis();
            int departmentSize = subCompanyList.size(); // 部门总量

            int departmentErrorCount = 1;
            for (int i = 0; i < 3; i++) {
                if (departmentErrorCount > 0) {
                    departmentList = ZdkConnUtil.synDepartment(departmentList, i);
                    departmentErrorCount = departmentList.size();
                    LOGGER.info("待插入部门数量： " + departmentErrorCount);
                }
            }

            // 清空部门缓存
            new DepartmentComInfo().removeCompanyCache();
            long time4 = System.currentTimeMillis();
            LOGGER.info("部门信息同步完成，同步数量： " + departmentSize + ", 耗时：" + (time4 - time3) + " 秒。");
        } catch (Exception e) {
            LOGGER.error("分部、部门同步异常： " + e);
        }
        LOGGER.info("分部、部门同步结束==============");
    }

    @Override
    public void synHrmResource(String manXmlStr) {
        LOGGER.info("人员同步开始, 接收数据：" + manXmlStr);
        try {

        } catch (Exception e) {
            LOGGER.error("人员同步异常： " + e);
        }
        LOGGER.info("人员同步结束==============");
    }
}
