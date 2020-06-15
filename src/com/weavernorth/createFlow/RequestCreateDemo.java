package com.weavernorth.createFlow;

import com.alibaba.fastjson.JSONObject;
import com.weavernorth.createFlow.util.IdentityVerifyUtil;
import com.weavernorth.createFlow.util.OkHttpUtil;
import com.weavernorth.createFlow.vo.WorkflowDetailTableInfoEntity;
import com.weavernorth.createFlow.vo.WorkflowRequestTableField;
import com.weavernorth.createFlow.vo.WorkflowRequestTableRecord;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程创建实例
 * description :
 * author ：JHY
 * date : 2020/4/29
 * version : 1.0
 */
public class RequestCreateDemo {

    @Test
    public void test1() {
        createRequest("流程测试-" + currentTime(), "21");
    }

    /**
     * 创建流程入口
     *
     * @param requestname 流程标题
     * @param userid      创建人id
     */
    public void createRequest(String requestname, String userid) {
        IdentityVerifyUtil identityVerifyUtil = IdentityVerifyUtil.getInstance();
        String spk = identityVerifyUtil.getSPK();
        String secret = identityVerifyUtil.getSECRET();
        String token = identityVerifyUtil.getToken();

        System.out.println("SPK: " + spk);
        System.out.println("secret: " + secret);
        System.out.println("token: " + token);
        if (spk == null || secret == null || token == null) {
            return;
        }
        String url = IdentityVerifyUtil.HOST + "/api/workflow/paService/doCreateRequest";
        Map<String, String> heads = IdentityVerifyUtil.getHttpHeads(token, userid, spk);
        Map<String, String> params = new HashMap<>();
        params.put("requestName", requestname);
        // 主表信息
        params.put("mainData", getFormMainData());
        // 明细表数据，可选
        // params.put("detailData",getFormDetailData());

        // 流程id
        params.put("workflowId", "44");
        // 新建流程是否默认提交到第二节点，可选值为[0 ：不流转 1：流转 (默认)]
        params.put("otherParams", "{\"isnextflow\": \"0\"}");
        // 如果isnextflow为1，提交时的签字意见
        params.put("remark", "restful接口创建流程测试");
        try {
            System.out.println("================");
            System.out.println("url: " + url);
            System.out.println("params: " + params);
            System.out.println("heads: " + heads);
//            String res = OkHttpUtil.okPostBodyHeader(url, params, heads);
//            System.out.println("创建流程返回数据： " + res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 主表数据
     * <p>
     * 附件上传 包含base64, http等
     * 包含浏览框数据，单行文本数据
     *
     * @return
     */
    private String getFormMainData() {
        List<WorkflowRequestTableField> mainData = new ArrayList<>();

        //附件上传字段
//        field1.setFieldName("tpsc");
//        List<Map<String, String>> fileInfo = new ArrayList<>();
//        Map<String, String> fileItem1 = new HashMap<>();
//        fileItem1.put("fileName", "t01d0b9e7421ad092b5.jpg");
//        String str1 = "base64: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";
//        fileItem1.put("filePath", str1);
//        fileInfo.add(fileItem1);
//
//        Map<String, String> fileItem2 = new HashMap<>();
//        fileItem2.put("fileName", "scm_8802d1a86_4.png");
//        fileItem2.put("filePath", "http://scm.ddimg.mobi/scm_8802d1a86_4.png");
//        //fileInfo.add(fileItem2);
//
//        field1.setFieldValue(JSONObject.toJSONString(fileInfo));
//        mainData.add(field1);

        //单行文本字段
        WorkflowRequestTableField field2 = new WorkflowRequestTableField();
        field2.setFieldName("description"); // 数据库字段名
        field2.setFieldValue("测试事由"); // 值
        mainData.add(field2);

        WorkflowRequestTableField field3 = new WorkflowRequestTableField();
        field3.setFieldName("remark");
        field3.setFieldValue("测试备注");
        mainData.add(field3);

        WorkflowRequestTableField field4 = new WorkflowRequestTableField();
        field4.setFieldName("amount");
        field4.setFieldValue("1002");
        mainData.add(field4);

        //单人力资源字段
//        WorkflowRequestTableField field3 = new WorkflowRequestTableField();
//        field3.setFieldName("drlzy");
//        field3.setFieldValue("2979");
//        mainData.add(field3);

        return JSONObject.toJSONString(mainData);
    }

    /**
     * 明细数据
     *
     * @return
     */
    private String getFormDetailData() {
        List<WorkflowDetailTableInfoEntity> details = new ArrayList<>();

        //明细信息
        WorkflowDetailTableInfoEntity detail1 = new WorkflowDetailTableInfoEntity();
        detail1.setTableDBName("formtable_main_1356_dt1");

        //明细数据
        List<WorkflowRequestTableRecord> detailRows = new ArrayList<>();
        WorkflowRequestTableRecord row1 = new WorkflowRequestTableRecord();

        //明细行数据
        List<WorkflowRequestTableField> rowDatas = new ArrayList<>();

        //行字段数据
        WorkflowRequestTableField field1 = new WorkflowRequestTableField();
        field1.setFieldName("dhwb");
        field1.setFieldValue("test01");

        rowDatas.add(field1);
        WorkflowRequestTableField field2 = new WorkflowRequestTableField();
        field2.setFieldName("drl");
        field2.setFieldValue("2978,2979");
        rowDatas.add(field2);
        row1.setRecordOrder(0);
        row1.setWorkflowRequestTableFields(rowDatas);
        detailRows.add(row1);

        detail1.setWorkflowRequestTableRecords(detailRows);
        details.add(detail1);
        return JSONObject.toJSONString(details);

    }

    private static String currentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(LocalDateTime.now());
    }
}
