package com.weavernorth.meitanzy.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weavernorth.meitanzy.service.PushService;
import com.weavernorth.meitanzy.util.ConnUtil;
import com.weavernorth.meitanzy.util.MeiTanConfigInfo;
import com.weavernorth.meitanzy.util.MtHttpUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.conn.RecordSet;

import java.util.Map;

/**
 * 合同签订信息上报接口
 */
public class HtqdService implements PushService {

    private static final Log LOGGER = LogFactory.getLog(HtqdService.class);

    @Override
    public String push(String ids, String token) {
        LOGGER.info("合同签订信息上报接口Start ================== 此次推送数据id： " + ids);

        try {
            RecordSet updateSet = new RecordSet();
            RecordSet detailSet = new RecordSet();
            RecordSet recordSet = new RecordSet();
            recordSet.executeQuery("select * from uf_Mkzy_htgl where id in (" + ids + ")");
            while (recordSet.next()) {
                JSONObject jsonObject = new JSONObject(true);
                String id = recordSet.getString("id");
                String htzbh = recordSet.getString("htzbh");
                jsonObject.put("contractUniqueId", MeiTanConfigInfo.DWBM.getValue() + "_" + htzbh); // 合同唯一标识
                jsonObject.put("contractType", recordSet.getString("htsjflbm")); // 合同类型
                jsonObject.put("contractSubject", ConnUtil.getWorkflowSelect(MeiTanConfigInfo.HTBD.getValue(), recordSet.getString("sjhtflcphjsmc"))); // 合同标的
                jsonObject.put("contractName", recordSet.getString("htmc")); // 合同名称
                jsonObject.put("contractSelfCode", htzbh); // 合同自编号

                jsonObject.put("relatedProjectName", recordSet.getString("xgxmmchz")); // 关联项目名称
                jsonObject.put("relatedProjectCode", recordSet.getString("glxmbh")); // 关联项目编号
                jsonObject.put("buyMethod", recordSet.getString("cgfs")); // 采购方式
                jsonObject.put("bidFile", ConnUtil.pushFileToFtp(recordSet.getString("zbtzs"), htzbh, "zbtzs")); // 中标通知书
                jsonObject.put("contractAmount", recordSet.getString("htjezgje")); // 合同金额/暂估金额

                jsonObject.put("valuationMode", recordSet.getString("jjfs")); // 计价方式
                jsonObject.put("currencyName", "人民币"); // 币种(传汉字)
                jsonObject.put("exchangeRate", recordSet.getString("hl")); // 汇率
                jsonObject.put("amountExplain", recordSet.getString("htjesm")); // 合同金额说明
                jsonObject.put("paymentDirection", recordSet.getString("szfx")); // 收支方向

                jsonObject.put("paymentType", recordSet.getString("szfx")); // 合同收/付款类型
                jsonObject.put("paymentMethod", recordSet.getString("htsfkfs")); // 合同收/付款方式
                jsonObject.put("isAdvancePayment", recordSet.getString("sfyysyfk")); // 是否有预收/预付款
                jsonObject.put("signingSubject", ConnUtil.getSysByFiled("departmentname", "hrmdepartment", recordSet.getString("wfqyzt1"))); // 我方签约主体
                jsonObject.put("signingSubjectCode", recordSet.getString("qyztbm")); // 签约主体编码

                jsonObject.put("creatorAccount", recordSet.getString("jbrzh")); // 经办人账号
                jsonObject.put("creatorName", ConnUtil.getSysByFiled("lastname", "hrmresource", recordSet.getString("jbrmc"))); // 经办人名称
                jsonObject.put("creatorDeptCode", recordSet.getString("jbbmbm")); // 经办部门编码
                jsonObject.put("creatorDeptName", ConnUtil.getSysByFiled("departmentname", "hrmdepartment", recordSet.getString("jbbm"))); // 经办部门
                jsonObject.put("performAddress", ConnUtil.getProvinceAndCityNameById(recordSet.getString("htlxd"))); // 合同履行地

                jsonObject.put("signAddress", ConnUtil.getProvinceAndCityNameById(recordSet.getString("htqsd"))); // 合同签署地
                jsonObject.put("contractPeriod", recordSet.getString("htqxlx")); // 合同期限类型
                jsonObject.put("performPeriod", recordSet.getString("htlxqx")); // 合同履行期限
                jsonObject.put("periodExplain", recordSet.getString("htqxsm")); // 期限说明
                jsonObject.put("ourIsAuth", recordSet.getString("wfsfsq")); // 是否授权（我方）

                jsonObject.put("authType", recordSet.getString("sqlx")); // 授权类型
                jsonObject.put("contractContent", recordSet.getString("htzynr")); // 合同主要内容

                // 合同正文
                jsonObject.put("contractText", ConnUtil.pushFileToFtp(recordSet.getString("htzw"), htzbh, "htzw"));
                // 合同立项依据
                jsonObject.put("contractGist", ConnUtil.pushFileToFtp(recordSet.getString("htlxyj"), htzbh, "htlxyj"));
                // 合同审批单
                jsonObject.put("contractApprovalForm", ConnUtil.pushFileToFtp(recordSet.getString("htspd"), htzbh, "htspd"));
                // 合同相关附件
                jsonObject.put("contractAttachment", ConnUtil.pushFileToFtp(recordSet.getString("htfj"), htzbh, "htfj"));

                // 相对方联系人
                JSONArray relOppositeInfoList = new JSONArray();
                JSONObject xdfObj = new JSONObject(true);
                xdfObj.put("oppositeUniqueId", recordSet.getString("xdfwybs")); // 相对方唯一标识
                xdfObj.put("oppositeName", ConnUtil.getSysByFiled("xdfmc", "uf_Mkzy_xdf", recordSet.getString("xdfmc"))); // 相对方名称
                xdfObj.put("oppositeRelName", recordSet.getString("xdflxr")); // 相对方联系人
                xdfObj.put("bankOfDeposit", recordSet.getString("yxmc")); // 银行名称
                xdfObj.put("bankAccount", recordSet.getString("yxzh")); // 银行账号

                xdfObj.put("bankAccountName", recordSet.getString("")); // 银行账户名
                relOppositeInfoList.add(xdfObj);
                jsonObject.put("relOppositeInfoList", relOppositeInfoList);

                // 印章信息
                JSONArray sealInfoList = new JSONArray();
                JSONObject sealInfo = new JSONObject(true);
                sealInfo.put("sealTime", recordSet.getString("yyrq")); // "用印日期
                sealInfo.put("sealType", recordSet.getString("yylx")); // 用印类型
                sealInfo.put("signNum", recordSet.getString("yyfs")); // 用印份数
                sealInfoList.add(sealInfo);
                jsonObject.put("sealInfoList", sealInfoList);

                // signInfoList拼接
                JSONArray signInfoList = new JSONArray();
                JSONObject signInfo = new JSONObject();
                signInfo.put("signTime", recordSet.getString("qdrq")); // 签订日期
                signInfo.put("ourName", ConnUtil.getSysByFiled("lastname", "hrmresource", recordSet.getString("wfqdr1"))); // 我方签订人
                signInfo.put("ownAuth", ConnUtil.pushFileToFtp(recordSet.getString("wfsqwts"), htzbh, "wfsqwts")); // 我方授权委托书
                signInfo.put("contractScanFile", ConnUtil.pushFileToFtp(recordSet.getString("htqswb"), htzbh, "htqswb")); // 签署文本
                signInfoList.add(signInfo);
                jsonObject.put("signInfoList", signInfoList);

                // signItemList拼接
                JSONArray signItemList = new JSONArray();
                JSONObject signItem = new JSONObject(true);
                signItem.put("oppositeCode", recordSet.getString("xdfwybs")); // 相对方唯一标识
                signItem.put("opptName", recordSet.getString("dfqdr")); // 对方签订人
                signItem.put("opptIsAuth", recordSet.getString("dfsfsq")); // 对方是否授权
                signItem.put("opptAuth", ConnUtil.pushFileToFtp(recordSet.getString("dfsqwts"), htzbh, "dfsqwts")); // 对方授权委托书
                signItemList.add(signItem);
                jsonObject.put("signItemList", signItemList);

                // relatedPartyList拼接
                JSONObject relatedParty = new JSONObject(true);
                relatedParty.put("isRelatedParty", recordSet.getString("sfglf")); // 是否关联方
                //relatedParty.put("rpName", recordSet.getString("glfmc")); // 关联方名称
                relatedParty.put("rpType", recordSet.getString("glflx")); // 关联方类型
                relatedParty.put("isRelatedDeal", recordSet.getString("sfgljy")); // 是否关联交易
                relatedParty.put("dealType", recordSet.getString("gljylx")); // 关联交易类型

                relatedParty.put("relatedDealItem", recordSet.getString("gljysx")); // 关联交易事项
                relatedParty.put("isIntertemporal", recordSet.getString("sfkqht")); // 是否跨期合同 1：是:0：否
                relatedParty.put("isImportantRelatedDeal", recordSet.getString("sfzdgljy")); // 是否重大关联交易
                relatedParty.put("isNeedPerfApprove", recordSet.getString("sfjlxgljysp")); // 是否经履行关联交易审批

                // 年度 预估金额 为明细表字段
                detailSet.executeQuery("select nd, ygjey from uf_Mkzy_htgl_dt3 where mainid = ?", id);
                JSONArray intertemporalList = new JSONArray();
                while (detailSet.next()) {
                    JSONObject intertemporal = new JSONObject(true);
                    intertemporal.put("intertemporalYear", detailSet.getString("nd")); // 年度
                    intertemporal.put("estimateAmount", detailSet.getString("ygjey")); // 预估金额
                    intertemporalList.add(intertemporal);
                }
                relatedParty.put("intertemporalList", intertemporalList);

                jsonObject.put("relatedParty", relatedParty);

                JSONObject allObj = new JSONObject();
                allObj.put("contractInfo", jsonObject.toJSONString());
                String sendJson = allObj.toJSONString();
                LOGGER.info("合同签订接口发送数据： " + sendJson);

                // 发送请求
                Map<String, String> headerMap = ConnUtil.getHeader(token);
                String returnStr = MtHttpUtil.postJsonHeader(MeiTanConfigInfo.QIAN_DING_URL.getValue(), sendJson, headerMap);
                LOGGER.info("合同签订信息上报接口返回信息： " + returnStr);

                // 更新推送返回信息
                String updateSql = "update uf_Mkzy_htgl set htqdfhzt = ?, htqddxxsbztfk = ? where id = ?";
                String[] message = ConnUtil.parseReturnJson(returnStr);
                updateSet.executeUpdate(updateSql, message[0], message[1], id);

            }
        } catch (Exception e) {
            LOGGER.error("合同签订信息上报接口异常： " + e);
        }

        LOGGER.info("合同签订信息上报接口End ==================");

        return "";
    }

}
