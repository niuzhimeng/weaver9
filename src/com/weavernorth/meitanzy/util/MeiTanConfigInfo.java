package com.weavernorth.meitanzy.util;

public enum MeiTanConfigInfo {
    appuser("MKZY"), // 应用授权代码
    secretkey("TYBVfTDugOHigY7nc3Ll+lyPEUsLcIq2NFVg0hI7PiQ="), // 秘钥
    DWBM("cqyjy"), // 单位编码
    LOGIN_URL("http://172.20.71.2/rest/login"), // 获取token接口url
    QIAN_DING_URL("http://172.20.71.2/rest/registerContractInfo"), // 合同签订信息上报接口URL
    KUAN_XIANG_URL("http://172.20.71.2/rest/registerPaymentPlanAndFeedbackInfo"), // 款项信息上报接口
    SHOU_RU_URL("http://172.20.71.2/rest/registerIncomeInfo"), // 收入信息上报接口

    FTP_URL("172.20.71.2"), // ftp地址
    FTP_USERNAME("cqyjyftp"), // ftp用户名
    FTP_PASSWORD("cqyjyftp"), // ftp密码
    FTP_PORT("21"), // ftp端口

    // 流程下拉框
    HTBD("6740"); // 合同标的字段id

    MeiTanConfigInfo(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
