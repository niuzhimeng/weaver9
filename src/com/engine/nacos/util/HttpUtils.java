package com.engine.nacos.util;

import com.engine.nacos.constant.HttpConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import weaver.general.Util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * java http 请求工具类
 */
public class HttpUtils {

    private static final Log logger = LogFactory.getLog(HttpUtils.class);

    /**
     * post请求，参数为json字符串
     *
     * @param url     请求地址
     * @param params  json字符串
     * @param headers 请求头
     * @return 响应
     */
    public static String doPost(String url, String params, Map<String, String> headers) {

        logger.error("===================doPost 请求请求接口参数：" + params);
        String result = null;

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            // 配置本次连接的Content-type，正文UTF-8编码
            post.setHeader("Content-Type", "application/json;charset=utf-8");
            if (headers != null)
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    post.setHeader(entry.getKey(), entry.getValue());
                }
            // HTTP正文内容
            StringEntity postingString = new StringEntity(params, "utf-8");
            // 设置正文内容
            post.setEntity(postingString);

            // 返回信息
            response = httpClient.execute(post);

            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity, "utf-8");
                logger.error("===================doPost 接口调用正常，返回信息：" + result);
            }
        } catch (Exception e) {
            logger.error("==============doPost 请求异常=====================");
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * post请求，参数为Map
     *
     * @param path
     * @param params
     * @param headers
     * @return
     */
    public static String doPost(String path, Map<String, Object> params, Map<String, String> headers) {
        try {
            String str;

            URL url = new URL(path);
            //打开和url之间的连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //请求方式
            conn.setRequestMethod("POST");
            //设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            if (headers != null)
                for (Map.Entry<String, String> entry : headers.entrySet())
                    conn.setRequestProperty(entry.getKey(), entry.getValue());


            //设置是否向httpUrlConnection输出，设置是否从httpUrlConnection读入，此外发送post请求必须设置这两个
            //最常用的Http请求无非是get和post，get请求可以获取静态页面，也可以把参数放在URL字串后面，传递给servlet，
            //post与get的 不同之处在于post的参数不是放在URL字串里面，而是放在http请求的正文内。
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //获取URLConnection对象对应的输出流

            // 请求参数 编码为 utf-8
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "utf-8");

            if (params != null)
                out.write(mapToStr(params));
            //缓冲数据
            out.flush();

            out.close();

            //获取URLConnection对象对应的输入流
            InputStream is = conn.getInputStream();
            //构造一个字符流缓存,接口返回结果 编码为utf-8
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));

            String result = "";
            while ((str = br.readLine()) != null) {
                result = str;
            }
            //关闭流
            is.close();
            //断开连接，最好写上，disconnect是在底层tcp socket链接空闲时才切断。。
            //固定多线程的话，如果不disconnect，链接会增多，直到收发不出信息。写上disconnect后正常如果正在被其他线程使用就不切断一些。
            conn.disconnect();
            logger.error("===================doPost 接口调用正常，返回信息：" + result);
            return result;
        } catch (Exception e) {
            logger.error("==============doPost 请求异常=====================");
            e.printStackTrace();
        } finally {

        }
        return null;
    }


    /**
     * get请求，参数放在map里
     *
     * @param url     请求地址
     * @param param   参数map
     * @param headers 请求头
     * @return 响应
     */
    public static String doGet(String url, Map<String, String> param, Map<String, String> headers) {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();

        CloseableHttpResponse response = null;
        try {
            URIBuilder builder = new URIBuilder(url);

            if (param != null)
                for (Map.Entry<String, String> entry : param.entrySet()) {
                    builder.addParameter(entry.getKey(), entry.getValue());
                }


            HttpGet get = new HttpGet(builder.build());

            if (headers != null)
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    get.setHeader(entry.getKey(), entry.getValue());
                }
            response = httpClient.execute(get);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity, "utf-8");
                logger.error("===================doGet 接口调用正常，返回信息：" + result);
            }

        } catch (Exception e) {
            logger.error("==============doGet 请求异常=====================");
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public static String doPut(String url, Map<String, String> param, Map<String, String> headers) {

        InputStream is = null;
        BufferedReader br = null;
        StringBuilder sBuilder = null;
        try {
            HttpClient httpClient = HttpClients.createDefault();

            URIBuilder builder = new URIBuilder(url);

            for (Map.Entry<String, String> entry : param.entrySet()) {
                builder.addParameter(entry.getKey(), entry.getValue());
            }
            HttpPut httpPut = new HttpPut(builder.build());


            if (headers != null)
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPut.setHeader(entry.getKey(), entry.getValue());
                }
            HttpResponse httpResponse = httpClient.execute(httpPut);
            //连接成功
            if (200 == httpResponse.getStatusLine().getStatusCode()) {
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
                br = new BufferedReader(new InputStreamReader(is));
                String tempStr;
                sBuilder = new StringBuilder();
                while ((tempStr = br.readLine()) != null) {
                    sBuilder.append(tempStr);
                }
                br.close();
                is.close();
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return sBuilder == null ? "" : sBuilder.toString();

    }


    /**
     * 链接地址后追加参数
     */
    public static String addUrlParam(String url, String... param) {
        if (param != null) {
            for (String str : param)
                if (StringUtils.isNotBlank(str)) {
                    if (url.indexOf(HttpConstant.QUESTION_MARK) >= 0) {
                        url += HttpConstant.CONNECTION_SYNBOL + str;
                    } else {
                        url += HttpConstant.QUESTION_MARK + str;
                    }
                }
        }
        return url;
    }

    /**
     * 将Map转换成字符串参数，用于POST GET 请求
     *
     * @param map
     * @return
     */
    public static String mapToStr(Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                stringBuilder.append(entry.getKey());
                if (entry.getValue() != null)
                    stringBuilder.append(HttpConstant.EQUAL_SIGN_SYNBOL).append(Util.null2String(entry.getValue()));
                stringBuilder.append(HttpConstant.CONNECTION_SYNBOL);
            }
        }
        if (stringBuilder.length() > 0)
            return stringBuilder.substring(0, stringBuilder.length() - 1);
        return null;
    }
}
