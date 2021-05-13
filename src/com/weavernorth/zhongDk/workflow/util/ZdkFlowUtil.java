package com.weavernorth.zhongDk.workflow.util;

import com.alibaba.fastjson.JSONObject;
import com.weavernorth.zhongDk.workflow.vo.ZdkJsonVO;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.RecordSet;
import weaver.general.MD5;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ZdkFlowUtil {
    public static final String IP = "10.120.4.1:8080";
    public static final String URL = "http://" + IP + "/cidp/ws/intfsServiceWS";
    private static final String KEY = "OA_HD_TODO";
    private static final Log LOGGER = LogFactory.getLog(ZdkFlowUtil.class);

    /**
     * 发送xml给主数据的webservice接口
     *
     * @param sendXml 拼接好的xml
     */
    public static String postJson(String sendXml, String method) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Host", IP);
            connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            connection.setRequestProperty("SOAPAction", method);
            connection.setRequestProperty("Content-Length", String.valueOf(sendXml.length()));
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(sendXml.getBytes(StandardCharsets.UTF_8));
            outputStream.close();

            int responseCode = connection.getResponseCode();
            LOGGER.info("responseCode: " + responseCode);

            if (200 == responseCode) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuilder = new StringBuilder();
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    stringBuilder.append(str);
                }

                bufferedReader.close();
                String returnStr = stringBuilder.toString();
                LOGGER.info("主数据返回信息：" + returnStr);
                return returnStr;
            }
        } catch (Exception e) {
            LOGGER.error("主数据web接口发送数据异常： " + e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return "";
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

        return jsonObject.toJSONString();
    }

    /**
     * 校验接口调用权限
     *
     * @param loginId   登录名
     * @param timestamp 时间戳
     * @param sign      加密串
     */
    public static JSONObject apiCheck(String loginId, String timestamp, String sign) {
        try {
            // 非空判断
            if (StringUtils.isBlank(sign)) {
                return createReturnObj("500", "认证失败-sign为空");
            }

            // 时间验证
            long currentTimeMillis = System.currentTimeMillis();
            long longValue = getLongValue(timestamp);
            long result = currentTimeMillis - longValue;
            long min = result / 1000 / 60;
            if (min > 15) {
                return createReturnObj("500", "认证失败-sign已过期");
            }

            // 加密方式验证
            String md5ofStr = new MD5().getMD5ofStr(loginId + KEY + timestamp);
            LOGGER.info("oa加密后的sign：" + md5ofStr);
            if (!md5ofStr.equals(sign)) {
                return createReturnObj("500", "认证失败-sign错误");
            }

            // 创建人验证
            RecordSet recordSet = new RecordSet();
            loginId = StringUtils.isBlank(loginId) ? "error" : loginId;
            recordSet.executeQuery("select id from HrmResource where loginid = lower('" + loginId + "') and status < 4");
            if (!recordSet.next()) {
                return createReturnObj("500", "创建人登录名不存在");
            }
        } catch (Exception e) {
            LOGGER.error("apiCheck异常： " + e);
            return createReturnObj("500", "认证失败-校验接口调用权限异常");

        }
        return createReturnObj("200", "认证通过");
    }

    public static JSONObject createReturnObj(String status, String message) {
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("status", status);
        jsonObject.put("message", message);
        return jsonObject;
    }

    private static long getLongValue(String time) {
        try {
            return Long.parseLong(time);
        } catch (Exception e) {
            return -1;
        }
    }

}
