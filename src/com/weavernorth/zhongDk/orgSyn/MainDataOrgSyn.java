package com.weavernorth.zhongDk.orgSyn;

public interface MainDataOrgSyn {
    /**
     * 分部、部门同步
     */
    String synOrganization(String orgJsonStr);

    /**
     * 人员同步
     */
    String synHrmResource(String manJsonStr);


}
