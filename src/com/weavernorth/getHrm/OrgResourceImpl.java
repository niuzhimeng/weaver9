package com.weavernorth.getHrm;

import weaver.hrm.webservice.*;

public class OrgResourceImpl implements OrgResource {
    private final HrmServiceImpl hrmService = new HrmServiceImpl();

    @Override
    public UserBean[] getHrmUserInfo() throws Exception {
        return hrmService.getHrmUserInfo("", "", "", "", "", "");
    }

    @Override
    public String getHrmUserInfoXML() throws Exception {
        return hrmService.getHrmUserInfoXML("", "", "", "", "", "");
    }

    @Override
    public DepartmentBean[] getHrmDepartmentInfo() throws Exception {
        return hrmService.getHrmDepartmentInfo("", "");
    }

    @Override
    public String getHrmDepartmentInfoXML() throws Exception {
        return hrmService.getHrmDepartmentInfoXML("", "");
    }

    @Override
    public SubCompanyBean[] getHrmSubcompanyInfo() throws Exception {
        return hrmService.getHrmSubcompanyInfo("");
    }

    @Override
    public String getHrmSubcompanyInfoXML() throws Exception {
        return hrmService.getHrmSubcompanyInfoXML("");
    }

    @Override
    public JobTitleBean[] getHrmJobTitleInfo() throws Exception {
        return hrmService.getHrmJobTitleInfo("", "", "");
    }

    @Override
    public String getHrmJobTitleInfoXML() throws Exception {
        return hrmService.getHrmJobTitleInfoXML("", "", "");
    }


}
