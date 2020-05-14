package com.weavernorth;

import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.TimeUtil;
import net.sf.json.JSONArray;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;
import weaver.ofs.interfaces.SendRequestStatusDataInterfaces;
import weaver.workflow.request.todo.DataObj;
import weaver.workflow.request.todo.RequestStatusObj;

import java.util.ArrayList;

/**
 * 推送待办 亲测有效，细节待完善
 */
public class PushTodoTest implements SendRequestStatusDataInterfaces {

    private static Log log = LogFactory.getLog(PushTodoTest.class);
    private BaseBean baseBean = new BaseBean();

    /**
     * 后台设置id
     */
    public String id;
    /**
     * 设置的系统编号
     */
    public String syscode;
    /**
     * 服务器URL
     */
    public String serverurl;
    /**
     * 流程白名单
     */
    public ArrayList<String> workflowwhitelist;
    /**
     * 人员白名单
     */
    public ArrayList<String> userwhitelist;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getSyscode() {
        return syscode;
    }

    @Override
    public String getServerurl() {
        return serverurl;
    }

    @Override
    public ArrayList<String> getWorkflowwhitelist() {
        return workflowwhitelist;
    }

    @Override
    public ArrayList<String> getUserwhitelist() {
        return userwhitelist;
    }

    @Override
    public void SendRequestStatusData(ArrayList<DataObj> arrayList) {
        log.info("推送待办测试Start============" + TimeUtil.getCurrentTimeString());

        for (DataObj dobj : arrayList) {
            JSONArray jsonArray = new JSONArray();
            net.sf.json.JSONObject mainboject = new net.sf.json.JSONObject();
            String requestname = "";
            net.sf.json.JSONObject todojson = new net.sf.json.JSONObject();
            net.sf.json.JSONObject donejson = new net.sf.json.JSONObject();
            net.sf.json.JSONObject deljson = new net.sf.json.JSONObject();
            ArrayList<RequestStatusObj> tododatas = dobj.getTododatas();
            if (tododatas.size() > 0) {//处理推送的待办数据
                JSONArray todolist = new JSONArray();
                for (RequestStatusObj rso : tododatas) {//遍历当前发送的待办数据
                    net.sf.json.JSONObject rsojson = new net.sf.json.JSONObject();
                    requestname = rso.getRequstname();
                    rsojson.put("cid", rso.getCid());
                    rsojson.put("username", rso.getUser().getLastname() + "(" + rso.getUser().getUID() + ")");
                    rsojson.put("isremark", rso.getIsremark());
                    rsojson.put("viewtype", rso.getViewtype());
                    rsojson.put("receivedatetime", rso.getReceivedate() + " " + rso.getReceivetime() + "/" + rso.getOperatedate() + " " + rso.getOperatetime());
                    rsojson.put("nodename", rso.getNodename());
                    todolist.add(rsojson);
                }
                todojson.put("TodoCount", tododatas.size());
                todojson.put("TodoDatas", todolist);
                jsonArray.add(todojson);
            }
            ArrayList<RequestStatusObj> donedatas = dobj.getDonedatas();
            if (donedatas.size() > 0) {//处理推送的已办数据
                JSONArray todolist = new JSONArray();
                for (RequestStatusObj rso : donedatas) {//遍历当前发送的已办数据
                    net.sf.json.JSONObject rsojson = new net.sf.json.JSONObject();
                    requestname = rso.getRequstname();
                    rsojson.put("cid", rso.getCid());
                    rsojson.put("username", rso.getUser().getLastname() + "(" + rso.getUser().getUID() + ")");
                    rsojson.put("isremark", rso.getIsremark());
                    rsojson.put("viewtype", rso.getViewtype());
                    rsojson.put("receivedatetime", rso.getReceivedate() + " " + rso.getReceivetime() + "/" + rso.getOperatedate() + " " + rso.getOperatetime());
                    rsojson.put("nodename", rso.getNodename());
                    rsojson.put("iscomplete", rso.getIscomplete());
                    todolist.add(rsojson);
                }
                donejson.put("DoneCount", donedatas.size());
                donejson.put("DoneDatas", todolist);
                jsonArray.add(donejson);
            }
            ArrayList<RequestStatusObj> deldatas = dobj.getDeldatas();
            if (deldatas.size() > 0) {//处理推送的删除数据
                JSONArray todolist = new JSONArray();
                for (RequestStatusObj rso : donedatas) {//遍历当前发送的删除数据
                    requestname = rso.getRequstname();
                    net.sf.json.JSONObject rsojson = new net.sf.json.JSONObject();
                    rsojson.put("cid", rso.getCid());
                    rsojson.put("username", rso.getUser().getLastname() + "(" + rso.getUser().getUID() + ")");
                    todolist.add(rsojson);
                }
                deljson.put("DelCount", deldatas.size());
                deljson.put("DelDatas", todolist);
                jsonArray.add(deljson);
            }

            mainboject.put("syscode", syscode);
            mainboject.put("requestid", dobj.getRequestid());
            mainboject.put("requestname", requestname);
            mainboject.put("sendtimestamp", dobj.getSendtimestamp());
            mainboject.put("RequestDatas", jsonArray);

            //输入内容信息到日志文件中 /log/ecology
            log.info("待办、已办数据： " + mainboject.toString());
        }
        baseBean.writeLog(JSONObject.toJSONString(arrayList));
    }
}
