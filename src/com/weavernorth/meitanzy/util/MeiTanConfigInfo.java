package com.weavernorth.meitanzy.util;

public enum MeiTanConfigInfo {
    appuser("MKZY"), // 应用授权代码
    secretkey("TYBVfTDugOHigY7nc3Ll+lyPEUsLcIq2NFVg0hI7PiQ="), // 秘钥
    DWBM("mkzy"), // 单位编码
    LOGIN_URL("http://172.18.102.210:8888/rest/login"), // 获取token接口url
    QIAN_DING_URL("http://172.18.102.210:8888/rest/registerContractInfo"), // 合同签订信息上报接口URL
    KUAN_XIANG_URL("http://172.18.102.210:8888/rest/registerPaymentPlanAndFeedbackInfo"), // 款项信息上报接口
    SHOU_RU_URL("http://172.18.102.210:8888/rest/registerIncomeInfo"), // 收入信息上报接口

    FTP_URL("172.18.102.210"), // ftp地址
    FTP_USERNAME("mkzyftp"), // ftp用户名
    FTP_PASSWORD("mkzyftp"), // ftp密码
    FTP_PORT("21"), // ftp端口

    // 流程下拉框
    HTBD("6795"); // 合同标的字段id

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
