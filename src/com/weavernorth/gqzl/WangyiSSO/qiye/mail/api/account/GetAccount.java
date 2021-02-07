package com.weavernorth.gqzl.WangyiSSO.qiye.mail.api.account;

import java.io.UnsupportedEncodingException;

import com.weavernorth.gqzl.WangyiSSO.rsa.HttpPost; import com.weavernorth.gqzl.WangyiSSO.rsa.RSASignatureToQiye;

public class GetAccount {

	public static void main(String[] args) throws UnsupportedEncodingException {
		//根据企业实际修改，priKey为私钥，domain为域名，product即cid由网易提供
		String priKey = "30820276020100300d06092a864886f70d0101010500048202603082025c02010002818100b09f4dadb8cb01f11781be10800c62719b29b9f1d93bfd044bada8589ab0b5464d9d630e729ff3f3eb78e2dc20bcbdd119f2947c519dc2160a0d10bb78cef07385ed88dd8dba4694444be40d091fa8bf0dd507fe549ead9191d285ef1603bf946e0305bacb82a8d5f24800bd381829f1df34aac89837d0d119d751a1048bd6c502030100010281804d86ef92e4bd7f71b34ec8fdf726f8834387924e3d7b9e79273ae7dd54b16230a4db03e3109f7376cb127a0a03f8b51d36d58b25f2b15bc34ed0235b171146ebc20ea840ee7050e44b1a91a6dec450a60afdee4ab5d8a49277bace0896df214642a1731f2c999f8cf3bfeb1adc9097b3a2f2eabb0fc998e8da1f228c19ad80c1024100f46153b014908d5f7c69599b4e00151d2c536b234fdff5231d9d06c24622edbadc5b9b753dd9fa4294621334c6719edc7ba472ff417984cb0835fdb7d0c50f75024100b90534b11c7e9e280bd6edfe92b3b997b8a8e4089b797da014da53e778a00abf45e3c55b704d794f3b935634bbb220a6008e5c6134be33a76d43a1db1f349011024100ee4c61b0d7bcc53e9130a1a221834f136d7fc27f6289fe9e49682f890d9f4db5e2dea94ec00b684fb8259298e9d12d99ce2f63bba87af8948ae32b0f2d529b6902406e8a48afe45955be914c4906c6b9301210967f1f115ca58ca5c8c7464d10393b8bd3088f97ea01a72fedd81c4b63a213ebd32228456db9c8b7042848953d3f310240357ab9405b6de337ef7c91af5241dbaf5fd92a165dc49032fea8ce71711f5d661636c00d9a30039fd60b430d702693e2767b59f131e0cf32cc0258c2e54d1bc8";
		String domain = "abc.com";
		String product = "abc_com";

		String account_name = "test001";
		String time = System.currentTimeMillis() + "";
		
		String url = "https://apihz.qiye.163.com/qiyeservice/api/unit/getAccountList";
		String sign = "account_name=" + account_name + "&domain=" + domain + "&product=" + product + "&time=" + time;
		System.out.println(sign);
		sign = RSASignatureToQiye.generateSigature(priKey, sign);
		System.out.println(sign);
		url = url + "?" + "account_name=" + account_name + "&domain="
				+ domain  + "&product=" + product + "&sign=" + sign + "&time=" + time;
		System.out.println(url);
		HttpPost hp = new HttpPost();
		String res = hp.post(url);
		System.out.print(res);
		
	}

}
