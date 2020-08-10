package com.weavernorth.saiwen.timed;

import com.weavernorth.saiwen.myWeb.WebUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.formmode.setup.ModeRightInfo;
import weaver.general.BaseBean;
import weaver.general.TimeUtil;
import weaver.interfaces.schedule.BaseCronJob;

import java.util.ArrayList;
import java.util.List;

/**
 * 定时获取全量科目信息
 */
public class SubjectTimed extends BaseCronJob {
    /**
     * 模块id
     */
    private static final String MODE_ID = "21";
    private BaseBean baseBean = new BaseBean();
    private ModeRightInfo moderightinfo = new ModeRightInfo();

    @Override
    public void execute() {
        String myXml = "<?xml version=\"1.0\" encoding=\"utf‐16\"?>\n" +
                "<Cbo_Account_Query_Model>\n" +
                "<Sob>-999</Sob>\n" +
                "</Cbo_Account_Query_Model>";
        try {
            int insertCount = 0;
            int updateCount = 0;
            String currentTimeString = TimeUtil.getCurrentTimeString();
            baseBean.writeLog("定时获取科目信息 Start ================== " + currentTimeString);
            // 获取科目信息
            String returnXml = WebUtil.getSubject(myXml);

            Document doc = DocumentHelper.parseText(returnXml);
            Element rootElt = doc.getRootElement();
            List resultList = rootElt.element("AccountQueryResultList").elements();
            baseBean.writeLog("科目信息返回共计：" + resultList.size() + " 条");

            // 此次新增数据id集合
            List<String> addIdList = new ArrayList<String>();

            RecordSet recordSet = new RecordSet();
            RecordSet insertSet = new RecordSet();
            RecordSet updateSet = new RecordSet();
            recordSet.executeQuery("select kmid from uf_subject");
            // 已有数据的id集合
            List<String> idList = new ArrayList<String>(recordSet.getCounts() + 1000);
            while (recordSet.next()) {
                idList.add(recordSet.getString("kmid"));
            }

            Element myElement;
            Object[] parameter = new Object[15];
            for (Object tableObj : resultList) {
                myElement = (Element) tableObj;
                parameter[0] = myElement.elementTextTrim("Sob");
                parameter[1] = myElement.elementTextTrim("OrgName");
                parameter[2] = myElement.elementTextTrim("OrgCode");
                parameter[3] = myElement.elementTextTrim("Code");
                parameter[4] = myElement.elementTextTrim("Name");

                parameter[5] = myElement.elementTextTrim("AccountProperty_AccountBasic_code");
                parameter[6] = myElement.elementTextTrim("AccountProperty_AccountBasic_name");
                parameter[7] = myElement.elementTextTrim("AccountProperty_Name");
                parameter[8] = myElement.elementTextTrim("AccountProperty_Code");
                String kmId = myElement.elementTextTrim("Id");
                parameter[9] = kmId;

                parameter[10] = MODE_ID;
                parameter[11] = "1";
                parameter[12] = "0";
                parameter[13] = TimeUtil.getCurrentTimeString().substring(0, 10);
                parameter[14] = TimeUtil.getCurrentTimeString().substring(11);

                if (idList.contains(kmId)) {
                    updateSet.executeUpdate("update uf_subject set sob = ?, org_name = ?, org_code = ?, code = ?, name = ?," +
                                    "account_basic_code = ?, account_basic_name = ?, account_property_name = ?, account_property_code = ?," +
                                    "modedatacreatedate = ?, modedatacreatetime = ? where kmid = ?",
                            parameter[0], parameter[1], parameter[2], parameter[3], parameter[4],
                            parameter[5], parameter[6], parameter[7], parameter[8], parameter[13], parameter[14], kmId);
                    updateCount++;
                } else {
                    idList.add(kmId);
                    addIdList.add(kmId);
                    insertSet.executeUpdate("insert into uf_subject(sob, org_name, org_code, code, name, " +
                            "account_basic_code, account_basic_name, account_property_name, account_property_code, kmid, " +
                            "formmodeid,modedatacreater,modedatacreatertype,modedatacreatedate,modedatacreatetime) " +
                            "values(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?)", parameter);
                    insertCount++;
                }
            }
            baseBean.writeLog("赋权开始 ====== 数据条数： " + addIdList.size());

            moderightinfo.setNewRight(true);
            RecordSet maxSet = new RecordSet();
            maxSet.executeSql("select id,kmid from uf_subject where MODEDATACREATEDATE + MODEDATACREATETIME >= '" + currentTimeString + "'");

            int maxId;
            while (maxSet.next()) {
                if (!addIdList.contains(maxSet.getString("kmid"))) {
                    // 不是新增的，就跳过赋权，否则太费时间，会产生大量日志
                    continue;
                }
                maxId = maxSet.getInt("id");
                //创建人id， 模块id， 该条数据id
                moderightinfo.rebuildModeDataShareByEdit(1, Integer.parseInt(MODE_ID), maxId);
            }
            baseBean.writeLog("新增： " + insertCount + ", 更新： " + updateCount);
            baseBean.writeLog("定时获取科目信息 End ================== " + TimeUtil.getCurrentTimeString());
        } catch (Exception e) {
            baseBean.writeLog("定时获取科目信息 Error: " + e);
        }
    }
}
