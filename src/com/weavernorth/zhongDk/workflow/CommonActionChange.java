package com.weavernorth.zhongDk.workflow;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weavernorth.zhongDk.workflow.util.ZdkFlowUtil;
import com.weavernorth.zhongDk.workflow.vo.ZdkJsonVO;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.action.BaseAction;

import java.util.HashMap;
import java.util.Map;

/**
 * 主数据通用action 状态变更接口
 */
public class CommonActionChange extends BaseAction {

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

        this.writeLog("推送信息至主数据系统-状态变更Start requestid=" + requestId + "，requestName=" + requestName + "，tableName --- " + tableName);
        try {
            // 查询主表
            recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
            recordSet.next();
            String sjzt = recordSet.getString("sjzt"); // 数据状态
            this.writeLog("数据状态： " + sjzt);

            // 根据流程表单名  查询主数据表名（接口中需要传递）
            String selSql = "select mdmbmc from uf_lczddzb where lcbdmc like '%," + tableName + ",%'";
            this.writeLog("字段对应关系查询语句： " + selSql);
            recordSet.executeQuery(selSql);
            if (recordSet.next()) {
                String mdmbmc = recordSet.getString("mdmbmc");
                this.writeLog("主数据对应表名： " + mdmbmc);
                String sendJson = getSendJson(tableName, requestId, mdmbmc);
                this.writeLog("发送json：" + sendJson);

                String sendXml = "<soapenv:Envelope\n" +
                        "    xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                        "    xmlns:int=\"http://www.meritit.com/ws/IntfsServiceWS\">\n" +
                        "    <soapenv:Header/>\n" +
                        "    <soapenv:Body>\n" +
                        "        <int:updateDataUseStatus>\n" +
                        "            <int:modelCode>" + mdmbmc + "</int:modelCode>\n" +
                        "            <int:dataStr>" + sendJson + "</int:dataStr>\n" +
                        "            <int:dataType>JSON</int:dataType>\n" +
                        "            <int:dataStatus>" + sjzt + "</int:dataStatus>\n" +
                        "            <int:userName></int:userName>\n" +
                        "            <int:password></int:password>\n" +
                        "        </int:updateDataUseStatus>\n" +
                        "    </soapenv:Body>\n" +
                        "</soapenv:Envelope>";
                this.writeLog("发送xml：" + sendXml);
                String returnXml = ZdkFlowUtil.postJson(sendXml, "updateDataUseStatus");
                this.writeLog("主数据返回xml：" + returnXml);
                if (StringUtils.isBlank(returnXml)) {
                    requestInfo.getRequestManager().setMessageid("110000");
                    requestInfo.getRequestManager().setMessagecontent("推送信息至主数据系统-状态变更 Error 请查看日志");
                    return "0";
                }
                Document document = DocumentHelper.parseText(returnXml);
                Element rootElt = document.getRootElement();
                Element s = rootElt.element("Body");
                Element ns2 = s.element("updateDataUseStatusResponse");
                String aReturn = StringUtils.trimToEmpty(ns2.elementTextTrim("return"));
                if (!"1".equals(aReturn)) {
                    requestInfo.getRequestManager().setMessageid("110000");
                    requestInfo.getRequestManager().setMessagecontent("推送信息至主数据系统-状态变更 Error: " + aReturn);
                    return "0";
                }
            }

            this.writeLog("推送信息至主数据系统-状态变更 End ===============");
        } catch (Exception e) {
            this.writeLog("推送信息至主数据系统-状态变更 Error： " + e);
            requestInfo.getRequestManager().setMessageid("110000");
            requestInfo.getRequestManager().setMessagecontent("推送信息至主数据系统-状态变更 Error： " + e);
            return "0";
        }

        return "1";
    }

    /**
     * 根据配置表，拼接发送的字段json
     *
     * @param tableName    oa流程表名
     * @param requestId    流程请求id
     * @param mdmTableName 主数据表名
     * @return 拼接好的json
     */
    public static String getSendJson(String tableName, String requestId, String mdmTableName) {
        // 主数据字段名 - 自定义类
        Map<String, ZdkJsonVO> zdMap = new HashMap<>();

        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("select b.* from uf_lczddzb a left join uf_lczddzb_dt1 b on a.id = b.mainid where a.lcbdmc like '%," + tableName + ",%'");
        while (recordSet.next()) {
            String szbd = recordSet.getString("szbd");
            if ("0".equals(szbd)) {
                // 主表字段
                ZdkJsonVO zdkJsonVO = new ZdkJsonVO();
                zdkJsonVO.setOaColName(recordSet.getString("oazdm"));
                zdkJsonVO.setIfSplit(recordSet.getString("sffg")); // 是否分隔
                zdkJsonVO.setSplitFlag(recordSet.getString("fgf")); // 分隔符
                zdkJsonVO.setTakePart(recordSet.getString("qzbf")); // 取值部分

                zdMap.put(recordSet.getString("zsjzdm"), zdkJsonVO);
            }
        }

        JSONObject jsonContent = new JSONObject(true);
        // 查询主表
        recordSet.executeQuery("select * from " + tableName + " where requestid = '" + requestId + "'");
        recordSet.next();
        for (Map.Entry<String, ZdkJsonVO> entry : zdMap.entrySet()) {
            ZdkJsonVO zdkJsonVO = entry.getValue();
            String colValue = recordSet.getString(zdkJsonVO.getOaColName()); // 字段值
            jsonContent.put(entry.getKey(), zdkJsonVO.handle(colValue));
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(mdmTableName, jsonContent);

        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonObject);

        JSONObject allObj = new JSONObject();
        allObj.put("LIST", jsonArray);
        return allObj.toJSONString();
    }
}
