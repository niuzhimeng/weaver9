package com.mytest;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;

public class MyDom4j {

    @Test
    public void test4() {
        Element root = DocumentHelper.createElement("Iss_Itreasury");
        Element instrReq = root.addElement("InstrReq");
        instrReq.addElement("OperationType").setText("");
        instrReq.addElement("SystemID").setText("");
        instrReq.addElement("EnterpriseCode").setText("");
        instrReq.addElement("OperatorCode").setText("");

        // 拼接明细
        for (int i = 0; i < 3; i++) {
            Element instrContent = instrReq.addElement("InstrContent");
            instrContent.addElement("ApplyCode").setText("40");
            instrContent.addElement("TransType").setText("");
            instrContent.addElement("Amount").setText("6601080000");
            instrContent.addElement("ExcuteDate").setText("J0");
            instrContent.addElement("ExcuteTime").setText("N100000019");

            instrContent.addElement("ClientCode").setText("N1000000");
            instrContent.addElement("PayerAcctNo").setText("");
            instrContent.addElement("PayeeAcctNo").setText("100.00");
            instrContent.addElement("PayeeAcctName").setText("");
            instrContent.addElement("RemitBankName").setText("");

            instrContent.addElement("RemitProvince").setText("孙思宇_123");
            instrContent.addElement("RemitCity").setText("");
            instrContent.addElement("RemitBankCode").setText("");
            instrContent.addElement("ConfirmUser").setText("");
            instrContent.addElement("ConfirmTime").setText("");

            instrContent.addElement("Note").setText("");
            instrContent.addElement("Abstract").setText("2020-08-24");
            instrContent.addElement("Capticalpurpose_id").setText("");
        }

        Document document = DocumentHelper.createDocument(root);
        String pushXml = document.asXML();
        System.out.println(pushXml);
    }

}
