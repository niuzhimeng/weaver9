package com.weavernorth.saiwen.action.gyslr;

import com.weaver.general.TimeUtil;
import com.weavernorth.saiwen.myWeb.WebUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 新供应商录入
 * 地址 -> 联系对象 -> 供应商 -> 银行账号
 * 地址编码 在联系对象创建时做参数调用联系对象接口，再以联系对象的编码作为参数调用供应商创建接口，最后以供应商编码为参数调用供应商银行账号接口
 */
public class SupplierInsert extends BaseAction {

    private static final String OA_URL = "http://111.160.2.51:8888/";

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

        this.writeLog("新供应商录入 Start requestid --- " + requestId + "  operatetype --- " + operateType + "   fromTable --- " + tableName);
        try {
            // 查询主表
            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            recordSet.next();
            // 创建类型 1-添加地址；2添加地址与联系人
            String cjlx = recordSet.getString("cjlx");
            boolean addAddress = false;
            boolean addcontacts = false;
            if ("1".equals(cjlx)) {
                addAddress = true;
            } else if ("2".equals(cjlx)) {
                addAddress = true;
                addcontacts = true;
            }
            // 默认币种
            String mrbz = recordSet.getString("mrbz");
            // 账号名称
            String zhmc = recordSet.getString("zhmc");
            // 开户行
            String khh = recordSet.getString("yhmc");
            // 开户行账号
            String khhzh = recordSet.getString("yhzh");
            // 组织名称
            String zuzhimc = recordSet.getString("zuzmc");
            // 注册地址
            String zcdz = recordSet.getString("zcdz").trim();

            // 办公/收货地址
            String bgshdz = recordSet.getString("bgdz").trim();
            // 收货国家
            String shgj = recordSet.getString("shgj").trim();
            // 收货省份
            String shsf = recordSet.getString("shsf").trim();
            // 收货城市
            String shcs = recordSet.getString("shcs").trim();
            // 收货地址编码
            String shdzbm = recordSet.getString("bgdzbm").trim();

            // 附件
            String fj = recordSet.getString("fj").trim();

            // 1、供应商地点信息创建 ====================
            String shLocationCode = "";
            if (addAddress) {
                shLocationCode = createAddress(bgshdz, shgj, shsf, shcs, shdzbm, zuzhimc);
                if (shLocationCode.startsWith("error")) {
                    requestInfo.getRequestManager().setMessageid("1100000");
                    requestInfo.getRequestManager().setMessagecontent("供应商信息录入-【办公/收货地址】异常： " + shLocationCode);
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
                        requestInfo.getRequestManager().setMessagecontent("供应商信息录入-【注册地址】异常： " + zcLocationCode);
                        return "0";
                    }
                }
            }
            // 联系对象创建成功返回的code
            String contactCode = "";
            if (addcontacts) {
                // 2、供应商联系对象创建 ====================
                Element contactRoot = DocumentHelper.createElement("Base_contact_hz");
                contactRoot.addElement("Code").setText(recordSet.getString("lxrbm"));
                contactRoot.addElement("Name").setText(recordSet.getString("lxr"));
                contactRoot.addElement("ContactType").setText(recordSet.getString("lxrlx")); // 公司:1 人:0
                contactRoot.addElement("IsNoticeMail").setText("0");
                contactRoot.addElement("IsNoticeIM").setText("0");

                contactRoot.addElement("IsOrg").setText("0");
                contactRoot.addElement("IsCustomer").setText("0"); // 创建客户时选择此项1，不能同时为1
                contactRoot.addElement("IsSupplier").setText("1"); // 创建供应商时选择此项1
                contactRoot.addElement("IsEmployee").setText("0");
                contactRoot.addElement("ShowName").setText(recordSet.getString("xm"));

                contactRoot.addElement("Im_type").setText("0");
                contactRoot.addElement("Phone_country").setText("");
                contactRoot.addElement("Deft_email").setText(recordSet.getString("yx"));
                contactRoot.addElement("Name_nick").setText("");
                contactRoot.addElement("Name_last").setText("");

                contactRoot.addElement("Name_middle").setText("");
                contactRoot.addElement("Name_first").setText("");
                contactRoot.addElement("Phone").setText(recordSet.getString("bgdh"));
                contactRoot.addElement("Mobile").setText(recordSet.getString("sj"));
                contactRoot.addElement("Address").setText(shLocationCode); // 地址接口返回的编码

                contactRoot.addElement("EnterpriseContact").setText("");
                contactRoot.addElement("Dept").setText("");
                contactRoot.addElement("Title").setText(recordSet.getString("zw"));
                contactRoot.addElement("Gender").setText(recordSet.getString("lxrxb"));

                Document contactDocument = DocumentHelper.createDocument(contactRoot);
                String contactPushXml = contactDocument.asXML();
                this.writeLog("OA供应商联系对象创建推送xml：" + contactPushXml);
                String returnContactStr = WebUtil.createContanct(contactPushXml, zuzhimc);
                this.writeLog("OA供应商联系对象创建 返回信息： " + returnContactStr);

                if (returnContactStr.startsWith("error")) {
                    requestInfo.getRequestManager().setMessageid("1100000");
                    requestInfo.getRequestManager().setMessagecontent("供应商信息录入-【联系人信息】异常： " + returnContactStr);
                    return "0";
                }

                Document returnContact = DocumentHelper.parseText(returnContactStr);
                Element contactRootElement = returnContact.getRootElement();
                String contactState = contactRootElement.elementTextTrim("State");

                if ("success".equalsIgnoreCase(contactState)) {
                    contactCode = contactRootElement.element("Result").element("IDCodeName").elementTextTrim("M_code");
                } else {
                    requestInfo.getRequestManager().setMessageid("1100000");
                    requestInfo.getRequestManager().setMessagecontent("供应商信息录入-【联系人信息】异常： " + returnContactStr);
                    return "0";
                }
                this.writeLog("联系人信息创建成功==================");
            }

            // 3、创建供应商 ====================
            Element root = DocumentHelper.createElement("RequestSupplierList");
            Element SupplierList = root.addElement("SupplierList");
            Element CreateSupplierModel = SupplierList.addElement("CreateSupplierModel");

            // 拼接子级
            Element m_descFlexField = CreateSupplierModel.addElement("M_descFlexField");
            String gysdj = recordSet.getString("gysdj").trim();
            if (!"".equals(gysdj)) {
                m_descFlexField.addElement("M_privateDescSeg4").setText(gysdj); // 供应商等级
            }
            m_descFlexField.addElement("M_privateDescSeg3").setText(recordSet.getString("jzmj")); // 建筑面积
            m_descFlexField.addElement("M_privateDescSeg2").setText(recordSet.getString("qiyxz")); // 企业性质
            String zycp = recordSet.getString("zycp");
            if (!"".equals(zycp)) {
                m_descFlexField.addElement("M_pubDescSeg8").setText(zycp); // 主要产品
            }

            CreateSupplierModel.addElement("M_Turnover").setText(recordSet.getString("yingye")); // 营业额（文本）
            CreateSupplierModel.addElement("M_RegisterCapital").setText(recordSet.getString("zczb")); // 资本额币种
            CreateSupplierModel.addElement("M_tradeCategory").setText(recordSet.getString("hy")); // 行业
            CreateSupplierModel.addElement("M_RegisterLocation").setText(recordSet.getString("zcdzbm")); // 注册地址
            CreateSupplierModel.addElement("M_EmployeeCount").setText(recordSet.getString("ygrs")); // 员工人数

            String clnf = recordSet.getString("clnf").trim();
            if (!"".equals(clnf)) {
                CreateSupplierModel.addElement("M_EstablishDate").setText(recordSet.getString("clnf")); // 成立年份
            }
            CreateSupplierModel.addElement("M_effective").setText("true"); // 是否有效
            CreateSupplierModel.addElement("M_tradeCurrency").setText(recordSet.getString("jybz1")); // 交易币种
            CreateSupplierModel.addElement("M_shortName").setText(recordSet.getString("gysjc")); // 供应商简称
            CreateSupplierModel.addElement("Name").setText(recordSet.getString("kh")); // 供应商全称

            CreateSupplierModel.addElement("M_code").setText(recordSet.getString("gysbm")); // 供应商编码
            CreateSupplierModel.addElement("M_officialLocation").setText(recordSet.getString("bgdzbm")); // 办公地址
            CreateSupplierModel.addElement("M_org").setText(recordSet.getString("zuzmc")); // 组织编码
            String xydm = recordSet.getString("xydm").trim();
            if (!"".equals(xydm)) {
                CreateSupplierModel.addElement("M_corpUnifyCode").setText(xydm); // 统一社会信用代码
            }

            if (!"".equals(contactCode)) {
                CreateSupplierModel.addElement("M_ContactObject").setText(contactCode); // 联系人接口返回编码
            }

            CreateSupplierModel.addElement("M_isPriceListModify").setText("true");
            CreateSupplierModel.addElement("M_isAPConfirmTermEditable").setText("true");
            CreateSupplierModel.addElement("M_isPaymentTermModify").setText("true");
            CreateSupplierModel.addElement("M_isReceiptRuleEditable").setText("true");

            CreateSupplierModel.addElement("M_taxSchedule").setText(recordSet.getString("szh"));
            CreateSupplierModel.addElement("M_Category").setText(recordSet.getString("fl"));
            CreateSupplierModel.addElement("M_receiptRule").setText(recordSet.getString("shyzll"));
            CreateSupplierModel.addElement("M_aPConfirmTerm").setText(recordSet.getString("lztj"));

            Document document = DocumentHelper.createDocument(root);
            String pushXml = document.asXML();
            this.writeLog("OA创建供应商推送xml：" + pushXml);

            // 调用创建接口
            String returnStr = WebUtil.createSupplier(pushXml, zuzhimc);
            this.writeLog("U9返回xml： " + returnStr);

            if (returnStr.startsWith("error")) {
                requestInfo.getRequestManager().setMessageid("1100000");
                requestInfo.getRequestManager().setMessagecontent("供应商信息录入-【联系人信息】异常： " + returnStr);
                return "0";
            }

            Document returnClient = DocumentHelper.parseText(returnStr);
            Element clientRootElement = returnClient.getRootElement();
            String clientState = clientRootElement.elementTextTrim("State");
            // 供应商信息创建成功返回的code
            String supperCode = "";
            if ("success".equalsIgnoreCase(clientState)) {
                supperCode = clientRootElement.element("Result").element("IDCodeName").elementTextTrim("M_code");
            } else {
                requestInfo.getRequestManager().setMessageid("1100000");
                requestInfo.getRequestManager().setMessagecontent("供应商信息录入-【联系人信息】异常： " + returnStr);
                return "0";
            }

            // 4、供应商银行账号创建 ====================
            Element bankRoot = DocumentHelper.createElement("SupplierBankAccount");
            bankRoot.addElement("Org").setText(zuzhimc); // 组织名称
            bankRoot.addElement("Currency").setText(mrbz); // 币种
            bankRoot.addElement("BankAccountName").setText(zhmc); // 账号名称
            bankRoot.addElement("Bank").setText(khh); // 银行
            bankRoot.addElement("BankAccountCode").setText(khhzh); // 账号

            bankRoot.addElement("Supplier").setText(supperCode); // 供应商接口返回的编码
            bankRoot.addElement("IsDefalut").setText("true"); //

            Document bankDocument = DocumentHelper.createDocument(bankRoot);
            String bankPushXml = bankDocument.asXML();
            this.writeLog("OA供应商银行账号创建推送xml：" + bankPushXml);

            String customerBank = WebUtil.createSupplierBankAccountFromXML(bankPushXml, zuzhimc);
            this.writeLog("供应商银行账号创建U9返回xml： " + customerBank);
            if (customerBank.startsWith("error")) {
                requestInfo.getRequestManager().setMessageid("1100000");
                requestInfo.getRequestManager().setMessagecontent("供应商信息录入-【银行账号创建】异常： " + customerBank);
                return "0";
            }

            Document customerBankClient = DocumentHelper.parseText(customerBank);
            Element clientR = customerBankClient.getRootElement();
            String bankAccountState = clientR.elementTextTrim("State");
            if (!"success".equalsIgnoreCase(bankAccountState)) {
                requestInfo.getRequestManager().setMessageid("1100000");
                requestInfo.getRequestManager().setMessagecontent("供应商信息录入-【银行账号创建】异常： " + clientR.elementTextTrim("Msg"));
                return "0";
            }

            if (!"".equals(fj)) {
                this.writeLog("上传附件开始======= " + fj);
                Element attachList = DocumentHelper.createElement("AttachList");
                Element attach = attachList.addElement("Attach");

                String pathSql = "SELECT im.imagefileid,im.imagefilename,im.filerealpath FROM ImageFile im LEFT JOIN " +
                        "DocImageFile df ON df.imagefileid = im.imagefileid WHERE df.docid IN ( " + fj + " )";
                this.writeLog("拼接后sql： " + pathSql);
                recordSet.executeQuery(pathSql);
                String dataPath = TimeUtil.getCurrentDateString().substring(0, 7);
                while (recordSet.next()) {
                    String imagefilename = recordSet.getString("imagefilename");
                    String filerealpath = recordSet.getString("filerealpath");
                    String savePath = "D:/WEAVER/ecology/";
                    String savePathTwo = "TmpU9File/" + dataPath + "/" + imagefilename;
                    unZip(filerealpath, savePath + savePathTwo);

                    Element docAttachment = attach.addElement("DocAttachment");
                    docAttachment.addElement("FileUrl").setText(OA_URL + savePathTwo);
                    docAttachment.addElement("IniFileName").setText(imagefilename);
                    docAttachment.addElement("EntityName").setText("supplier");
                    docAttachment.addElement("DocCode").setText(supperCode);
                }
                Document attachListObj = DocumentHelper.createDocument(attachList);
                String attachListObjXml = attachListObj.asXML();
                this.writeLog("OA供应商附件推送xml：" + attachListObjXml);

                String fJReturn = WebUtil.upLoadAttachFromXMLLocal(attachListObjXml, zuzhimc);
                this.writeLog("A供应商附件推送U9返回xml： " + fJReturn);
                if (fJReturn.startsWith("error")) {
                    requestInfo.getRequestManager().setMessageid("1100000");
                    requestInfo.getRequestManager().setMessagecontent("供应商信息录入-【附件推送U9】异常： " + fJReturn);
                    return "0";
                }

                Document fJReturnClient = DocumentHelper.parseText(fJReturn);
                Element fJReturnR = fJReturnClient.getRootElement();
                String fJReturnRState = fJReturnR.elementTextTrim("State");
                if (!"success".equalsIgnoreCase(fJReturnRState)) {
                    requestInfo.getRequestManager().setMessageid("1100000");
                    requestInfo.getRequestManager().setMessagecontent("供应商信息录入-【附件推送U9】异常： " + clientR.elementTextTrim("Msg"));
                    return "0";
                }
            }

            this.writeLog("新供应商录入 End ===============");
        } catch (Exception e) {
            this.writeLog("新供应商录入 异常： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("新供应商录入 异常： " + e);
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
            this.writeLog("OA供应商位置创建推送xml：" + locationPushXml);
            String returnAddress = WebUtil.createAddress(locationPushXml, zuzhimc);
            this.writeLog("OA供应商位置创建 返回信息： " + returnAddress);

            if (returnAddress.startsWith("error")) {
                this.writeLog("供应商信息录入-【地点信息】异常： " + returnAddress);
                return returnAddress;
            }

            Document returnDdDocument = DocumentHelper.parseText(returnAddress);
            Element ddRootElement = returnDdDocument.getRootElement();
            String ddState = ddRootElement.elementTextTrim("State");

            if ("success".equalsIgnoreCase(ddState)) {
                locationCode = ddRootElement.element("Result").element("IDCodeName").elementTextTrim("M_code");
            } else {
                this.writeLog("供应商信息录入-【地点信息】异常： " + returnAddress);
                return "error: " + returnAddress;
            }
            this.writeLog("地址信息创建成功==================");
        } catch (Exception e) {
            this.writeLog("创建地址异常： " + e);
        }
        return locationCode;
    }

    private void unZip(String srcFilePath, String destDirPath) throws RuntimeException, IOException {
        System.setProperty("sun.jnu.encoding", "UTF-8");
        File srcFile = new File(srcFilePath);
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
        }
        // 开始解压
        ZipFile zipFile = new ZipFile(srcFile);

        Enumeration<?> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
            File targetFile = new File(destDirPath);
            // 保证这个文件的父文件夹必须要存在
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            // 将压缩文件内容写入到这个文件中
            InputStream is = zipFile.getInputStream(entry);
            FileOutputStream fos = new FileOutputStream(targetFile);
            int len;
            byte[] buf = new byte[2048];
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            // 关流顺序，先打开的后关闭
            fos.close();
            is.close();
        }

        if (zipFile != null) {
            try {
                zipFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
