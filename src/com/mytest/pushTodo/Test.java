package com.mytest.pushTodo;

import com.alibaba.fastjson.JSONObject;
import com.weavernorth.meitanzy.util.MtHttpUtil;
import weaver.general.TimeUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class Test {

    @org.junit.Test
    public void test1() {
        // 发送待办
        String url = "http://127.0.0.1:8080/rest/ofs/ReceiveTodoRequestByJson";
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("syscode", "test"); // 异构系统标识
        jsonObject.put("flowid", "2"); // 流程实例id
        jsonObject.put("requestname", "测试流程" + TimeUtil.getCurrentTimeString()); // 标题
        jsonObject.put("workflowname", "测试流程"); // 流程类型名称
        jsonObject.put("nodename", "领导审批"); // 步骤名称（节点名称）

        jsonObject.put("pcurl", "http://www.baidu.com&todoRead=1"); // PC地址
        jsonObject.put("appurl", "1"); // APP地址
        jsonObject.put("creator", "ls"); // 创建人（原值）
        jsonObject.put("createdatetime", TimeUtil.getCurrentTimeString()); // 创建日期时间
        jsonObject.put("receiver", "nzm"); // 接收人（原值）

        jsonObject.put("receivedatetime", TimeUtil.getCurrentTimeString()); // 接收日期时间
        jsonObject.put("receivets", String.valueOf(System.currentTimeMillis())); // 时间戳字段

        String jsonString = jsonObject.toJSONString();
        System.out.println("发送json： " + jsonString);

        String s = MtHttpUtil.postJsonHeader(url, jsonString, null);
        System.out.println(s);
    }


    /**
     * 变为已办
     */
    @org.junit.Test
    public void test3() {
        String url = "http://127.0.0.1:8080/rest/ofs/ProcessDoneRequestByJson";
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("syscode", "test"); // 异构系统标识
        jsonObject.put("flowid", "1"); // 流程实例id
        jsonObject.put("requestname", "测试流程2 2021-02-02 11:59:441"); // 标题
        jsonObject.put("workflowname", "财务流程"); // 流程类型名称
        jsonObject.put("nodename", "领导审批2"); // 步骤名称（节点名称）

        jsonObject.put("receiver", "nzm"); // 接收人（原值）
        jsonObject.put("receivets", String.valueOf(System.currentTimeMillis()));


      //  String jsonString = jsonObject.toJSONString();
        String jsonString = "{\"syscode\":\"test\",\"nodename\":\"领导审批\",\"requestname\":\"测试流程2021-05-28 17:11:19\",\"receivets\":\"1622193803877\",\"receiver\":\"nzm\",\"workflowname\":\"测试流程\",\"flowid\":\"5\"}";
        System.out.println("发送json： " + jsonString);

        String s = MtHttpUtil.postJsonHeader(url, jsonString, null);
        System.out.println(s);
    }

    @org.junit.Test
    public void test5() {
        // 发送待阅
        String url = "http://127.0.0.1:8080/rest/ofs/ReceiveCCRequestByJson";
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("syscode", "local"); // 异构系统标识
        jsonObject.put("flowid", "2"); // 流程实例id
        jsonObject.put("requestname", "local测试流程" + TimeUtil.getCurrentTimeString()); // 标题
        jsonObject.put("workflowname", "local测试流程"); // 流程类型名称
        jsonObject.put("nodename", "领导审批"); // 步骤名称（节点名称）

        jsonObject.put("pcurl", "www.baidu.com"); // PC地址
        jsonObject.put("appurl", "1"); // APP地址
        jsonObject.put("creator", "ls"); // 创建人（原值）
        jsonObject.put("createdatetime", TimeUtil.getCurrentTimeString()); // 创建日期时间
        jsonObject.put("receiver", "nzm"); // 接收人（原值）

        jsonObject.put("receivedatetime", TimeUtil.getCurrentTimeString()); // 接收日期时间
        jsonObject.put("receivets", String.valueOf(System.currentTimeMillis())); // 时间戳字段


        String jsonString = jsonObject.toJSONString();
        System.out.println("发送json： " + jsonString);

        String s = MtHttpUtil.postJsonHeader(url, jsonString, null);
        System.out.println(s);
    }

    /**
     * 变为办结
     */
    @org.junit.Test
    public void test4() {
        String url = "http://127.0.0.1:8080/rest/ofs/ProcessOverRequestByJson";
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("syscode", "test"); // 异构系统标识
        jsonObject.put("flowid", "1"); // 流程实例id
        jsonObject.put("requestname", "测试流程2 2021-02-02 11:59:441"); // 标题
        jsonObject.put("workflowname", "财务流程"); // 流程类型名称
        jsonObject.put("nodename", "领导审批2"); // 步骤名称（节点名称）

        jsonObject.put("receiver", "zs"); // 接收人（原值）
        jsonObject.put("receivets", String.valueOf(System.currentTimeMillis()));


        String jsonString = jsonObject.toJSONString();
        System.out.println("发送json： " + jsonString);

        String s = MtHttpUtil.postJsonHeader(url, jsonString, null);
        System.out.println(s);
    }

    @org.junit.Test
    public void test2() {
        String s = doReceiveTodo();
        System.out.println(s);
    }


    private String doReceiveTodo() {
        //Json格式参数
        String paramStr = getReceiveTodoJson();
        //Xml格式参数
        //String paramStr = getReceiveToDoXml();
        OutputStreamWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            // 接口地址和接口方法
            // 此例的接口访问地址为http://127.0.0.1:9008，可依据实际情况修改
            // 此处为调用rest接收待办数据接口
            //Json格式
            URL realUrl = new URL("http://127.0.0.1:8080/rest/ofs/ReceiveTodoRequestByJson");
            //Xml格式
//            URL realUrl = new URL("http://127.0.0.1:9008" +
//            "/rest/ofs/receiveTodoRequestByXml");
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(paramStr);
            out.flush();
            in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    private String getReceiveTodoJson() {
        JSONObject json = new JSONObject();
        json.put("syscode", "test");
        json.put("flowid", "2");
        json.put("requestname", "标题名称TEST");
        json.put("workflowname", "流程类型测试用");
        json.put("nodename", "节点名称");

        json.put("pcurl", "/test.jsp");
        json.put("appurl", "");
        json.put("viewtype", "1");
        // 依据人员转换规则，传递人员信息数据
        json.put("creator", "nzm");
        // 日期时间格式为2004-09-07 23:20:05
        json.put("createdatetime", TimeUtil.getCurrentTimeString());
        // 依据人员转换规则，传递人员信息数据
        json.put("receiver", "zs");
        // 日期时间格式为2004-09-07 23:20:05
        json.put("receivedatetime", "2018-01-20 01:43:43");

        return json.toJSONString();
    }

    @org.junit.Test
    public void test6() {
        // 发送已办
        String url = "http://127.0.0.1:8080/rest/ofs/ReceiveRequestInfoByJson";
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("syscode", "test"); // 异构系统标识
        jsonObject.put("flowid", "1"); // 流程实例id
        jsonObject.put("requestname", "测试流程" + TimeUtil.getCurrentTimeString()); // 标题
        jsonObject.put("workflowname", "测试流程-已办"); // 流程类型名称
        jsonObject.put("nodename", "领导审批"); // 步骤名称（节点名称）

        jsonObject.put("pcurl", "http://www.xxxxx.com&todoRead=1"); // PC地址
        jsonObject.put("appurl", "1"); // APP地址
        jsonObject.put("creator", "ls"); // 创建人（原值）
        jsonObject.put("createdatetime", TimeUtil.getCurrentTimeString()); // 创建日期时间
        jsonObject.put("receiver", "nzm"); // 接收人（原值）

        jsonObject.put("receivedatetime", TimeUtil.getCurrentTimeString()); // 接收日期时间
        jsonObject.put("receivets", String.valueOf(System.currentTimeMillis())); // 时间戳字段
        jsonObject.put("isremark", "2");
        jsonObject.put("viewtype", "1");

        String jsonString = jsonObject.toJSONString();
        System.out.println("发送json： " + jsonString);

        String s = MtHttpUtil.postJsonHeader(url, jsonString, null);
        System.out.println(s);
    }
}
