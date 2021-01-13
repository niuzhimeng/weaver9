package com.weavernorth.getHrm;

import weaver.hrm.webservice.DepartmentBean;
import weaver.hrm.webservice.JobTitleBean;
import weaver.hrm.webservice.SubCompanyBean;
import weaver.hrm.webservice.UserBean;

public interface OrgResource {

    UserBean[] getHrmUserInfo() throws Exception;

    String getHrmUserInfoXML() throws Exception;

    DepartmentBean[] getHrmDepartmentInfo() throws Exception;

    String getHrmDepartmentInfoXML() throws Exception;

    SubCompanyBean[] getHrmSubcompanyInfo() throws Exception;

    String getHrmSubcompanyInfoXML() throws Exception;

    JobTitleBean[] getHrmJobTitleInfo() throws Exception;

    String getHrmJobTitleInfoXML() throws Exception;
}
