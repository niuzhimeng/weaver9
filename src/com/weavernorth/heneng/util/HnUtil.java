package com.weavernorth.heneng.util;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import weaver.general.BaseBean;

import java.io.IOException;
import java.util.Map;

public class HnUtil {
    public static final String tenantId = "1758889"; // 企业接入id
    public static final String clientId = "t7g0GjzLvkEwNQ3lUr00"; // 企业接入id
    private static final String clientSecret = "zAMUE3PEjXKglwPZD868"; // 企业接入使用秘钥
    private static final String GET_TOKEN_URL = "https://qyapi.weibangong.com/api/auth2/enterprise/accesstoken"; // 获取token的url
    public static final String GET_ALL_USER_URL = "https://qyapi.weibangong.com/open/security/v1/organizations/contacts"; // 获取所有员工的url
    public static final String GET_CLIENT_BY_USER_URL = "https://qyapi.weibangong.com/open/crm/v1/customer/list/owned"; // 获取某人负责的客户

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private BaseBean baseBean = new BaseBean();

    /**
     * post请求传输json数据与请求头
     *
     * @return
     */
    public String okPostJson(String url, String jsonStr, Map<String, String> header) {
        OkHttpClient okHttpClient = new OkHttpClient();

        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);
        if (header != null && header.size() > 0) {
            header.forEach(builder::header);
        }
        Request request = builder.build();
        Call call = okHttpClient.newCall(request);
        String returnStr = "";
        try {
            //同步调用,返回Response,会抛出IO异常
            Response response = call.execute();
            returnStr = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnStr;
    }

    public String getToken() {
        HnUtil hnUtil = new HnUtil();
        JSONObject params = new JSONObject();
        params.put("clientId", HnUtil.clientId);
        params.put("clientSecret", clientSecret);
        String tokenJsonStr = hnUtil.okPostJson(GET_TOKEN_URL, params.toJSONString(), null);
        baseBean.writeLog("获取token返回json： " + tokenJsonStr);

        JSONObject jsonObject = JSONObject.parseObject(tokenJsonStr);
        String status = jsonObject.getString("status");
        if (!"200".equals(status)) {
            return "";
        }
        return jsonObject.getJSONObject("data").getString("accessToken");
    }
}
