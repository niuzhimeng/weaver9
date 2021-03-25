package com.weavernorth.zhongDk.workflow;

import com.alibaba.fastjson.JSONObject;
import com.engine.workflow.entity.publicApi.ApiWorkflowRequestInfo;
import com.engine.workflow.publicApi.impl.WorkflowRequestListPAImpl;
import com.weaver.general.Util;
import com.weavernorth.zhongDk.workflow.util.ZdkFlowUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.RecordSet;
import weaver.hrm.OnLineMonitor;
import weaver.hrm.User;
import weaver.login.Account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetFlowToDo {

    private static final Log LOGGER = LogFactory.getLog(GetFlowToDo.class);

    @GET
    @Path("/GetFlowToDo")
    @Produces(MediaType.APPLICATION_JSON)
    public String GetFlowToDoMethod(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        String loginId = Util.null2String(request.getParameter("loginId")).trim();
        String timestamp = Util.null2String(request.getParameter("timestamp")).trim();
        String sign = Util.null2String(request.getParameter("sign")).trim();
        int count = Util.getIntValue(request.getParameter("count"), 10);
        if (count > 50) {
            count = 50;
        }

        LOGGER.info("慧点获取OA待办Start============" + loginId);

        JSONObject jsonObject = ZdkFlowUtil.apiCheck(loginId, timestamp, sign);
        LOGGER.info("接口调用权限校验结果： " + jsonObject.toJSONString());
        if ("500".equals(jsonObject.getString("status"))) {
            return jsonObject.toJSONString();
        }

        try {
            RecordSet recordSet = new RecordSet();
            recordSet.executeQuery("select * from HrmResource where loginid = lower('" + loginId + "') and status < 4");
            recordSet.next();
            User userNew = new User();
            userNew.setUid(recordSet.getInt("id"));
            userNew.setLoginid(recordSet.getString("loginid"));
            userNew.setFirstname(recordSet.getString("firstname"));
            userNew.setLastname(recordSet.getString("lastname"));
            userNew.setAliasname(recordSet.getString("aliasname"));
            userNew.setTitle(recordSet.getString("title"));
            userNew.setTitlelocation(recordSet.getString("titlelocation"));
            userNew.setSex(recordSet.getString("sex"));
            userNew.setPwd(recordSet.getString("password"));
            String languageidweaver = recordSet.getString("systemlanguage");
            userNew.setLanguage(Util.getIntValue(languageidweaver, 0));
            userNew.setTelephone(recordSet.getString("telephone"));
            userNew.setMobile(recordSet.getString("mobile"));
            userNew.setMobilecall(recordSet.getString("mobilecall"));
            userNew.setEmail(recordSet.getString("email"));
            userNew.setCountryid(recordSet.getString("countryid"));
            userNew.setLocationid(recordSet.getString("locationid"));
            userNew.setResourcetype(recordSet.getString("resourcetype"));
            userNew.setStartdate(recordSet.getString("startdate"));
            userNew.setEnddate(recordSet.getString("enddate"));
            userNew.setContractdate(recordSet.getString("contractdate"));
            userNew.setJobtitle(recordSet.getString("jobtitle"));
            userNew.setJobgroup(recordSet.getString("jobgroup"));
            userNew.setJobactivity(recordSet.getString("jobactivity"));
            userNew.setJoblevel(recordSet.getString("joblevel"));
            userNew.setSeclevel(recordSet.getString("seclevel"));
            userNew.setUserDepartment(Util.getIntValue(recordSet.getString("departmentid"), 0));
            userNew.setUserSubCompany1(Util.getIntValue(recordSet.getString("subcompanyid1"), 0));
            userNew.setUserSubCompany2(Util.getIntValue(recordSet.getString("subcompanyid2"), 0));
            userNew.setUserSubCompany3(Util.getIntValue(recordSet.getString("subcompanyid3"), 0));
            userNew.setUserSubCompany4(Util.getIntValue(recordSet.getString("subcompanyid4"), 0));
            userNew.setManagerid(recordSet.getString("managerid"));
            userNew.setAssistantid(recordSet.getString("assistantid"));
            userNew.setPurchaselimit(recordSet.getString("purchaselimit"));
            userNew.setCurrencyid(recordSet.getString("currencyid"));
            userNew.setLastlogindate(recordSet.getString("currentdate"));
            userNew.setLogintype("1");
            userNew.setAccount(recordSet.getString("account"));

            userNew.setLoginip(request.getRemoteAddr());
            List<Account> childAccountList = getChildAccountList(userNew);//子账号相关设置
            request.getSession(true).setMaxInactiveInterval(60 * 60 * 24);
            request.getSession(true).setAttribute("weaver_user@bean", userNew);
            request.getSession(true).setAttribute("accounts", childAccountList);
            request.getSession(true).setAttribute("browser_isie", getisIE(request));

            request.getSession(true).setAttribute("moniter", new OnLineMonitor("" + userNew.getUID(), userNew.getLoginip()));
            request.getSession(true).setAttribute("logmessage", getLogMessage(String.valueOf(userNew.getUID())));


            // 查询待办条件
            Map<String, String> map = new HashMap<>();
            WorkflowRequestListPAImpl workflowRequestListPA = new WorkflowRequestListPAImpl();
            // 最后一个参数，是否显示异构系统流程（比如慧点推过来的）
            List<ApiWorkflowRequestInfo> toDoWorkflowRequestList = workflowRequestListPA.getToDoWorkflowRequestList(1, count, userNew, map, true, false);

            jsonObject.put("count", toDoWorkflowRequestList.size());
            jsonObject.put("content", toDoWorkflowRequestList);
        } catch (Exception e) {
            LOGGER.error("慧点获取OA待办Error================" + e);
        }
        return jsonObject.toJSONString();
    }

    private List<Account> getChildAccountList(User user) {
        List<Account> accounts = new ArrayList<Account>();
        int uid = user.getUID();
        RecordSet rs = new RecordSet();
        rs.executeQuery("select * from hrmresource where status in (0,1,2,3) and (belongto = " + uid + " or id = " + uid + ")");
        while (rs.next()) {
            Account account = new Account();
            account.setId(rs.getInt("id"));
            account.setDepartmentid(rs.getInt("departmentid"));
            account.setJobtitleid(rs.getInt("JOBTITLE"));
            account.setSubcompanyid(rs.getInt("SUBCOMPANYID1"));
            account.setType(rs.getInt("ACCOUNTTYPE"));
            account.setAccount(Util.null2String(rs.getString("ACCOUNT")));
            accounts.add(account);
        }
        return accounts;
    }

    /**
     * 获取日志信息
     */
    private String getLogMessage(String uid) {
        String message = "";
        RecordSet rs = new RecordSet();
        String sqltmp = "";
        if (rs.getDBType().equals("oracle")) {
            sqltmp = "select * from (select * from SysMaintenanceLog where relatedid = " + uid + " and operatetype='6' and operateitem='60' order by id desc ) where rownum=1 ";
        } else if (rs.getDBType().equals("db2")) {
            sqltmp = "select * from SysMaintenanceLog where relatedid = " + uid + " and operatetype='6' and operateitem='60' order by id desc fetch first 1 rows only ";
        } else if (rs.getDBType().equals("mysql")) {
            sqltmp = "SELECT t2.* FROM (SELECT * FROM SysMaintenanceLog WHERE relatedid = " + uid + " and  operatetype='6' AND operateitem='60' ORDER BY id DESC) t2  LIMIT 1 ,1";
        } else {
            sqltmp = "select top 1 * from SysMaintenanceLog where relatedid = " + uid + " and operatetype='6' and operateitem='60' order by id desc";
        }

        rs.executeSql(sqltmp);
        if (rs.next()) {
            message = rs.getString("clientaddress") + " " + rs.getString("operatedate") + " " + rs.getString("operatetime");
        }

        return message;
    }

    /**
     * 判断浏览器是否为IE
     */
    private String getisIE(HttpServletRequest request) {
        String isIE = "true";
        String agent = request.getHeader("User-Agent").toLowerCase();
        if (!agent.contains("rv:11") && !agent.contains("msie")) {
            isIE = "false";
        }
        if (agent.contains("rv:11") || agent.contains("msie")) {
            isIE = "true";
        }
        return isIE;
    }
}













