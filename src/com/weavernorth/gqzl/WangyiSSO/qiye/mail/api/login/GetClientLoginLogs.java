package com.weavernorth.gqzl.WangyiSSO.qiye.mail.api.login;

import com.weavernorth.gqzl.WangyiSSO.rsa.HttpPost;
import com.weavernorth.gqzl.WangyiSSO.rsa.RSASignatureToQiye;

public class GetClientLoginLogs {
	public static void main(String[] args) {
		String priKey = "30820275020100300d06092a864886f70d01010105000482025f3082025b020100028181009691c94cbebf091bdf6d230bc877192ff65e33a81bcb94314a6f85e131cafba48832e4e9a653501d02d03ea148a519e9d1c9f3a6a4c514bd9996c1758ca1c7a11a6d05d569c739ec30d97ef004d9c06732ba3d904506d32433b731f4b989d65fecac93d5cea4178d114b27a860ae95110ec798ccddd47369030ebffac2dac87d02030100010281801f357893ffb5791ddde1b131f00b3f9d4163a53163117f55f827c58837d9eaeedf8c2d06810323c7df9d6a5de3c0d4ee321db9f300941908739d5bdc119a36d64b6cb400376d029983c27c41721a1e23d305d8de0345246ff342099cc952c59c3946f761e0898fefa584918e3ffb0162e29ca0fa5e81e264cedea21d1f0fd7c1024100e3933f3c1064e9b5ab484e9e13217a73b66ac517d93c7cc94068fcf755f3cd5e24b079676d212f6dc22787e4500660575415683ec3b0fba5401365212b745df3024100a96044ffa7886b3a5261d91a7e51251f7cbb31d6df84d7035c809f26a4b9a2ce8ab6bd176402720cd01d75ccc7d048aa93cec4799b90e81c1623bdd50bd92bcf02401f2b3badee50e3f18dd409707d0e48c73064cf39c4f713e03c8224a37b9238be7c27ef78cae990a567c2224a92eb4ca6a94d85499363da1378033006aafcf52102400cad38dccd84274a592a1b9ea0ff9fc0a88c3621b7def05cbf0a9d5ebe0b6ac4fc9acab23a78f8623f1d4558cb47aa78a1d162e11b24904ab1d980f6b6094dff02403d477f8106d62da2c89709dc7993bf8c626c20e5bf7340bd22f47d14cfa812b5aef26d634f0935765346ceb588a9c4f677e9a1bb9a6dbef8a7195ca1b52578e0";
		String domain = "hmail.my163mail.com";
		String product = "hmail_my163mail_com";
		
		String accounts = "gaga";
     	String start = "2019-05-29";
		String end = "2019-06-28";
		String url = "https://apihz.qiye.163.com/qiyeservice/api/login/getClientLoginLogs";
		String time = System.currentTimeMillis() + "";

		String sign = "accounts="+accounts+"&domain=" + domain + "&end="+end+"&product=" + product + "&start="+start+"&time=" + time;
		System.out.println(sign);
		sign = RSASignatureToQiye.generateSigature(priKey, sign);
		url = url + "?" + "accounts="+accounts+"&domain=" + domain + "&end="+end+"&product=" + product + "&start="+start+"&sign=" + sign + "&time=" + time;
		System.out.println(url);

		HttpPost hp = new HttpPost();
		String res = hp.post(url);
		System.out.println(res);
	}
}

