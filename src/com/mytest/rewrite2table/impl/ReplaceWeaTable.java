package com.mytest.rewrite2table.impl;

import com.alibaba.fastjson.JSONObject;
import com.weaverboot.frame.ioc.anno.classAnno.WeaIocReplaceComponent;
import com.weaverboot.frame.ioc.anno.methodAnno.WeaReplaceAfter;
import com.weaverboot.frame.ioc.handler.replace.weaReplaceParam.impl.WeaAfterReplaceParam;
import com.weaverboot.tools.componentTools.table.WeaTableTools;
import com.weaverboot.tools.enumTools.weaComponent.WeaBelongEnum;
import com.weaverboot.tools.enumTools.weaComponent.WeaBooleanEnum;
import com.weaverboot.weaComponent.impl.weaTable.column.impl.DefaultWeaTableColumn;
import com.weaverboot.weaComponent.impl.weaTable.table.impl.DefaultWeaTable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@WeaIocReplaceComponent
public class ReplaceWeaTable {

    private static final Log MY_LOG = LogFactory.getLog(ReplaceWeaTable.class);

    @WeaReplaceAfter(value = "/api/workflow/reqlist/splitPageKey", order = 1)
    public String replace(WeaAfterReplaceParam weaAfterReplaceParam) {
        MY_LOG.info("待办table重写 修改前数据： " + weaAfterReplaceParam.getData());
        //将接口返回报文反序列化成jsonobject对象
        JSONObject jsonObject = JSONObject.parseObject(weaAfterReplaceParam.getData());
        //获取到接口报文中的sessionkey
        String sessionkey = jsonObject.getString("sessionkey");
        try {
            if (sessionkey.startsWith("0f57de4d-89bb-4b96-ac78-48ce9b834592")) { // 待办开头固定字符串
                //因为待办事宜表格为标准表格，所以我们用defaultWeaTable来获取
                //在整个拦截过程中，WeaTableTools是一个非常关键的工具类
                //此处是根据sessionkey，反序列化表格类
                DefaultWeaTable defaultWeaTable = WeaTableTools.checkTableStringConfig(sessionkey, DefaultWeaTable.class);
                MY_LOG.info("defaultWeaTable： " + JSONObject.toJSONString(defaultWeaTable));

                //找到table中名为创建者的列，并用字段默认实现类DefaultWeaTableColumn去获取
                DefaultWeaTableColumn defaultWeaTableColumn = defaultWeaTable.readWeaTableColumnWithColumn("creater", DefaultWeaTableColumn.class);
                //将此类的列名修改为操作人员
                defaultWeaTableColumn.setText("创建人nzm");

                DefaultWeaTableColumn myCol = new DefaultWeaTableColumn();
                myCol.setText("后端自定义列");
                myCol.setBelong(WeaBelongEnum.PC);
                myCol.setColumn("myCol");
                myCol.setDisplay(WeaBooleanEnum.TRUE);
                myCol.setWidth("10%");
                defaultWeaTable.getColumns().add(myCol);

                //将修改后的table，以sessionkey最为键值，覆盖原来缓存中的table数据
                WeaTableTools.setTableStringVal(sessionkey, WeaTableTools.toTableString(defaultWeaTable));
            }
        } catch (Exception e) {
            MY_LOG.error("发生错误，原因为:" + e);
        }
        //将原有接口报文返回
        return weaAfterReplaceParam.getData();
    }
}