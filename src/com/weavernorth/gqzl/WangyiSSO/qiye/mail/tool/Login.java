package com.weavernorth.gqzl.WangyiSSO.qiye.mail.tool;


import com.weavernorth.gqzl.WangyiSSO.rsa.RSATool;

public class Login {

	public static String getLoginUrl(String email) {
		String priKey = "30820275020100300d06092a864886f70d01010105000482025f3082025b0201000281810096f029846fe1ce18b1aef687ccd2706d499d7bb6a77a24bb388818fe6c3ac7e5015d06f1c9446167bda9ebb426456d60079ce4b2cf268a317319c5d8a3593729368d41e331085804d9d2aae4e4447e1e3c4a254a27395c41d361d75e7a0fde1d9da061ad20fe07c9ae731e15786f10864e981e891df439c9c301b621757fb6c5020301000102818071cbcb616000bc237e02cd378142bf248635c749e466803eeed61d11e48344a5f12b33cb22d4fd22fba99a5d0de5961aaa62ad41ea4fc22c99fcba5f247864cef2fd2227b8f224a96342fe1769a1e07e7dbaaa4fba90b40e805c4c1dd8781fb34cbbcb3f272d330b68a2ae096262c8e8cb0eb3a3d6b73754f40d58b3e9453799024100ccc5088c9a5943a8fb9a04748921dd623ce9f311ec1aefb0ff17ee7b9e46563cae9555ae2f69fb18a372673ced5d49f79d3de64a3be78e012b2588657e3f9d2b024100bcb35bd9bcfbcb218f57788d79b528ff1247eaca5aedc51d914837db37f068be3520bedd53854d8bc0351a3841e3e27f0bc169fb5459ac3fbde8a6a27ed863cf02403c26489b9c9f49fbb2648c987acbc2cf1f9b8d361f87ce0eccf007b2b24c70fa8442039815ff68add51d3eec2bc1727fd4e5a0e6ff6fc64bdb93bbdc2c589d5f02401e29dd88d4b8bb23c632094734294d4058bfbe3c5284a779ae7b88d23602ebfb5baabc57ccbe06434c9a3072c5b593c31331fbaafd34c85ff80f74738d2b31e902402640fa969d158d5c30b417fae937dadbf8087a3015e67d4f57aeaa49365b58254584308c85b034e6928ea0ed4636543cefa52b593a77ad8fa771a5da292a00ff";
		String domain = "china-icv.cn";
		String time = System.currentTimeMillis() + "";
		String lang = "0";
		String account_name = email.substring(0,email.length()-13);
		String src = account_name + domain +  time ;
		RSATool rsa = new RSATool();
		String enc = rsa.generateSHA1withRSASigature(src, priKey);
		String url = "https://entryhz.qiye.163.com/domain/oa/Entry?domain=" + domain + "&account_name=" + account_name + "&time=" + time + "&language=" + lang+ "&enc=" + enc ;
		return url;
	}

}

