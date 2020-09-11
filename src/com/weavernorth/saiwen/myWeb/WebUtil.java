package com.weavernorth.saiwen.myWeb;

import com.weavernorth.saiwen.myWebFile.SevenU9WeaverLocator;
import com.weavernorth.saiwen.myWebFile.SevenU9WeaverSoap_PortType;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;

public class WebUtil {

    /**
     * 上传文件
     */
    public static String upLoadAttachFromXMLLocal(String myXml, String orgId) throws Exception {
        String[] userInfo = getUserInfo();
        SevenU9WeaverLocator locator = new SevenU9WeaverLocator();
        SevenU9WeaverSoap_PortType soap = locator.getSevenU9WeaverSoap();
        return soap.upLoadAttachFromXMLLocal(myXml, userInfo[0], userInfo[1], orgId);
    }

    /**
     * 创建供应商银行账号
     */
    public static String createSupplierBankAccountFromXML(String myXml, String orgId) throws Exception {
        String[] userInfo = getUserInfo();
        SevenU9WeaverLocator locator = new SevenU9WeaverLocator();
        SevenU9WeaverSoap_PortType soap = locator.getSevenU9WeaverSoap();
        return soap.createSupplierBankAccountFromXML(myXml, userInfo[0], userInfo[1], orgId);
    }

    /**
     * 付款单创建
     */
    public static String createPayBillFromXML(String myXml, String orgId) throws Exception {
        String[] userInfo = getUserInfo();
        SevenU9WeaverLocator locator = new SevenU9WeaverLocator();
        SevenU9WeaverSoap_PortType soap = locator.getSevenU9WeaverSoap();
        return soap.createpayBillFromXML(myXml, userInfo[0], userInfo[1], orgId);
    }

    /**
     * 借款余额查询
     */
    public static String getLoanBalance(String myXml, String orgId) throws Exception {
        String[] userInfo = getUserInfo();
        SevenU9WeaverLocator locator = new SevenU9WeaverLocator();
        SevenU9WeaverSoap_PortType soap = locator.getSevenU9WeaverSoap();
        return soap.getAccountRemainFromXML(myXml, userInfo[0], userInfo[1], orgId);
    }

    /**
     * 创建凭证
     */
    public static String createVoucher(String myXml, String orgId) throws Exception {
        BaseBean baseBean = new BaseBean();
        String voucherFromXML = "";
        try {
            String[] userInfo = getUserInfo();
            SevenU9WeaverLocator locator = new SevenU9WeaverLocator();
            SevenU9WeaverSoap_PortType soap = locator.getSevenU9WeaverSoap();
            voucherFromXML = soap.createVoucherFromXML(myXml, userInfo[0], userInfo[1], orgId);
        } catch (Exception e) {
            baseBean.writeLog("createVoucher推送凭证异常： " + e);
        }

        return voucherFromXML;
    }

    /**
     * 获取所有科目信息
     */
    public static String getSubject(String myXml) throws Exception {
        SevenU9WeaverLocator locator = new SevenU9WeaverLocator();
        SevenU9WeaverSoap_PortType soap = locator.getSevenU9WeaverSoap();
        return soap.queryAccountFromXML(myXml, "", "", "");
    }

    /**
     * 获取所有客户信息
     */
    public static String getCustomer(String myXml) throws Exception {
        SevenU9WeaverLocator locator = new SevenU9WeaverLocator();
        SevenU9WeaverSoap_PortType soap = locator.getSevenU9WeaverSoap();
        return soap.querySupplierBankAccountFromXML(myXml, "", "", "");
    }

    /**
     * 创建供应商
     *
     * @param myXml 供应商信息
     * @param orgId 组织编码
     */
    public static String createSupplier(String myXml, String orgId) throws Exception {
        String[] userInfo = getUserInfo();
        SevenU9WeaverLocator locator = new SevenU9WeaverLocator();
        SevenU9WeaverSoap_PortType soap = locator.getSevenU9WeaverSoap();
        return soap.createSupplierFromXML(myXml, userInfo[0], userInfo[1], orgId);
    }

    /**
     * 创建客户
     */
    public static String createClient(String myXml, String orgId) throws Exception {
        String[] userInfo = getUserInfo();
        SevenU9WeaverLocator locator = new SevenU9WeaverLocator();
        SevenU9WeaverSoap_PortType soap = locator.getSevenU9WeaverSoap();
        return soap.createCustomerFromXML(myXml, userInfo[0], userInfo[1], orgId);
    }

    /**
     * 创建客户银行账号
     *
     * @param myXml 客户银行账号
     * @param orgId 组织编码
     */
    public static String createCustomerBank(String myXml, String orgId) throws Exception {
        String[] userInfo = getUserInfo();
        SevenU9WeaverLocator locator = new SevenU9WeaverLocator();
        SevenU9WeaverSoap_PortType soap = locator.getSevenU9WeaverSoap();
        return soap.createCustomerBankAccountFromXML(myXml, userInfo[0], userInfo[1], orgId);
    }

    /**
     * 创建客户地点信息
     *
     * @param myXml 客户银行账号
     * @param orgId 组织编码
     */
    public static String createAddress(String myXml, String orgId) throws Exception {
        String[] userInfo = getUserInfo();
        SevenU9WeaverLocator locator = new SevenU9WeaverLocator();
        SevenU9WeaverSoap_PortType soap = locator.getSevenU9WeaverSoap();
        return soap.createAddressFromXML(myXml, userInfo[0], userInfo[1], orgId);
    }

    /**
     * 创建客户联系对象
     *
     * @param myXml 客户银联系对象
     * @param orgId 组织编码
     */
    public static String createContanct(String myXml, String orgId) throws Exception {
        String[] userInfo = getUserInfo();
        SevenU9WeaverLocator locator = new SevenU9WeaverLocator();
        SevenU9WeaverSoap_PortType soap = locator.getSevenU9WeaverSoap();
        return soap.createContanctFromXML(myXml, userInfo[0], userInfo[1], orgId);
    }

    /**
     * 获取登录信息
     */
    private static String[] getUserInfo() {
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("select username, password from uf_u9login");
        recordSet.next();
        return new String[]{recordSet.getString("username"), recordSet.getString("password")};
    }

}
