package com.weavernorth.saiwen.util;

public class SwUtil {

    public static String deleteZero(String code, String companyCode) {
        if ("104".equals(companyCode)) {
            int i = code.indexOf("|");
            String one = code.substring(0, i);
            String two = code.substring(i);
            two = two.replace("0|", "").replaceAll("\\|0$", "");
            code = one + two;
        }
        return code;
    }
}
