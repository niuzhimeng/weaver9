package com.mytest.http2web;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpExeWebTest {

    @Test
    public void test2() throws Exception {
        String strUrl = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx";
        String sendXml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://WebXml.com.cn/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <web:getRegionCountry/>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        URL url = new URL(strUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Host", "ws.webxml.com.cn");
        connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        connection.setRequestProperty("SOAPAction", "http://WebXml.com.cn/getRegionCountry");
        connection.setRequestProperty("Content-Length", String.valueOf(sendXml.length()));
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setDefaultUseCaches(false);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(sendXml.getBytes(StandardCharsets.UTF_8));
        outputStream.close();

        int responseCode = connection.getResponseCode();
        System.out.println("responseCode: " + responseCode);

        if (200 == responseCode) {//表示服务端响应成功
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String str = "";
            while ((str = bufferedReader.readLine()) != null) {
                stringBuilder.append(str);
            }

            bufferedReader.close();
            String voucherReturn = stringBuilder.toString();
            System.out.println(voucherReturn);

            Document doc = DocumentHelper.parseText(voucherReturn);
            Element rootElt = doc.getRootElement();
            List elements = rootElt.element("Body").element("getRegionCountryResponse").element("getRegionCountryResult").elements("string");
            System.out.println(elements.size());
            for (Object obj : elements) {
                Element element = (Element) obj;
                String textTrim = element.getTextTrim();
                System.out.println(textTrim);
            }

        }
    }


    @Test
    public void test3() throws Exception {
        String strUrl = "http://localhost:8080/services/HrmService";
        String sendXml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:hrm=\"http://localhost/services/HrmService\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <hrm:getHrmUserInfo>\n" +
                "         <hrm:in0></hrm:in0>\n" +
                "         <hrm:in1></hrm:in1>\n" +
                "         <hrm:in2></hrm:in2>\n" +
                "         <hrm:in3></hrm:in3>\n" +
                "         <hrm:in4></hrm:in4>\n" +
                "         <hrm:in5></hrm:in5>\n" +
                "      </hrm:getHrmUserInfo>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost:8080");
        headers.put("SOAPAction", "urn:weaver.hrm.webservice.HrmService.getHrmUserInfo");
        headers.put("Content-Type", "text/xml;charset=UTF-8");
        headers.put("Content-Length", sendXml.length() + "");
        String voucherReturn = postJsonHeader(strUrl, sendXml, headers);
        System.out.println(voucherReturn);

        Document doc = DocumentHelper.parseText(voucherReturn);
        Element rootElt = doc.getRootElement();
        Element element1 = rootElt.element("Body").element("getHrmUserInfoResponse").element("out");
        List elements = element1.elements();
        for (Object obj : elements) {
            Element element = (Element) obj;
            Element userBean = element.element("UserBean");
            String accounttype = userBean.elementTextTrim("accounttype");
            String lastname = userBean.elementTextTrim("lastname");
            System.out.println("lastname: " + lastname);
            System.out.println("accounttype: " + accounttype);
        }

    }

    public static String postJsonHeader(String url, String jsonStr, Map<String, String> headerMap) {
        MediaType xml = MediaType.parse("");
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody1 = RequestBody.create(xml, jsonStr);

        Request.Builder requestBuilder = new Request.Builder()
                .post(requestBody1)
                .url(url);
        // 添加请求头
        if (headerMap != null) {
            headerMap.forEach(requestBuilder::addHeader);
        }
        try {
            Response response = client.newCall(requestBuilder.build()).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

}
