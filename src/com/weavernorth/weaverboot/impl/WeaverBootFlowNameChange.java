package com.weavernorth.weaverboot.impl;

import com.alibaba.fastjson.JSONObject;
import com.weaverboot.frame.ioc.anno.classAnno.WeaIocReplaceComponent;
import com.weaverboot.frame.ioc.anno.methodAnno.WeaReplaceAfter;
import com.weaverboot.frame.ioc.handler.replace.weaReplaceParam.impl.WeaAfterReplaceParam;
import com.weaverboot.tools.componentTools.table.WeaTableTools;
import com.weaverboot.tools.logTools.LogTools;
import com.weaverboot.weaComponent.impl.weaTable.column.impl.DefaultWeaTableColumn;
import com.weaverboot.weaComponent.impl.weaTable.table.impl.DefaultWeaTable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@WeaIocReplaceComponent
public class WeaverBootFlowNameChange {
    private static final Log MY_LOG = LogFactory.getLog(WeaverBootFlowNameChange.class);

    //这个是接口后置方法，大概的用法跟前置方法差不多，稍有差别
    //注解名称为WeaReplaceAfter
    //返回类型必须为String
    //参数叫WeaAfterReplaceParam，这个类前四个参数跟前置方法的那个相同，不同的是多了一个叫data的String，这个是那个接口执行完返回的报文
    //你可以对那个报文进行操作，然后在这个方法里return回去
    @WeaReplaceAfter(value = "/api/workflow/reqlist/splitPageKey", order = 1)
    public String after(WeaAfterReplaceParam weaAfterReplaceParam) {
        MY_LOG.info("修改前数据： " + weaAfterReplaceParam.getData());
        JSONObject jsonObject = JSONObject.parseObject(weaAfterReplaceParam.getData());
        String apiUrl = weaAfterReplaceParam.getApiUrl();
        //获取到接口报文中的sessionkey
        String sessionkey = jsonObject.getString("sessionkey");
        try {
            if (sessionkey.startsWith("0f57de4d-89bb-4b96-ac78-48ce9b834592")) { // 待办开头固定字符串
                // 只修改待办
                //因为待办事宜表格为标准表格，所以我们用defaultWeaTable来获取
                //在整个拦截过程中，WeaTableTools是一个非常关键的工具类
                //此处是根据sessionkey，反序列化表格类

                DefaultWeaTable defaultWeaTable = WeaTableTools.checkTableStringConfig(sessionkey, DefaultWeaTable.class);
                MY_LOG.info("defaultWeaTable: " + JSONObject.toJSONString(defaultWeaTable));
                //找到table中名为创建者的列，并用字段默认实现类DefaultWeaTableColumn去获取
                DefaultWeaTableColumn defaultWeaTableColumn = defaultWeaTable.readWeaTableColumnWithColumn("creater", DefaultWeaTableColumn.class);

                //将此类的列名修改为操作人员
                defaultWeaTableColumn.setText("创建的人");
                //将修改后的table，以sessionkey最为键值，覆盖原来缓存中的table数据
                WeaTableTools.setTableStringVal(sessionkey, WeaTableTools.toTableString(defaultWeaTable));
                MY_LOG.info("修改后数据： " + WeaTableTools.toTableString(defaultWeaTable));
            }

        } catch (Exception e) {
            LogTools.error("发生错误，原因为:" + e.getMessage());
        }
        //将原有接口报文返回
        return weaAfterReplaceParam.getData();
    }

    //这是接口前置方法，这个方法会在接口执行前执行
    //前值方法必须用@WeaReplaceBefore,这里面有两个参数，第一个叫value，是你的api地址
    //第二个参数叫order，如果你有很多方法拦截的是一个api，那么这个就决定了执行顺序
    //前置方法的参数为WeaBeforeReplaceParam 这个类，里面有四个参数，request，response，请求参数的map，api的地址
//    @WeaReplaceBefore(value = "/api/....", order = "1")
//    public void before(WeaBeforeReplaceParam weaBeforeReplaceParam) {
//        //一顿操作
//        //weaBeforeReplaceParam.setParamMap();
//        Map paramMap = weaBeforeReplaceParam.getParamMap();
//        baseBean.writeLog("weaverboot接口前参数： " + JSONObject.toJSONString(paramMap));
//
//    }
}
