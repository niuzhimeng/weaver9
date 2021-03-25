package com.engine.nacos.util;

import com.engine.nacos.constant.PushMsgConstant;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StrUtils {

    /**
     * 字符串前后追加值
     */
    public static String addComma(String str, String param) {
        StringBuilder stringBuilder = new StringBuilder();
        if(!str.startsWith(param))
            stringBuilder.append(param);
        stringBuilder.append(str);
        if(!str.endsWith(param))
            stringBuilder.append(param);
        return stringBuilder.toString();
    }

    /**
     * 字符串是否包含，逗号分隔
     */
    public static boolean isCommaContains(String str, String param) {
        return addComma(str, PushMsgConstant.DEFAULT_SEPARATOR).contains(addComma(param, PushMsgConstant.DEFAULT_SEPARATOR));
    }

    /**
     * 字符串解析成Map
     * @param strs
     * @param separator1
     * @param separator2
     * @return
     */
    public static Map<String,Object> splitStr(String strs, String separator1, String separator2){
        Map<String,Object> splitMap = new HashMap<>();
        for (String str : strs.split(separator1)) {
            String[] prop = str.split(separator2);
            String key = null;
            String value = null;
            if (prop.length >= 1)
                key = prop[0];
            if (prop.length >= 2)
                value = prop[1];
            splitMap.put(key, value);
        }
        return splitMap;
    }


    /**
     * 过滤多语言标签
     * @param htmlStr
     * @return
     */
    public static String delHTMLTag(String htmlStr) {
        htmlStr = htmlStr.replaceAll("\\</p>|<br>|</br>|<br/>", "\n");

        htmlStr = htmlStr.replaceAll("&nbsp;", "");
        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式
        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签
        return htmlStr.trim();
    }


}
