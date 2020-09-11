package com.weavernorth.saiwen.action.khlr;

import com.weavernorth.saiwen.myWeb.WebUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

/**
 * 新客户录入
 * 地址 -> 联系对象 -> 客户 -> 银行账号
 * 地址编码 在联系对象创建时做参数调用联系对象接口，再以联系对象的编码作为参数调用客户创建接口，最后以客户编码为参数调用客户银行账号接口
 */
public class ClientInsert extends BaseAction {

    @Override
    public String execute(RequestInfo requestInfo) {
        String requestId = requestInfo.getRequestid();
        String operateType = requestInfo.getRequestManager().getSrc();
        int formId = requestInfo.getRequestManager().getFormid();
        String tableName = "";
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("SELECT tablename FROM workflow_bill WHERE id = '" + formId + "'");
        if (recordSet.next()) {
            tableName = recordSet.getString("tablename");
        }

        this.writeLog("新客户录入 Start requestid --- " + requestId + "  operatetype --- " + operateType + "   fromTable --- " + tableName);
        try {
            // 查询主表
            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            if (recordSet.next()) {
                // 默认币种
                String mrbz = recordSet.getString("mrbz");
                // 账号名称
                String zhmc = recordSet.getString("zhmc");
                // 开户行
                String khh = recordSet.getString("kaihh");
                // 开户行账号
                String khhzh = recordSet.getString("khhzh");
                // 组织名称
                String zuzhimc = recordSet.getString("zuzhimc");
                // 注册地址
                String zcdz = recordSet.getString("zcdz").trim();

                // 办公/收货地址
                String bgshdz = recordSet.getString("bgshdz").trim();
                // 收货国家
                String shgj = recordSet.getString("shgj").trim();
                // 收货省份
                String shsf = recordSet.getString("shsf").trim();
                // 收货城市
                String shcs = recordSet.getString("shcs").trim();
                // 收货地址编码
                String shdzbm = recordSet.getString("shdzbm").trim();

                // 1、客户地点信息创建 ====================
                String shLocationCode = createAddress(bgshdz, shgj, shsf, shcs, shdzbm, zuzhimc);
                if (shLocationCode.startsWith("error")) {
                    requestInfo.getRequestManager().setMessageid("1100000");
                    requestInfo.getRequestManager().setMessagecontent("客户信息录入-【办公/收货地址】异常： " + shLocationCode);
                    return "0";
                }
                // 如果两个地址不相等，创建注册地址
                String zcLocationCode = "";
                if (!zcdz.equalsIgnoreCase(bgshdz)) {
                    // 注册国家
                    String zcgj = recordSet.getString("zcgj").trim();
                    // 注册省份
                    String zcsf = recordSet.getString("zcsf").trim();
                    // 注册城市
                    String zccs = recordSet.getString("zccs").trim();
                    // 注册地址编码
                    String zcdzbm = recordSet.getString("zcdzbm").trim();
                    zcLocationCode = createAddress(zcdz, zcgj, zcsf, zccs, zcdzbm, zuzhimc);
                    if (zcLocationCode.startsWith("error")) {
                        requestInfo.getRequestManager().setMessageid("1100000");
                        requestInfo.getRequestManager().setMessagecontent("客户信息录入-【注册地址】异常： " + zcLocationCode);
                        return "0";
                    }
                }

                // 2、客户联系对象创建 ====================
                Element contactRoot = DocumentHelper.createElement("Base_contact_hz");
                contactRoot.addElement("Code").setText(recordSet.getString("lxrbm"));
                contactRoot.addElement("Name").setText(recordSet.getString("xm"));
                contactRoot.addElement("ContactType").setText(recordSet.getString("lxrlx")); // 公司:1 人:0
                contactRoot.addElement("IsNoticeMail").setText("0");
                contactRoot.addElement("IsNoticeIM").setText("0");

                contactRoot.addElement("IsOrg").setText("0");
                contactRoot.addElement("IsCustomer").setText("1"); // 创建客户时选择此项1，不能同时为1
                contactRoot.addElement("IsSupplier").setText("0"); // 创建供应商时选择此项1
                contactRoot.addElement("IsEmployee").setText("0");
                contactRoot.addElement("ShowName").setText(recordSet.getString("xm"));

                contactRoot.addElement("Im_type").setText("0");
                contactRoot.addElement("Phone_country").setText("");
                contactRoot.addElement("Deft_email").setText(recordSet.getString("email"));
                contactRoot.addElement("Name_nick").setText("");
                contactRoot.addElement("Name_last").setText("");

                contactRoot.addElement("Name_middle").setText("");
                contactRoot.addElement("Name_first").setText("");
                contactRoot.addElement("Phone").setText(recordSet.getString("bgdh"));
                contactRoot.addElement("Mobile").setText(recordSet.getString("yddh"));
                contactRoot.addElement("Address").setText(shLocationCode); // 地址接口返回的编码

                contactRoot.addElement("EnterpriseContact").setText("");
                contactRoot.addElement("Dept").setText("");
                contactRoot.addElement("Title").setText("");
                contactRoot.addElement("Gender").setText(recordSet.getString("xb"));

                Document contactDocument = DocumentHelper.createDocument(contactRoot);
                String contactPushXml = contactDocument.asXML();
                this.writeLog("OA客户联系对象创建推送xml：" + contactPushXml);
                String returnContactStr = WebUtil.createContanct(contactPushXml, zuzhimc);
                this.writeLog("OA客户联系对象创建 返回信息： " + returnContactStr);

                if (returnContactStr.startsWith("error")) {
                    requestInfo.getRequestManager().setMessageid("1100000");
                    requestInfo.getRequestManager().setMessagecontent("客户信息录入-【联系人信息】异常： " + returnContactStr);
                    return "0";
                }

                Document returnContact = DocumentHelper.parseText(returnContactStr);
                Element contactRootElement = returnContact.getRootElement();
                String contactState = contactRootElement.elementTextTrim("State");
                // 联系对象创建成功返回的code
                String contactCode = "";
                if ("success".equalsIgnoreCase(contactState)) {
                    contactCode = contactRootElement.element("Result").element("IDCodeName").elementTextTrim("M_code");
                } else {
                    requestInfo.getRequestManager().setMessageid("1100000");
                    requestInfo.getRequestManager().setMessagecontent("客户信息录入-【联系人信息】异常： " + returnContactStr);
                    return "0";
                }
                this.writeLog("联系人信息创建成功==================");

                // 3、创建客户 ====================
                Element root = DocumentHelper.createElement("RequestCustomerList");
                Element customerList = root.addElement("CustomerList");
                Element createSupplierModel = customerList.addElement("CreateCustomerModel");
                // 拼接子级
                Element mDescflexfield = createSupplierModel.addElement("M_descFlexField");
                mDescflexfield.addElement("M_privateDescSeg4").setText(recordSet.getString("kprq")); // 开票日期
                mDescflexfield.addElement("M_privateDescSeg2").setText(recordSet.getString("dh")); // 电话
                createSupplierModel.addElement("M_department").setText(recordSet.getString("bmbm")); // =========
                createSupplierModel.addElement("M_Turnover").setText(recordSet.getString("yingye")); // ===========
                createSupplierModel.addElement("M_RegisterCapital").setText(recordSet.getString("zhucje")); // ========
                createSupplierModel.addElement("M_tradeCategory").setText(recordSet.getString("khhy")); // 行业
                String tczcdz = ""; // 填充的注册地址
                if (!"".equalsIgnoreCase(zcLocationCode)) {
                    tczcdz = zcLocationCode;
                } else {
                    tczcdz = shLocationCode;
                }
                createSupplierModel.addElement("M_RegisterLocation").setText(tczcdz); // 注册地址
                createSupplierModel.addElement("M_effective").setText("true"); // 固定值
                createSupplierModel.addElement("M_tradeCurrency").setText(recordSet.getString("jhbz")); // 交货币种
                createSupplierModel.addElement("M_shortName").setText(recordSet.getString("khjc")); // 客户简称
                createSupplierModel.addElement("Name").setText(recordSet.getString("khqv")); // 公司名称

                createSupplierModel.addElement("M_code").setText(recordSet.getString("khbm")); // 客户编码
                createSupplierModel.addElement("M_officialLocation").setText(recordSet.getString("shdzbm")); // 办公/收货地址
                createSupplierModel.addElement("M_org").setText(recordSet.getString("zuzhimc")); // 所属组织
                createSupplierModel.addElement("M_saleType").setText(recordSet.getString("yxlx")); // 允许限销类型
                createSupplierModel.addElement("M_aRConfirmTerm").setText(recordSet.getString("lztj")); // 立账条件

                createSupplierModel.addElement("M_recervalTerm").setText("001"); // 固定值001
                createSupplierModel.addElement("M_taxSchedule").setText(recordSet.getString("szh")); // 税组合编码
                createSupplierModel.addElement("M_DefaultContact").setText(contactCode); // 联系人接口返回的编码
                createSupplierModel.addElement("M_Saleser").setText(recordSet.getString("xsy")); // 销售员
                // 固定传值
                createSupplierModel.addElement("M_commission").setText("0");
                createSupplierModel.addElement("M_commissionType").setText("-1");
                createSupplierModel.addElement("M_isCreditCheck").setText("false");
                createSupplierModel.addElement("M_isARCfmModify").setText("true");
                createSupplierModel.addElement("M_isRecTermModify").setText("true");
                createSupplierModel.addElement("M_isShipmentModify").setText("true");
                createSupplierModel.addElement("M_isPriceListModify").setText("true");

                Document document = DocumentHelper.createDocument(root);
                String pushXml = document.asXML();

                this.writeLog("OA创建客户推送xml：" + pushXml);

                // 调用客户创建接口
                String returnStr = WebUtil.createClient(pushXml, zuzhimc);
                this.writeLog("OA创建客户U9返回xml： " + returnStr);

                if (returnStr.startsWith("error")) {
                    requestInfo.getRequestManager().setMessageid("1100000");
                    requestInfo.getRequestManager().setMessagecontent("客户信息录入-【联系人信息】异常： " + returnStr);
                    return "0";
                }

                Document returnClient = DocumentHelper.parseText(returnStr);
                Element clientRootElement = returnClient.getRootElement();
                String clientState = clientRootElement.elementTextTrim("State");
                // 客户信息创建成功返回的code
                String clientCode = "";
                if ("success".equalsIgnoreCase(clientState)) {
                    clientCode = clientRootElement.element("Result").element("IDCodeName").elementTextTrim("M_code");
                } else {
                    requestInfo.getRequestManager().setMessageid("1100000");
                    requestInfo.getRequestManager().setMessagecontent("客户信息录入-【联系人信息】异常： " + returnContactStr);
                    return "0";
                }

                // 4、客户银行账号创建 ====================
                Element bankRoot = DocumentHelper.createElement("CustomerBankAccount");
                bankRoot.addElement("Org").setText(zuzhimc); // 组织名称
                bankRoot.addElement("Currency").setText(mrbz); // 币种
                bankRoot.addElement("BankAccountName").setText(zhmc); // 账号名称
                bankRoot.addElement("Bank").setText(khh); // 银行
                bankRoot.addElement("BankAccountCode").setText(khhzh); // 账号

                bankRoot.addElement("Customer").setText(clientCode); // 客户接口返回的编码
                bankRoot.addElement("IsDefalut").setText("true"); //

                Document bankDocument = DocumentHelper.createDocument(bankRoot);
                String bankPushXml = bankDocument.asXML();
                this.writeLog("OA客户银行账号创建推送xml：" + bankPushXml);

                String customerBank = WebUtil.createCustomerBank(bankPushXml, zuzhimc);
                this.writeLog("客户银行账号创建U9返回xml： " + customerBank);
                if (customerBank.startsWith("error")) {
                    requestInfo.getRequestManager().setMessageid("1100000");
                    requestInfo.getRequestManager().setMessagecontent("客户信息录入-【银行账号创建】异常： " + customerBank);
                    return "0";
                }

                Document customerBankClient = DocumentHelper.parseText(customerBank);
                Element clientR = customerBankClient.getRootElement();
                String bankAccountState = clientR.elementTextTrim("State");
                if (!"success".equalsIgnoreCase(bankAccountState)) {
                    requestInfo.getRequestManager().setMessageid("1100000");
                    requestInfo.getRequestManager().setMessagecontent("客户信息录入-【银行账号创建】异常： " + clientR.elementTextTrim("Msg"));
                    return "0";
                }
            }
            this.writeLog("新客户录入 End ===============");
        } catch (Exception e) {
            this.writeLog("新客户录入 异常： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("新客户录入 异常： " + e);
            return "0";
        }

        return "1";
    }

    private String createAddress(String bgshdz, String shgj, String shsf, String shcs, String shdzbm, String zuzhimc) {
        // 地点信息创建成功返回的code
        String locationCode = "";
        try {
            Element locationRoot = DocumentHelper.createElement("Base_location_hz");
            locationRoot.addElement("Country").setText(shgj);
            locationRoot.addElement("Province").setText(shsf);
            locationRoot.addElement("City").setText(shcs);
            locationRoot.addElement("Shortname").setText(bgshdz);
            locationRoot.addElement("Code").setText(shdzbm);

            locationRoot.addElement("Name").setText(bgshdz);
            locationRoot.addElement("Address1").setText(bgshdz);

            Document locationDocument = DocumentHelper.createDocument(locationRoot);
            String locationPushXml = locationDocument.asXML();
            this.writeLog("OA客户位置创建推送xml：" + locationPushXml);
            String returnAddress = WebUtil.createAddress(locationPushXml, zuzhimc);
            this.writeLog("OA客户位置创建 返回信息： " + returnAddress);

            if (returnAddress.startsWith("error")) {
                this.writeLog("客户信息录入-【地点信息】异常： " + returnAddress);
                return returnAddress;
            }

            Document returnDdDocument = DocumentHelper.parseText(returnAddress);
            Element ddRootElement = returnDdDocument.getRootElement();
            String ddState = ddRootElement.elementTextTrim("State");

            if ("success".equalsIgnoreCase(ddState)) {
                locationCode = ddRootElement.element("Result").element("IDCodeName").elementTextTrim("M_code");
            } else {
                this.writeLog("客户信息录入-【地点信息】异常： " + returnAddress);
                return "error: " + returnAddress;
            }
            this.writeLog("地址信息创建成功==================");
        } catch (Exception e) {
            this.writeLog("创建地址异常： " + e);
        }
        return locationCode;
    }
}
