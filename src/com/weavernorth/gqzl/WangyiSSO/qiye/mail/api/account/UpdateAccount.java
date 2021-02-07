package com.weavernorth.gqzl.WangyiSSO.qiye.mail.api.account;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.weavernorth.gqzl.WangyiSSO.rsa.HttpPost; import com.weavernorth.gqzl.WangyiSSO.rsa.RSASignatureToQiye;

public class UpdateAccount {

	public static void main(String[] args) throws UnsupportedEncodingException {
		//根据企业实际修改，priKey为私钥，domain为域名，product即cid由网易提供
		String priKey = "30820275020100300d06092a864886f70d01010105000482025f3082025b02010002818100a44d57f355aa8fcdb459e8cff09dc18e4b231550ab42ad0a20fa20cb70c35c34919da23cfde1221ea866e1fb2df951a8ee2e11689930dd62a436cd45b03ffbee0eb2b85e417e4402c16573b0ca51eaf6c087929a76d53e7705054ea4cbd62b57ed3cbfe1060d5571ced9cfb51290f5aa17be16d2dd441d463a4d3e72c6dbf9ab020301000102818029aaa39f461e5711a7a7156f8669bb68468dc31e0e107ea98eeb5fddb7df13471196944a3b6818ab05b1f1a52d6788d9fa6d1c6516545a1065ecafe9f8648192d9580c4d9ca7f9b19b5a201c74e9ae6ab7bcc8a239d112b5af90b59be986ada8d92c3e38b39b7d0a4fa219d73875894bdb359bd9208d2950ad735f5dff5f0721024100da073e6fb5570ad36c6e4389b7a80c61468f1a8d50eecccbd6a8eed569c3322339cc93ac2b3b841a655c19683ffa1bbb1f5868d9304586a41a431192b4459d15024100c0eaba1b436d0e4738b086dcffb7302718625043d27cd501d5b9a334611c2a90954af7d23f1f316de3e8a6135d4472c5ce2b882a289d94e13c41ec09ae346bbf02404148fa012923af322fbdb4ab803ae9ba170eb2bcae07537b96036fc0a7b2b348a8fe011a04b8058e81db5a204547f715c905082040bc10dd02a0229af33005210240663272d0ca91554509950a7cbcc2b0b1a54b3fb60e25af39bf9d0e064b837e5105a5a74f2bbd3c94386e20bc9566135f126f213d24330691a33850f077e6b1450240253b993d3285394f93eb44a2cb9f94fadc5c1f59cdd0246482aab43b75d78d834b233908633f0701c18d464f5857343a0be296bd2a97161c54d0dae493d279de";
		String domain = "abc.com";
		String product = "abc_com";
		
		String account_name = "zhangsan";
		String addr_right = "0";
		String addr_visible = "0";
		String exp_time = "2020-01-01";
		String job_no = "5555";
		String mobile = "(56)2563";
		String nickname = "测试帐号";
		String time = System.currentTimeMillis() + "";
		
		String url = "https://apihz.qiye.163.com/qiyeservice/api/account/updateAccount";
		String sign = "account_name=" + account_name + "&addr_right="
				+ addr_right + "&addr_visible=" + addr_visible + "&domain="
				+ domain + "&exp_time=" + exp_time + "&job_no=" + job_no
				+ "&mobile=" + mobile + "&nickname=" + nickname
				+ "&product=" + product  + "&time=" + time;
		System.out.println(sign);
		sign = RSASignatureToQiye.generateSigature(priKey, sign);
		System.out.println(sign);
		url = url + "?" + "account_name=" + account_name + "&addr_right="
				+ addr_right + "&addr_visible=" + addr_visible + "&domain="
				+ domain + "&exp_time=" + exp_time + "&job_no=" + job_no
				+ "&mobile=" + mobile + "&nickname=" + URLEncoder.encode(nickname,"utf-8")
				+ "&product=" + product + "&sign=" + sign + "&time=" + time;
		System.out.println(url);
		HttpPost hp = new HttpPost();
		String res = hp.post(url);
		System.out.print(res);
		
	}

}
