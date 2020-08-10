/**
 * SevenU9WeaverSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.weavernorth.saiwen.myWebFile;

public interface SevenU9WeaverSoap_PortType extends java.rmi.Remote {

    /**
     * 创建客户
     */
    public String createCustomerFromXML(String customerXml, String u9User, String u9Pwd, String u9Org) throws java.rmi.RemoteException;

    /**
     * 创建供应商
     */
    public String createSupplierFromXML(String supplierXml, String u9User, String u9Pwd, String u9Org) throws java.rmi.RemoteException;

    /**
     * 查询科目
     */
    public String queryAccountFromXML(String xml, String u9User, String u9Pwd, String u9Org) throws java.rmi.RemoteException;

    /**
     * 查询供应商银行账号
     */
    public String querySupplierBankAccountFromXML(String xml, String u9User, String u9Pwd, String u9Org) throws java.rmi.RemoteException;

    /**
     * 销售价格调整单
     */
    public String createSPAdjustFromXML(String xml, String u9User, String u9Pwd, String u9Org) throws java.rmi.RemoteException;

    /**
     * 凭证创建
     */
    public String createVoucherFromXML(String xml, String u9User, String u9Pwd, String u9Org) throws java.rmi.RemoteException;

    /**
     * 创建客户银行账号
     */
    public String createCustomerBankAccountFromXML(String xml, String u9User, String u9Pwd, String u9Org) throws java.rmi.RemoteException;

    /**
     * 创建地址
     */
    public String createAddressFromXML(String xml, String u9User, String u9Pwd, String u9Org) throws java.rmi.RemoteException;

    /**
     * 创建联系对象
     */
    public String createContanctFromXML(String xml, String u9User, String u9Pwd, String u9Org) throws java.rmi.RemoteException;

    /**
     * 创建付款单
     */
    public String createpayBillFromXML(String payXml, String u9User, String u9Pwd, String u9Org) throws java.rmi.RemoteException;

    /**
     * 创建供应商银行账号
     */
    public String createSupplierBankAccountFromXML(String xml, String u9User, String u9Pwd, String u9Org) throws java.rmi.RemoteException;

    /**
     * 获取科目余额
     */
    public String getAccountRemainFromXML(String xml, String u9User, String u9Pwd, String u9Org) throws java.rmi.RemoteException;

    /**
     * 附件上传
     */
    public String upLoadAttachFromXMLLocal(String xml, String u9User, String u9Pwd, String u9Org) throws java.rmi.RemoteException;
}
