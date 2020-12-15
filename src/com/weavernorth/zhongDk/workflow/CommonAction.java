package com.weavernorth.zhongDk.workflow;

import com.weavernorth.zhongDk.workflow.util.ZdkFlowUtil;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

/**
 * 主数据㧲通用action
 */
public class CommonAction extends BaseAction {

    @Override
    public String execute(RequestInfo requestInfo) {
        String requestName = requestInfo.getRequestManager().getRequestname();
        String requestId = requestInfo.getRequestid();
        String operateType = requestInfo.getRequestManager().getSrc();
        int formId = requestInfo.getRequestManager().getFormid();
        String tableName = "";
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("SELECT tablename FROM workflow_bill WHERE id = '" + formId + "'");
        if (recordSet.next()) {
            tableName = recordSet.getString("tablename");
        }

        this.writeLog("推送信息至主数据系统Start requestid=" + requestId + "，requestName=" + requestName + "，tableName --- " + tableName);
        try {
            // 查询主数据表名
            recordSet.executeQuery("select mdmbmc from uf_lczddzb where lcbdmc = '" + tableName + "'");
            if (recordSet.next()) {
                String mdmbmc = recordSet.getString("mdmbmc");
                this.writeLog("主数据对应表名： " + mdmbmc);
                String sendJson = ZdkFlowUtil.getSendJson(tableName, requestId, mdmbmc);
                this.writeLog("发送json：" + sendJson);

                String sendXml = "<soapenv:Envelope\n" +
                        "    xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                        "    xmlns:int=\"http://www.meritit.com/ws/IntfsServiceWS\">\n" +
                        "    <soapenv:Header/>\n" +
                        "    <soapenv:Body>\n" +
                        "        <int:importData>\n" +
                        "            <int:modelCode>" + mdmbmc + "</int:modelCode>\n" +
                        "            <int:dataStr>" + sendJson + "</int:dataStr>\n" +
                        "            <int:dataType>JSON</int:dataType>\n" +
                        "            <int:dataStatus>1</int:dataStatus>\n" +
                        "            <int:userName></int:userName>\n" +
                        "            <int:password></int:password>\n" +
                        "        </int:importData>\n" +
                        "    </soapenv:Body>\n" +
                        "</soapenv:Envelope>";

                String returnXml = ZdkFlowUtil.postJson(sendXml);
                this.writeLog("主数据返回xml：" + returnXml);
                if (StringUtils.isBlank(returnXml)) {
                    requestInfo.getRequestManager().setMessageid("110000");
                    requestInfo.getRequestManager().setMessagecontent("推送信息至主数据系统 Error 请查看日志");
                    return "0";
                }
                Document document = DocumentHelper.parseText(returnXml);
                Element rootElt = document.getRootElement();
                Element s = rootElt.element("Body");
                Element ns2 = s.element("importDataResponse");
                String aReturn = StringUtils.trimToEmpty(ns2.elementTextTrim("return"));
                if (!"1".equals(aReturn)) {
                    requestInfo.getRequestManager().setMessageid("110000");
                    requestInfo.getRequestManager().setMessagecontent("推送信息至主数据系统 Error: " + aReturn);
                    return "0";
                }
            }

            this.writeLog("推送信息至主数据系统 End ===============");
        } catch (Exception e) {
            this.writeLog("推送信息至主数据系统 Error： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("推送信息至主数据系统 Error： " + e);
            return "0";
        }

        return "1";
    }
}
