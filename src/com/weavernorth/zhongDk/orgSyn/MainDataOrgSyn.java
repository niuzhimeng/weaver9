package com.weavernorth.zhongDk.orgSyn;

public interface MainDataOrgSyn {
    /**
     * 分部、部门同步
     */
    void synOrganization(String orgJsonStr);

    /**
     * 人员同步
     */
    void synHrmResource(String manJsonStr);


}
