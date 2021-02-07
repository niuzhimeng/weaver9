package com.weavernorth.gqzl.WangyiSSO.qiye.mail.api.archive;

import java.io.UnsupportedEncodingException;

import com.weavernorth.gqzl.WangyiSSO.rsa.HttpPost;
import com.weavernorth.gqzl.WangyiSSO.rsa.RSASignatureToQiye;

public class AddArchiveMailAccount {

	public static void main(String[] args) throws UnsupportedEncodingException {
		//根据企业实际修改，priKey为私钥，domain为域名，product即cid由网易提供
		String priKey = "30820276020100300d06092a864886f70d0101010500048202603082025c02010002818100d3ce364f4574d7e850d0aa9db620f611701704e1f3ffda444c79ebc51865335d134b1435d2da279c71d0e53eb89357633984b7b4913611496644d7faf88150af9687c74ade899c3208d98a14c270f90af2ae5c095b500e91f158a2fff83941f580bdb7ccf12624270481be6b0f029d5907ce85d4817e352324bfe738980a33ef020301000102818065313d534c3cd1b6857ddc35f340f62f489d46d0e93150c796be1d3d92352f80056ab6622cb5db7cec07c0aee1a24b7af58d09f3b3f8dff3706fedbc5323fd36b80c678386cd9ff89cf01487d65ea02182206a45d6f77ac1129bbafb9d1e0cbbedf7fc8ab44ecb72c7bac3468f69767ffb91cb3342eff0f6e887274e4fc7c6e9024100f1c8493edaf0eee436db88445eff5f942c25240a2472921f19e03e608245b9334e593d77f046248edf9f79017ed28be378deb3a771e65528c3f28675f5a60cfd024100e042aa06c32f2ef0fb6a1f8af33d7b36266694ee90e17a72fa4b2cba89fa287403056d5e0c1f035deff8e70257e1126fcb2e006d6bec72dca1323d2ca2d0ce5b02402251414430a33de51a7c326d31e6a15450a7bce3cba48f64e6b1536933545e23101dce81b592df2180500c46d00f6657951257018f4318173f28af3912f2cca902403b07f13f94734014f8e9076289029f0332fa7fab888181bcf03b878b96b43d9fc407d2b115d9b8062b1bc69b2c4cb790abd7f10edc80c1ff678314ed41f25acf0241008f510054ee5c1223111b2a32e82e08c1b070ac4d320679d78908188ad669b4c0ccf97806cbf955b45236aea3664611bbae7927edc27c1f13bca9c3c010a4324e";
		String domain = "hmail.my163mail.com";
		String product = "hmail_my163mail_com";

		String account_name = "max";
		String time = System.currentTimeMillis() + "";
		
		String url = "https://apihz.qiye.163.com/qiyeservice/api/archive/addArchiveMailAccount";
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
