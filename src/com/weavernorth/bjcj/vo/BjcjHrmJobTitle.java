package com.weavernorth.bjcj.vo;

import weaver.general.BaseBean;

public class BjcjHrmJobTitle extends BaseBean {

    private String jobTitleCode;

    private String jobTitleName;

    private String jobActivityId;

    public String getJobActivityId() {
        return jobActivityId;
    }

    public void setJobActivityId(String jobActivityId) {
        this.jobActivityId = jobActivityId;
    }

    public String getJobTitleCode() {
        return jobTitleCode;
    }

    public void setJobTitleCode(String jobTitleCode) {
        this.jobTitleCode = jobTitleCode;
    }

    public String getJobTitleName() {
        return jobTitleName;
    }

    public void setJobTitleName(String jobTitleName) {
        this.jobTitleName = jobTitleName;
    }
}
