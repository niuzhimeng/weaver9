package com.weavernorth.zhongJiJian;

import java.util.HashMap;
import java.util.Map;

public class ConnUtil {

    public static Map<String, String> colMap = new HashMap<>();

    static {
        colMap.put("xmjlbaxmjlryxx", "项目经理（备案项目经理）");
        colMap.put("bgxmjlryxx", "变更项目经理");
        colMap.put("xmzxjlryxx", "项目执行经理");
        colMap.put("bgxmzxjlryxx", "变更项目执行经理");
        colMap.put("xmjsfzrryxx", "项目技术负责人（备案人员）");

        colMap.put("bgxmjsfzrryxx", "变更项目技术负责人");
        colMap.put("xmsjjsfzrryxx", "项目实际技术负责人");
        colMap.put("bgxmsjjsfzrryxx", "变更项目实际技术负责人");
        colMap.put("xmfjlryxx", "项目副经理");
        colMap.put("bgxmfjlryxx", "变更项目副经理");

        colMap.put("xmkzjl", "项目控制经理");
        colMap.put("bgxmkzjl", "变更项目控制经理");
        colMap.put("xmaqjl", "项目安全经理");
        colMap.put("bgxmaqjl", "变更项目安全经理");

    }
}
