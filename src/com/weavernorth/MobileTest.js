$(function () {
    var jkResults = new Array();
    var date = new Date();
    var mm = date.getSeconds() * 1000;
    $.readOnly("field25770");
    //提交校验
    customSubmit = function () {
        var reslut = true;
        for (var i = 0; i < jkResults.length; i++) {
            if (!jkResults[i].flag) {
                reslut = false;
                window.top.Dialog.alert(jkResults[i].msg);
                break;
            }
        }

        return reslut;
    }

    var ygbh = jQuery("#field25759").val();//field25716 员工编号
    var ksrq = jQuery("#field25767").val();//field25767 开始日期
    var jsrq = jQuery("#field25768").val();//field25768 结束日期
    if (ygbh != "" && ygbh != null && ksrq != "" && ksrq != null && jsrq != "" && jsrq != null) {
        jQuery.cust_ajax("/weavernorth/jsp/hrm/bgskq/BgskqBrjsqLsjl.jsp", {
            'ygbh': ygbh,
            'ksrq': ksrq,
            'jsrq': jsrq,
            'mm': mm
        }, false, function (data) {
            removedetail(0);
            var length = jkResults.length;
            jkResults[length] = {flag: false, msg: "接口请求中..."};
            if (typeof data.error != "undefined") {
                jkResults[length] = {flag: false, msg: "考勤接口访问失败"};
            } else {
                jkResults[length] = {flag: true, msg: ""};
            }
            var objt = $.parseJSON(data.resData);
            for (i = 0; i < objt.length; i++) {
                addRow0(0);
                jQuery("#field25772_" + i).val(objt[i].Kqde_Ext15);
                jQuery("#field25772_" + i + "span").text(objt[i].Kqde_Ext15);
                jQuery("#field25773_" + i).val(objt[i].Kqde_Ext16);
                jQuery("#field25773_" + i + "span").text(objt[i].Kqde_Ext16);
                jQuery("#field25774_" + i).val(objt[i].Kqde_Ext17);
                jQuery("#field25774_" + i + "span").text(objt[i].Kqde_Ext17);
                jQuery("#field25775_" + i).val(objt[i].Kqde_Ext18);
                jQuery("#field25775_" + i + "span").text(objt[i].Kqde_Ext18);
            }
        });
    }


    var dateSpan;
    var starttime;
    var endtime;
    /*var requestid = jQuery("#requestid").val();
    if(requestid != "" && requestid != null && requestid != -1 && requestid != undefined ){
        jQuery.cust_ajax("/weavernorth/jsp/hrm/bgskq/UpdBrjqjts.jsp",{'requestid':requestid},false,function(data){
            var length = jkResults.length;
            jkResults[length] = {flag:false,msg:"接口请求中..."};
            if(typeof data.error != "undefined"){
                jkResults[length] = {flag:false,msg:"考勤接口访问失败"};
            }else{
                jkResults[length] = {flag:true,msg:""};
            }
            jQuery("#field25770span").text(data.resMsg);
            jQuery("#field25770").val(data.resMsg);
        });
    }
    */
    //field25718 员工编号
    jQuery("#field25759").bindPropertyChange(function () {
        jQuery("#field25759").removeAttr("chk");
        jQuery("#field25770").removeAttr("chk");
        var type = true;
        var ygbh = jQuery("#field25759").val()//field25716 员工编号
        jQuery.cust_ajax("/weavernorth/jsp/hrm/bgskq/BgskqBrjYgxb.jsp", {'ygbh': ygbh}, false, function (data) {
            if (data.chk == 0) { //chk= 0 时 是男性
                jQuery("#field25759").attr({"chk": "0", "msg": "您是男性，不能申请哺乳假!"});
                window.top.Dialog.alert(data.msg);
                type = false;
            }
        })

        if (type == true) {
            jQuery.cust_ajax('/weavernorth/jsp/hrm/bgskq/BgskqBrjsq.jsp', {
                'ygbh': ygbh,
                'mm': mm
            }, false, function (data) {
                var length = jkResults.length;
                jkResults[length] = {flag: false, msg: "接口请求中..."};
                if (typeof data.error != "undefined") {
                    jkResults[length] = {flag: false, msg: "考勤接口访问失败"};
                } else {
                    jkResults[length] = {flag: true, msg: ""};
                }

                var obj2 = $.parseJSON(data.resData);
                var time = 0;
                if (data.resFlag == 0) {
                    jQuery("#field25766").val("");
                    jQuery("#field25766span").text("");
                } else {
                    for (var i = 0; i < obj2.length; i++) {
                        if (obj2[i].stock_type == 0) {
                            time += obj2[i].stock_remain_amount;
                            //time += obj2[i].stock_remain_amount;

                        }
                    }
                    jQuery("#field25766span").text(time.toFixed(2)); //可申请天数
                    jQuery("#field25766").val(time.toFixed(2));
                    //jQuery("#field25766").val(time/100);

                }
            });
        } else {
            jQuery("#field25766").val("");
        }

        var ksrq = jQuery("#field25767").val();//field25767 开始日期
        var jsrq = jQuery("#field25768").val();//field25768 结束日期
        jQuery.cust_ajax("/weavernorth/jsp/hrm/bgskq/BgskqBrjsqLsjl.jsp", {
            'ygbh': ygbh,
            'ksrq': ksrq,
            'jsrq': jsrq,
            'mm': mm
        }, false, function (data) {
            removedetail(0);
            var length = jkResults.length;
            jkResults[length] = {flag: false, msg: "接口请求中..."};
            if (typeof data.error != "undefined") {
                jkResults[length] = {flag: false, msg: "考勤接口访问失败"};
            } else {
                jkResults[length] = {flag: true, msg: ""};
            }
            var objt = $.parseJSON(data.resData);
            for (i = 0; i < objt.length; i++) {
                addRow0(0);
                jQuery("#field25772_" + i).val(objt[i].Kqde_Ext15);
                jQuery("#field25772_" + i + "span").text(objt[i].Kqde_Ext15);
                jQuery("#field25773_" + i).val(objt[i].Kqde_Ext16);
                jQuery("#field25773_" + i + "span").text(objt[i].Kqde_Ext16);
                jQuery("#field25774_" + i).val(objt[i].Kqde_Ext17);
                jQuery("#field25774_" + i + "span").text(objt[i].Kqde_Ext17);
                jQuery("#field25775_" + i).val(objt[i].Kqde_Ext18);
                jQuery("#field25775_" + i + "span").text(objt[i].Kqde_Ext18);
            }
        });
    })

    //field25765 开始日期 field25765span
    jQuery("#field25765").bindPropertyChange(function () {
        jQuery("#field25765").removeAttr("chk");
        jQuery("#field25770").removeAttr("chk");
        jQuery("#field25770span").text("");
        jQuery("#field25770").val("");
        var ygbm = jQuery("#field25759").val();//field25393 员工编号
        var ksrq = jQuery("#field25765").val();//field25765 开始日期field25765span
        var kssj = jQuery("#field25767").val();//field25767span 开始时间field25767span
        var jsrq = jQuery("#field25768").val();//field25768span 结束日期field25768span
        var jssj = jQuery("#field25769").val();//field25769span 结束时间field25769span
        var date1 = new Date(ksrq);
        var date2 = new Date(jsrq);
        dateSpan = date2 - date1;
        var end = jssj.split(':');
        endtime = parseInt(end[0] + end[1]);
        var start = kssj.split(':');
        starttime = parseInt(start[0] + start[1]);
        if (dateSpan < 0) {
            jQuery("#field25765").attr({"chk": "0", "msg": "时间选择错误，请重新选择时间"});
            window.top.Dialog.alert("时间选择错误，请重新选择时间");
        } else if (dateSpan == 0) {
            jQuery("#field25765").removeAttr("chk");
            jQuery("#field25767").removeAttr("chk");
            jQuery("#field25768").removeAttr("chk");
            jQuery("#field25769").removeAttr("chk");
            if (starttime > endtime) {
                jQuery("#field25765").attr({"chk": "0", "msg": "时间选择错误，请重新选择时间"});
                window.top.Dialog.alert("时间选择错误，请重新选择时间");
            } else {
                getBrjsqts();
                /*jQuery.cust_ajax('/weavernorth/jsp/hrm/bgskq/BgskqBrjsqTs.jsp',{'ygbm':ygbm,'ksrq':ksrq,'kssj':kssj,'jsrq':jsrq,'jssj':jssj},false,function(data){
                    if(data.resFlag == 0){
                        window.top.Dialog.alert(data.resMsg);
                    }else{
                        jQuery("#field25770span").text(data.resMsg);
                        jQuery("#field25770").val(data.resMsg);
                    }
                });*/
            }
        } else {
            getBrjsqts();
            /*jQuery.cust_ajax('/weavernorth/jsp/hrm/bgskq/BgskqBrjsqTs.jsp',{'ygbm':ygbm,'ksrq':ksrq,'kssj':kssj,'jsrq':jsrq,'jssj':jssj},false,function(data){
                if(data.resFlag == 0){
                    window.top.Dialog.alert(data.resMsg);
                }else{
                    jQuery("#field25770span").text(data.resMsg);
                    jQuery("#field25770").val(data.resMsg);
                }
            });*/
        }
    });
    // 开始时间
    jQuery("#field25767").bindPropertyChange(function () {
        jQuery("#field25767").removeAttr("chk");
        jQuery("#field25770").removeAttr("chk");
        jQuery("#field25770span").text("");
        jQuery("#field25770").val("");
        var ygbm = jQuery("#field25759").val();//field25393 员工编号
        var ksrq = jQuery("#field25765span").text();//field25765 开始日期field25765span
        var kssj = jQuery("#field25767span").text();//field25767span 开始时间field25767span
        var jsrq = jQuery("#field25768span").text();//field25768span 结束日期field25768span
        var jssj = jQuery("#field25769span").text();//field25769span 结束时间field25769span
        var date1 = new Date(ksrq);
        var date2 = new Date(jsrq);
        dateSpan = date2 - date1;
        var end = jssj.split(':');
        endtime = parseInt(end[0] + end[1]);
        var start = kssj.split(':');
        starttime = parseInt(start[0] + start[1]);
        if (dateSpan < 0) {
            jQuery("#field25767").attr({"chk": "0", "msg": "时间选择错误，请重新选择时间"});
            //window.top.Dialog.alert("时间选择错误，请重新选择时间");
        } else if (dateSpan == 0) {
            jQuery("#field25765").removeAttr("chk");
            jQuery("#field25767").removeAttr("chk");
            jQuery("#field25768").removeAttr("chk");
            jQuery("#field25769").removeAttr("chk");
            if (starttime > endtime) {
                jQuery("#field25767").attr({"chk": "0", "msg": "时间选择错误，请重新选择时间"});
                window.top.Dialog.alert("时间选择错误，请重新选择时间");
            } else {
                getBrjsqts();
                /*jQuery.cust_ajax('/weavernorth/jsp/hrm/bgskq/BgskqBrjsqTs.jsp',{'ygbm':ygbm,'ksrq':ksrq,'kssj':kssj,'jsrq':jsrq,'jssj':jssj},false,function(data){
                    if(data.resFlag == 0){
                        window.top.Dialog.alert(data.resMsg);
                    }else{
                        jQuery("#field25770span").text(data.resMsg);
                        jQuery("#field25770").val(data.resMsg);
                    }
                });*/
            }
        } else {
            getBrjsqts();
            /*jQuery.cust_ajax('/weavernorth/jsp/hrm/bgskq/BgskqBrjsqTs.jsp',{'ygbm':ygbm,'ksrq':ksrq,'kssj':kssj,'jsrq':jsrq,'jssj':jssj},false,function(data){
                if(data.resFlag == 0){
                    window.top.Dialog.alert(data.resMsg);
                }else{
                    jQuery("#field25770span").text(data.resMsg);
                    jQuery("#field25770").val(data.resMsg);
                }
            });*/
        }
    });
    //结束日期
    jQuery("#field25768").bindPropertyChange(function () {
        jQuery("#field25768").removeAttr("chk");
        jQuery("#field25770").removeAttr("chk");
        jQuery("#field25770span").text("");
        jQuery("#field25770").val("");
        var ygbm = jQuery("#field25759").val();//field25393 员工编号
        var ksrq = jQuery("#field25765span").text();//field25765 开始日期field25765span
        var kssj = jQuery("#field25767span").text();//field25767span 开始时间field25767span
        var jsrq = jQuery("#field25768span").text();//field25768span 结束日期field25768span
        var jssj = jQuery("#field25769span").text();//field25769span 结束时间field25769span
        var date1 = new Date(ksrq);
        var date2 = new Date(jsrq);
        dateSpan = date2 - date1;
        var end = jssj.split(':');
        endtime = parseInt(end[0] + end[1]);
        var start = kssj.split(':');
        starttime = parseInt(start[0] + start[1]);
        if (dateSpan < 0) {
            jQuery("#field25768").attr({"chk": "0", "msg": "时间选择错误，请重新选择时间"});
            //window.top.Dialog.alert("时间选择错误，请重新选择时间");
        } else if (dateSpan == 0) {
            jQuery("#field25765").removeAttr("chk");
            jQuery("#field25767").removeAttr("chk");
            jQuery("#field25768").removeAttr("chk");
            jQuery("#field25769").removeAttr("chk");
            if (starttime > endtime) {
                jQuery("#field25768").attr({"chk": "0", "msg": "时间选择错误，请重新选择时间"});
                //window.top.Dialog.alert("时间选择错误，请重新选择时间");
            } else {
                getBrjsqts();
                /*jQuery.cust_ajax('/weavernorth/jsp/hrm/bgskq/BgskqBrjsqTs.jsp',{'ygbm':ygbm,'ksrq':ksrq,'kssj':kssj,'jsrq':jsrq,'jssj':jssj},false,function(data){
                    if(data.resFlag == 0){
                        window.top.Dialog.alert(data.resMsg);
                    }else{
                        jQuery("#field25770span").text(data.resMsg);
                        jQuery("#field25770").val(data.resMsg);
                    }
                });*/
            }
        } else {
            getBrjsqts();
            /*jQuery.cust_ajax('/weavernorth/jsp/hrm/bgskq/BgskqBrjsqTs.jsp',{'ygbm':ygbm,'ksrq':ksrq,'kssj':kssj,'jsrq':jsrq,'jssj':jssj},false,function(data){
                if(data.resFlag == 0){
                    window.top.Dialog.alert(data.resMsg);
                }else{
                    jQuery("#field25770span").text(data.resMsg);
                    jQuery("#field25770").val(data.resMsg);
                }
            });*/
        }
    });
    //结束时间
    jQuery("#field25769").bindPropertyChange(function () {
        jQuery("#field25769").removeAttr("chk");
        jQuery("#field25770").removeAttr("chk");
        jQuery("#field25770span").text("");
        jQuery("#field25770").val("");
        var ygbm = jQuery("#field25759").val();//field25393 员工编号
        var ksrq = jQuery("#field25765span").text();//field25765 开始日期field25765span
        var kssj = jQuery("#field25767span").text();//field25767span 开始时间field25767span
        var jsrq = jQuery("#field25768span").text();//field25768span 结束日期field25768span
        var jssj = jQuery("#field25769span").text();//field25769span 结束时间field25769span
        var date1 = new Date(ksrq);
        var date2 = new Date(jsrq);
        dateSpan = date2 - date1;
        var end = jssj.split(':');
        endtime = parseInt(end[0] + end[1]);
        var start = kssj.split(':');
        starttime = parseInt(start[0] + start[1]);
        if (dateSpan < 0) {
            jQuery("#field25769").attr({"chk": "0", "msg": "时间选择错误，请重新选择时间"});
            //window.top.Dialog.alert("时间选择错误，请重新选择时间");

        } else if (dateSpan == 0) {
            jQuery("#field25765").removeAttr("chk");
            jQuery("#field25767").removeAttr("chk");
            jQuery("#field25768").removeAttr("chk");
            jQuery("#field25769").removeAttr("chk");
            if (starttime > endtime) {
                jQuery("#field25769").attr({"chk": "0", "msg": "时间选择错误，请重新选择时间"});
                //window.top.Dialog.alert("时间选择错误，请重新选择时间");
            } else {
                getBrjsqts();
                /*jQuery.cust_ajax('/weavernorth/jsp/hrm/bgskq/BgskqBrjsqTs.jsp',{'ygbm':ygbm,'ksrq':ksrq,'kssj':kssj,'jsrq':jsrq,'jssj':jssj},false,function(data){
                    if(data.resFlag == 0){
                        window.top.Dialog.alert(data.resMsg);
                    }else{
                        jQuery("#field25770span").text(data.resMsg);
                        jQuery("#field25770").val(data.resMsg);
                    }
                });*/
            }
        } else {
            getBrjsqts();
            /*jQuery.cust_ajax('/weavernorth/jsp/hrm/bgskq/BgskqBrjsqTs.jsp',{'ygbm':ygbm,'ksrq':ksrq,'kssj':kssj,'jsrq':jsrq,'jssj':jssj},false,function(data){
                if(data.resFlag == 0){
                    window.top.Dialog.alert(data.resMsg);
                }else{
                    jQuery("#field25770span").text(data.resMsg);
                    jQuery("#field25770").val(data.resMsg);
                }
            });*/
        }
    });
    //请假天数
    jQuery("#field25770").bindPropertyChange(function () {
        jQuery("#field25770").removeAttr("chk");
        var qjts = jQuery("#field25770").val();//请假天数
        var kqjts = jQuery("#field25766").val(); //可请假天数
        if ((qjts - kqjts) > 0) {
            jQuery("#field25770").attr({"chk": "0", "msg": "请假天数不能超过可请假天数"});
            //window.top.Dialog.alert("请假天数不能超过可请假天数");
        }
    });


    function getBrjsqts() {
        var ygbm = jQuery("#field25759").val();//field25393 员工编号
        var ksrq = jQuery("#field25765").val();//field25765 开始日期field25765span
        var kssj = jQuery("#field25767").val();//field25767span 开始时间field25767span
        var jsrq = jQuery("#field25768").val();//field25768span 结束日期field25768span
        var jssj = jQuery("#field25769").val();//field25769span 结束时间field25769span
        if (ygbm != "" && ygbm != null && ksrq != "" && ksrq != null && kssj != "" && kssj != null && jsrq != "" && jsrq != null && jssj != "" && jssj != null) {
            jQuery.cust_ajax('/weavernorth/jsp/hrm/bgskq/BgskqBrjsqTs.jsp', {
                'ygbm': ygbm,
                'ksrq': ksrq,
                'kssj': kssj,
                'jsrq': jsrq,
                'jssj': jssj
            }, false, function (data) {
                var length = jkResults.length;
                jkResults[length] = {flag: false, msg: "接口请求中..."};
                if (typeof data.error != "undefined") {
                    jkResults[length] = {flag: false, msg: "考勤接口访问失败"};
                } else {
                    jkResults[length] = {flag: true, msg: ""};
                }
                if (data.resFlag == 0) {
                    window.top.Dialog.alert(data.resMsg);
                } else {
                    jQuery("#field25770span").text(data.resMsg);
                    jQuery("#field25770").val(data.resMsg);
                }
            });
        }

    }

});


