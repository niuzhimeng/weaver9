package weaver.ofs.interfaces;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.workflow.request.todo.DataObj;
import weaver.workflow.request.todo.RequestStatusObj;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;

/**
 * 本示例，只将接受的数据打印在log/ecology中，具体的接口调用操作，请在相关数据遍历中执行。
 * 实现weaver.ofs.interfaces.SendRequestStatusDataInterfaces接口中SendRequestStatusData
 */
public class TestSendImpl implements SendRequestStatusDataInterfaces {

    private static final Log log = LogFactory.getLog(TestSendImpl.class);

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


    public String getId() {
        return id;
    }

    public String getSyscode() {
        return syscode;
    }

    public String getServerurl() {
        return serverurl;
    }

    public ArrayList<String> getWorkflowwhitelist() {
        return workflowwhitelist;
    }

    public ArrayList<String> getUserwhitelist() {
        return userwhitelist;
    }

    /**
     * 实现消息推送的具体方法
     *
     * @param datas 传入的请求数据对象数据集
     */
    public void SendRequestStatusData(ArrayList<DataObj> datas) {

        for (DataObj dobj : datas) {
            JSONArray jsonArray = new JSONArray();
            JSONObject mainboject = new JSONObject();
            String requestname = "";
            JSONObject todojson = new JSONObject();
            JSONObject donejson = new JSONObject();
            JSONObject deljson = new JSONObject();
            ArrayList<RequestStatusObj> tododatas = dobj.getTododatas();
            if (tododatas.size() > 0) {//处理推送的待办数据
                JSONArray todolist = new JSONArray();
                for (RequestStatusObj rso : tododatas) {//遍历当前发送的待办数据
                    JSONObject rsojson = new JSONObject();
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
                    JSONObject rsojson = new JSONObject();
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
                    JSONObject rsojson = new JSONObject();
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
            log.error("\n" + formatJson(mainboject.toString()));
        }

    }

    /**
     * 格式化JSON格式输出
     *
     * @param jsonStr
     * @return 返回指定格式化的数据
     */
    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr))
            return "";
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        boolean isInQuotationMarks = false;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '"':
                    if (last != '\\') {
                        isInQuotationMarks = !isInQuotationMarks;
                    }
                    sb.append(current);
                    break;
                case '{':
                case '[':
                    sb.append(current);
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent++;
                        addIndentBlank(sb, indent);
                    }
                    break;
                case '}':
                case ']':

                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent--;
                        addIndentBlank(sb, indent);
                    }
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    /**
     * 添加space(缩进)
     *
     * @param sb
     * @param indent
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }

}