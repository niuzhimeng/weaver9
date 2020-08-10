package com.weavernorth.saiwen.action.grjk;

import com.weaver.general.TimeUtil;
import com.weaver.general.Util;
import com.weavernorth.saiwen.myWeb.WebUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

/**
 * 个人借款
 */
public class PersonalLoan extends BaseAction {

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

        this.writeLog("个人借款 Start requestid --- " + requestId + "  operatetype --- " + operateType + "   fromTable --- " + tableName);
        try {
            // 查询主表
            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            recordSet.next();

            String yh = recordSet.getString("khh"); // 银行
            String yhzh = recordSet.getString("fkzh"); // 银行账号
            String fyssgs = recordSet.getString("fyssgs"); // 费用所属公司

            String workCode = recordSet.getString("ygbm"); // 人员编码
            String lcbh = recordSet.getString("lcbh"); // 流程编号
            String grjksy = recordSet.getString("grjksy"); // 借款事由
            String fkfs = recordSet.getString("fkfs"); // 付款方式
            String zhangb = recordSet.getString("zhangb"); // 账簿编码

            String fyssbm = recordSet.getString("fyssbm"); // 费用所属部门
            String zy = lcbh + "|" + grjksy; // 摘要
            double jkje = Util.getDoubleValue(recordSet.getString("jkje"), 0); // 借款金额
            this.writeLog("流程编号: " + lcbh + ", 借款事由: " + grjksy + ", 借款金额： " + jkje + ", 付款方式： " + fkfs +
                    ", 费用所属公司： " + fyssgs + "员工编码： " + workCode);

            Element root = DocumentHelper.createElement("GLVoucherList");
            Element SupplierList = root.addElement("VoucherList");
            Element glVoucherHead = SupplierList.addElement("GLVoucherHead");
            Element m_entries_arrayLines = glVoucherHead.addElement("M_entries_ArrayLines");

            String jfCode = "";
            double jfMoney = 0;
            String dfCode = "";
            double dfMoney = 0;
            if ("0".equals(fkfs)) {
                // 现金
                jfCode = "122101|0|0|" + fyssbm + "|" + workCode + "|0|0|0|0|0";
                jfMoney = jkje;
                dfCode = "100101|0|0|0|0|0|0|0|0|0";
                dfMoney = jkje;
            } else if ("1".equals(fkfs)) {
                // 电汇-人民币
                jfCode = "122101|0|0|" + fyssbm + "|" + workCode + "|0|0|0|0|0";
                jfMoney = jkje;
                dfCode = "100201|0|0|0|0|" + yh + "|" + yhzh + "|0|0|0";
                dfMoney = jkje;
            } else if ("2".equals(fkfs)) {
                // 电汇-外币
                jfCode = "122101|0|0|" + fyssbm + "|" + workCode + "|0|0|0|0|0";
                jfMoney = jkje;
                dfCode = "100202|0|0|0|0|" + yh + "|" + yhzh + "|0|0|0";
                dfMoney = jkje;
            }
//            this.writeLog("替换前借方编码： " + jfCode + ", 贷方编码： " + dfCode);
//            // 如果为苏州，则去掉借贷方编码中所有的|0
//            jfCode = SwUtil.deleteZero(jfCode, fyssgs);
//            dfCode = SwUtil.deleteZero(dfCode, fyssgs);
//            this.writeLog("替换后借方编码： " + jfCode + ", 贷方编码： " + dfCode);

            this.writeLog("借方拼接开始===========");
            Element glVoucherLine = m_entries_arrayLines.addElement("GLVoucherLine");
            glVoucherLine.addElement("M_amountDr").setText("0"); // 固定值0
            glVoucherLine.addElement("M_amountCr").setText("0"); // 固定值0
            glVoucherLine.addElement("M_enteredDr").setText(String.valueOf(jfMoney)); // 借方金额(原币)
            glVoucherLine.addElement("M_enteredCr").setText("0"); // 贷方金额(原币)
            glVoucherLine.addElement("M_accountedDr").setText(String.valueOf(jfMoney)); // 借方金额(本币)

            glVoucherLine.addElement("M_accountedCr").setText("0"); // 贷方金额(本币)
            glVoucherLine.addElement("M_currency").setText("C001"); // 币种编码人民币（C001）
            glVoucherLine.addElement("M_account").setText(jfCode); // 科目编码
            glVoucherLine.addElement("M_abstracts").setText(zy); // 摘要

            this.writeLog("贷方拼接开始===========");
            Element glVoucherLinedf = m_entries_arrayLines.addElement("GLVoucherLine");
            glVoucherLinedf.addElement("M_amountDr").setText("0"); // 固定值0
            glVoucherLinedf.addElement("M_amountCr").setText("0"); // 固定值0
            glVoucherLinedf.addElement("M_enteredDr").setText("0"); // 借方金额(原币)
            glVoucherLinedf.addElement("M_enteredCr").setText(String.valueOf(dfMoney)); // 贷方金额(原币)
            glVoucherLinedf.addElement("M_accountedDr").setText("0"); // 借方金额(本币)

            glVoucherLinedf.addElement("M_accountedCr").setText(String.valueOf(dfMoney)); // 贷方金额(本币)
            glVoucherLinedf.addElement("M_currency").setText("C001"); // 币种编码人民币（C001）
            glVoucherLinedf.addElement("M_account").setText(dfCode); // 科目编码
            glVoucherLinedf.addElement("M_abstracts").setText(zy); // 摘要

            //================ 单据头
            String currentDateString = TimeUtil.getCurrentDateString();
            glVoucherHead.addElement("M_voucherCategory").setText("01"); // 单据类型 编码（01 记账凭证）
            glVoucherHead.addElement("M_sOB").setText(zhangb); // 账簿 编码
            glVoucherHead.addElement("M_postedPeriod").setText(currentDateString.substring(0, 7)); // 记账区间
            glVoucherHead.addElement("M_attachmentCount").setText("0"); // 固定值0
            glVoucherHead.addElement("M_createDate").setText(currentDateString); // 凭证创日期时间

            Document document = DocumentHelper.createDocument(root);
            String pushXml = document.asXML();
            this.writeLog("个人借款凭证推送xml： " + pushXml);

            // 调用接口创建凭证
            String voucherReturn = WebUtil.createVoucher(pushXml, fyssgs);
            this.writeLog("创建凭证返回信息： " + voucherReturn);
            Document doc = DocumentHelper.parseText(voucherReturn);
            Element rootElt = doc.getRootElement();
            String state = rootElt.elementTextTrim("State");
            String msg = rootElt.elementTextTrim("Msg");
            if (!"SUCCESS".equalsIgnoreCase(state)) {
                this.writeLog("凭证创建失败： " + voucherReturn);
                requestInfo.getRequestManager().setMessageid("110000");
                requestInfo.getRequestManager().setMessagecontent("创建凭证返回信息： " + msg);
                return "0";
            }

            this.writeLog("个人借款 End =============== " + msg);
        } catch (Exception e) {
            this.writeLog("个人借款 异常： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("个人借款 异常： " + e);
            return "0";
        }

        return "1";
    }
}
