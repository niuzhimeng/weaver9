package com.weavernorth.saiwen.action.fybx;

import com.alibaba.fastjson.JSONObject;
import com.weaver.general.TimeUtil;
import com.weaver.general.Util;
import com.weavernorth.saiwen.myWeb.WebUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

import java.util.HashMap;
import java.util.Map;

/**
 * 费用报销
 */
public class Reimbursement extends BaseAction {

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

        this.writeLog("费用报销 Start requestid --- " + requestId + "  operatetype --- " + operateType + "   fromTable --- " + tableName);
        try {
            // 查询主表
            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            recordSet.next();
            String mainid = recordSet.getString("id");
            String workCode = recordSet.getString("ygbm"); // 人员编码
            String lcbh = recordSet.getString("lcbh"); // 流程编号
            String fybxsm = recordSet.getString("fybxsm"); // 报销说明
            String fyssgs = recordSet.getString("fyssgs"); // 费用所属公司
            String zhangbbm = recordSet.getString("zhangbbm"); // 账簿编码

            String fyssbm = recordSet.getString("fyssbm"); // 费用所属部门
            String fkfs = recordSet.getString("fkfsxz"); // 付款方式
            double cxhj = Util.getDoubleValue(recordSet.getString("cxhj"), 0); // 税额合计
            double cjkje = Util.getDoubleValue(recordSet.getString("cjkje"), 0); // 冲借款金额
            double bxhj = Util.getDoubleValue(recordSet.getString("bxhj"), 0); // 价税合计总额

            double fkje = Util.getDoubleValue(recordSet.getString("fkje"), 0); // 付款金额
            String gys = recordSet.getString("gys"); // 供应商
            String zy = lcbh + "|" + fybxsm; // 摘要
            this.writeLog("付款方式： " + fkfs + ", 账簿编码: " + zhangbbm + ", 费用所属公司： " + fyssgs);
            this.writeLog("税额合计： " + cxhj + ", 冲借款金额： " + cjkje + " 价税合计总额: " + bxhj + " 付款金额: " + fkje);

            // 明细字段部分
            recordSet.executeQuery("select * from " + tableName + "_dt1 where mainid = '" + mainid + "'");
            Element root = DocumentHelper.createElement("GLVoucherList");
            Element SupplierList = root.addElement("VoucherList");
            Element glVoucherHead = SupplierList.addElement("GLVoucherHead");
            // 拼接明细
            Element m_entries_arrayLines = glVoucherHead.addElement("M_entries_ArrayLines");
            int sfzx = 0;
            while (recordSet.next()) {
                String fykm = recordSet.getString("fykm"); // 费用科目
                double wsje = Util.getDoubleValue(recordSet.getString("wsje"), 0); // 未税金额
                // 借方科目编码拼接规则
                String jfCode = "";
                if ("660306".equals(fykm) || "66023354".equals(fykm) || "51012501".equals(fykm)) {
                    jfCode = fykm + "|0|0|0|0|0|0|0|0|0";
                } else {
                    jfCode = fykm + "|0|0|" + fyssbm + "|0|0|0|0|0|0";
                }
                //jfCode = SwUtil.deleteZero(jfCode, fyssgs);
                //================ 借方
                Element glVoucherLine = m_entries_arrayLines.addElement("GLVoucherLine");
                glVoucherLine.addElement("M_amountDr").setText("0"); // 固定值0
                glVoucherLine.addElement("M_amountCr").setText("0"); // 固定值0
                glVoucherLine.addElement("M_enteredDr").setText(String.valueOf(wsje)); // 借方金额(原币)
                glVoucherLine.addElement("M_enteredCr").setText("0"); // 贷方金额(原币)
                glVoucherLine.addElement("M_accountedDr").setText(String.valueOf(wsje)); // 借方金额(本币)

                glVoucherLine.addElement("M_accountedCr").setText("0"); // 贷方金额(本币)
                glVoucherLine.addElement("M_currency").setText("C001"); // 币种编码人民币（C001）
                glVoucherLine.addElement("M_account").setText(jfCode); // 科目编码
                glVoucherLine.addElement("M_abstracts").setText(zy); // 摘要

                if (cxhj > 0 && sfzx == 0) {
                    // 如果税额合计 > 0 ，多传一个借方
                    String jfCode1 = "22210101|0|0|0|0|0|0|0|0|0";
                    //jfCode1 = SwUtil.deleteZero(jfCode1, fyssgs);
                    Element glVoucherLine1 = m_entries_arrayLines.addElement("GLVoucherLine");
                    glVoucherLine1.addElement("M_amountDr").setText("0"); // 固定值0
                    glVoucherLine1.addElement("M_amountCr").setText("0"); // 固定值0
                    glVoucherLine1.addElement("M_enteredDr").setText(String.valueOf(cxhj)); // 借方金额(原币)
                    glVoucherLine1.addElement("M_enteredCr").setText("0"); // 贷方金额(原币)
                    glVoucherLine1.addElement("M_accountedDr").setText(String.valueOf(cxhj)); // 借方金额(本币)

                    glVoucherLine1.addElement("M_accountedCr").setText("0"); // 贷方金额(本币)
                    glVoucherLine1.addElement("M_currency").setText("C001"); // 币种编码人民币（C001）
                    glVoucherLine1.addElement("M_account").setText(jfCode1); // 科目编码
                    glVoucherLine1.addElement("M_abstracts").setText(zy); // 摘要
                    sfzx++;
                }
            }

            // 贷方编码 - 贷方金额
            Map<String, Double> dfMap = new HashMap<String, Double>();
            if ("0".equals(fkfs)) {
                // 现金
                if (bxhj > cjkje && cjkje > 0) {
                    dfMap.put("122101|0|0|" + fyssbm + "|" + workCode + "|0|0|0|0|0", cjkje);
                    dfMap.put("224106|0|0|0|0|0|0|0|0|0|0", fkje);
                } else if (cjkje == bxhj && fkje == bxhj && bxhj > 0) {
                    dfMap.put("122101|0|0|" + fyssbm + "|" + workCode + "|0|0|0|0|0", cjkje);
                } else if (cjkje == 0) {
                    dfMap.put("224106|0|0|0|0|0|0|0|0|0|0", bxhj);
                }
            } else if ("3".equals(fkfs)) {
                // 电汇-员工
                if (bxhj > cjkje && cjkje > 0) {
                    dfMap.put("122101|0|0|" + fyssbm + "|" + workCode + "|0|0|0|0|0", cjkje);
                    dfMap.put("224106|0|0|0|0|0|0|0|0|0|0", fkje);
                } else if (cjkje == bxhj && bxhj > 0 && fkje == 0) {
                    dfMap.put("122101|0|0|" + fyssbm + "|" + workCode + "|0|0|0|0|0", cjkje);
                } else if (cjkje == 0) {
                    dfMap.put("224106|0|0|0|0|0|0|0|0|0|0", bxhj);
                }
            } else if ("1".equals(fkfs)) {
                // 电汇-人民币
                if (bxhj > cjkje && cjkje > 0) {
                    dfMap.put("112302|0|" + gys + "|0|" + workCode + "|0|0|0|0|0", cjkje);
                    dfMap.put("220202|0|" + gys + "|0|0|0|0|0|0|0", fkje);
                } else if (cjkje == bxhj && fkje == bxhj && bxhj > 0) {
                    dfMap.put("112302|0|" + gys + "|0|" + workCode + "|0|0|0|0|0", cjkje);
                } else if (cjkje == 0) {
                    dfMap.put("220202|0|" + gys + "|0|0|0|0|0|0|0", bxhj);
                }
            }

            this.writeLog("贷方拼接开始=========== 贷方数量" + dfMap.size() + ", " + JSONObject.toJSONString(dfMap));
            for (Map.Entry<String, Double> entry : dfMap.entrySet()) {
                String dfCode = entry.getKey();
                //dfCode = SwUtil.deleteZero(dfCode, fyssgs);
                Element glVoucherLinedf = m_entries_arrayLines.addElement("GLVoucherLine");
                glVoucherLinedf.addElement("M_amountDr").setText("0"); // 固定值0
                glVoucherLinedf.addElement("M_amountCr").setText("0"); // 固定值0
                glVoucherLinedf.addElement("M_enteredDr").setText("0"); // 借方金额(原币)
                glVoucherLinedf.addElement("M_enteredCr").setText(String.valueOf(entry.getValue())); // 贷方金额(原币)
                glVoucherLinedf.addElement("M_accountedDr").setText("0"); // 借方金额(本币)

                glVoucherLinedf.addElement("M_accountedCr").setText(String.valueOf(entry.getValue())); // 贷方金额(本币)
                glVoucherLinedf.addElement("M_currency").setText("C001"); // 币种编码人民币（C001）
                glVoucherLinedf.addElement("M_account").setText(dfCode); // 科目编码
                glVoucherLinedf.addElement("M_abstracts").setText(zy); // 摘要
            }

            //================ 单据头
            String currentDateString = TimeUtil.getCurrentDateString();
            glVoucherHead.addElement("M_voucherCategory").setText("01"); // 单据类型 编码（01 记账凭证）
            glVoucherHead.addElement("M_sOB").setText(zhangbbm); // 账簿 编码
            glVoucherHead.addElement("M_postedPeriod").setText(currentDateString.substring(0, 7)); // 记账区间
            glVoucherHead.addElement("M_attachmentCount").setText("0"); // 固定值0
            glVoucherHead.addElement("M_createDate").setText(currentDateString); // 凭证创日期时间

            Document document = DocumentHelper.createDocument(root);
            String pushXml = document.asXML();
            this.writeLog("费用报销凭证推送xml： " + pushXml);

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
            this.writeLog("费用报销 End ===============");
        } catch (Exception e) {
            this.writeLog("费用报销 异常： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("费用报销 异常： " + e);
            return "0";
        }

        return "1";
    }
}
