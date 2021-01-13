package com.weavernorth.hualianFlow.util;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.RecordSet;
import weaver.general.MD5;
import weaver.general.TimeUtil;
import weaver.general.Util;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HlConnUtil {

    public static final String URL = "http://60.247.2.195:8081/api/WorkFlow/RequestWorkFlow";

    public static final String URI = "/api/WorkFlow/RequestWorkFlow";

    public static final String APP_KEY = "797768b551191c41aeb6a363a04ac787"; // 秘钥

    public static final String APP_ID = "OA20210111"; // 供应商ID

    public static final String USER_IP = "47.91.169.15"; // OA IP

    private static Log log = LogFactory.getLog(HlConnUtil.class);

    private static final Pattern pattern = Pattern.compile("<[a-zA-Z]+.*?>([\\s\\S]*?)</[a-zA-Z]*>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern pattern1 = Pattern.compile(">(.*?)</", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();

    /**
     * 校验接口调用权限
     *
     * @param authorization 请求头中的json
     * @param flowKey       接口标识
     */
    public static JSONObject apiCheck(String authorization, String flowKey) {
        try {
            // 非空判断
            if (StringUtils.isBlank(authorization)) {
                return createReturnObj("1", "认证失败-authorization为空");
            }

            JSONObject authorizationJson = JSONObject.parseObject(authorization);
            String workCode = authorizationJson.getString("workCode");
            String currentTime = authorizationJson.getString("currentTime");
            String token = authorizationJson.getString("token");

            // 时间验证
            long currentTimeMillis = System.currentTimeMillis();
            long longValue = getLongValue(currentTime);
            long result = currentTimeMillis - longValue;
            long min = result / 1000 / 60;
            if (min > 30) {
                return createReturnObj("1", "认证失败-token已过期");
            }

            // 加密方式验证
            String md5ofStr = new MD5().getMD5ofStr(currentTime + flowKey + workCode);
            log.info("oa加密后的token：" + md5ofStr);
            if (!md5ofStr.equals(token)) {
                return createReturnObj("1", "认证失败-token错误");
            }

            // 创建人验证
            RecordSet recordSet = new RecordSet();
            workCode = StringUtils.isBlank(workCode) ? "error" : workCode;
            recordSet.executeQuery("select id from hrmresource where loginid = ?", workCode);
            if (!recordSet.next()) {
                return createReturnObj("1", "创建人登录名不存在");
            }
        } catch (Exception e) {
            log.error("apiCheck异常： " + e);
            return createReturnObj("1", "认证失败-解析authorization异常");

        }
        return createReturnObj("0", "认证通过");
    }

    private static long getLongValue(String time) {
        try {
            return Long.parseLong(time);
        } catch (Exception e) {
            return -1;
        }
    }

    public static JSONObject createReturnObj(String status, String message) {
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("status", status);
        jsonObject.put("message", message);
        return jsonObject;
    }

    public static String getFlowMes(String returnStr) {
        String errStr = "";
        int returnInt = Util.getIntValue(returnStr);
        if (returnInt > 0) {
            errStr = "流程创建成功";
        } else if (returnInt == -1) {
            errStr = "流程创建失败";
        } else if (returnInt == -2) {
            errStr = "没有创建权限";
        } else if (returnInt == -3) {
            errStr = "流程创建失败";
        } else if (returnInt == -4) {
            errStr = "字段或表名不正确";
        } else if (returnInt == -5) {
            errStr = "更新流程级别失败";
        } else if (returnInt == -6) {
            errStr = "无法创建流程待办任务";
        } else if (returnInt == -7) {
            errStr = "流程下一节点出错，请检查流程的配置，在OA中发起流程进行测试";
        } else if (returnInt == -8) {
            errStr = "流程节点自动赋值操作错误";
        }
        return errStr;
    }

    public static String HmacSHA1Encrypt(String src, String key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("utf-8"), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(src.getBytes("utf-8"));
            return new String(Hex.encodeHex(rawHmac));
        } catch (Exception e) {
            log.error("HmacSHA1Encrypt加密方法异常： " + e);
        }
        return "";
    }

    public static byte[] HmacSHA1EncryptByte(String encryptText, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes("utf-8");
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
        // 生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance("HmacSHA1");
        // 用给定密钥初始化 Mac 对象
        mac.init(secretKey);

        byte[] text = encryptText.getBytes("utf-8");
        // 完成 Mac 操作
        return mac.doFinal(text);
    }

    public static String sendPost(String url, String jsonStr) {
        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);
        Request request = builder.build();
        Call call = okHttpClient.newCall(request);
        String returnStr = "";
        try {
            //同步调用,返回Response,会抛出IO异常
            Response response = call.execute();
            returnStr = response.body().string();
        } catch (IOException e) {
            returnStr = "error:" + e;
            log.error("签字意见发送异常sendPost() " + e);
        }
        return returnStr;
    }

    public static String getRemarkStr(String beforeStr) {
        beforeStr = beforeStr.replaceAll("(?i)<img.*?/>", "")
                .replaceAll("(?i)<a.*?</a>", "")
                .replaceAll("(?i)<table.*?</table>", "");
        String returnStr = "";
        if (beforeStr.startsWith("<p")) {
            StringBuilder stringBuilder = new StringBuilder();
            String[] split = beforeStr.split("<p");
            for (String s : split) {
                if ("".equals(s)) {
                    continue;
                }
                if (s.contains("<span")) {
                    Matcher matcher = pattern.matcher(s);
                    if (matcher.find()) {
                        stringBuilder.append(matcher.group(1).trim()).append(" ");
                    }
                } else {
                    Matcher matcher = pattern1.matcher(s);
                    if (matcher.find()) {
                        stringBuilder.append(matcher.group(1).trim()).append(" ");
                    }
                }
            }
            returnStr = stringBuilder.toString();
        } else {
            returnStr = beforeStr;
        }

        return returnStr.replace("&nbsp;", " ")
                .replaceAll("(?i)(<br/>)", " ")
                .replaceAll("\\s+", " ")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
                .replace("&#39;", "'");
    }

    /**
     * 获取接口验证用的sig
     */
    public static String getSig() {
        String sig = "";
        try {
            String dateString = TimeUtil.getCurrentDateString().replace("-", "");
            String srcStr = "appid=" + APP_ID + "&format=json&ts=" + dateString + "&userip=" + USER_IP;
            String base64Src = "POST&" + URLEncoder.encode(URI, "utf-8") + "&" + URLEncoder.encode(srcStr, "utf-8");
            byte[] bytes = HlConnUtil.HmacSHA1EncryptByte(base64Src, APP_KEY + "&");
            sig = new String(new Base64().encode(bytes));
        } catch (Exception e) {
            log.error("拼接sig异常getSig() " + e);
        }
        return sig;
    }

}
