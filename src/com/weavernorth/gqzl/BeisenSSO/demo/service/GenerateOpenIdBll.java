package com.weavernorth.gqzl.BeisenSSO.demo.service;


import com.weavernorth.gqzl.BeisenSSO.demo.domain.Openid;
import com.weavernorth.gqzl.BeisenSSO.oidcsdk.provider.BeisenTokenProvider;
import com.weavernorth.gqzl.BeisenSSO.oidcsdk.utility.SafeTools;

import java.util.HashMap;

public class GenerateOpenIdBll {

    /**
     *  生成
     * @param openid
     * @return
     */
    public Openid GenerateOpenId(Openid openid) {
        if (openid.IsEmpty()) {
            openid = GetDefaultOpenId();
        }
        try {
            String header = BeisenTokenProvider.GetHeader(openid.getAlg(), openid.getPublic_key());
            // 用户自定义claims
            HashMap<String, Object> cls = new HashMap<String, Object>();
            String[] cls_list = openid.getStr_cls().split(",");
            for (int i = 0; i < cls_list.length; i++) {
                String item = cls_list[i];
                String[] kv = (SafeTools.StringIsNullOrEmpty(item) ? "" : item).split("=");
                if (!SafeTools.StringIsNullOrEmpty(item) && kv.length == 2)
                    cls.put(kv[0], kv[1]);
            }
            String fm = "yyyy-MM-dd HH:mm:ss";
            // 构建header
            String payload = BeisenTokenProvider.GetPayload(openid.getIss(), openid.getSub(), openid.getAud(), SafeTools.Date2TimeStamp(openid.getExp(), fm), SafeTools.Date2TimeStamp(openid.getIat(), fm), cls);
            String id_token = BeisenTokenProvider.GetIdToken(header, payload, openid.getPrivate_key());
            openid.setId_token(id_token);
        } catch (Exception e) {

        }
        return openid;
    }

    /**
     * 默认值初始化
     * @return
     */
    public Openid GetDefaultOpenId() {
        String exp = "", iat = "";
        long l_iat = SafeTools.getNowTimeStamp();
        long l_exp = l_iat + 60 * 60 * 24;
        exp = SafeTools.TimeStamp2Date(l_exp, "yyyy-MM-dd HH:mm:ss");
        iat = SafeTools.TimeStamp2Date(l_iat, "yyyy-MM-dd HH:mm:ss");
        Openid openid = new Openid("RS256", "oa.your-website.com", "101013063", "234593", "appid=100,uty=email,url_type=0,isv_type=0", prikey, pubkey, exp, iat);
        return openid;
    }

    public final String pubkey = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIDANBgkqhkiG9w0BAQEFAAOCAQ0AMIIBCAKCAQEAg9k/faGVExcQ0LWvnbfy\n" +
            "naehrJkw0FD3W5RAQFeXjRN0s/vFHER40GUDkfNMOyu04KAfnAHZ0EH9Ndn90dsj\n" +
            "g+MhMCRPl8HtlSkJfYmqenfRrsYLc+auQRdW3uDWv41dLO+Zechsmuo1VefFeTUp\n" +
            "EX5ee1LI/WLNhv5vCkZ8LaIviErdcYDqgv+8ZJitE5k3itwSE2DMiE7K+x0NG3rZ\n" +
            "AHfGltSpQCmhMuDZBNWLYkwJ0OsH9022kYUgxtgMD+Rsh87ImSBVrO6f5CLBjE2x\n" +
            "fkC4gJXe+8Wv5ZEC6ySJACOisZ6DvuU2UOnFFeFhSLztXjOV3gdonA/nQIIX/Skz\n" +
            "yQIBAw==\n" +
            "-----END PUBLIC KEY-----";
    public final String prikey = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEoQIBAAKCAQEAg9k/faGVExcQ0LWvnbfynaehrJkw0FD3W5RAQFeXjRN0s/vF\n" +
            "HER40GUDkfNMOyu04KAfnAHZ0EH9Ndn90dsjg+MhMCRPl8HtlSkJfYmqenfRrsYL\n" +
            "c+auQRdW3uDWv41dLO+Zechsmuo1VefFeTUpEX5ee1LI/WLNhv5vCkZ8LaIviErd\n" +
            "cYDqgv+8ZJitE5k3itwSE2DMiE7K+x0NG3rZAHfGltSpQCmhMuDZBNWLYkwJ0OsH\n" +
            "9022kYUgxtgMD+Rsh87ImSBVrO6f5CLBjE2xfkC4gJXe+8Wv5ZEC6ySJACOisZ6D\n" +
            "vuU2UOnFFeFhSLztXjOV3gdonA/nQIIX/SkzyQIBAwKCAQAFfmKlPBDgy6CzXOdR\n" +
            "PP9xPFFnMQyzWLT5Ji1YA6ZQi3oyKn2L2C+zWYrQv3iCdyeJXAFRKr5orWqM6RU+\n" +
            "E8wlfswMrC37qBSQ4bW6kGcab6i8nZXPfvHtYPjz9AjypePh31EPva8xHxeOPv2P\n" +
            "t4xguplFI3MKjsiQSpn1wu/XPAfBDRIDAoRCA1NWwVgy07bc9Yq4EBVZUZrW2tfD\n" +
            "anI+VvBAsdt8jMpGcbgUc3QaWGw7esNrEeY78/RrJbxXbeYPxyyjBN/62uHR1mJF\n" +
            "GSG64WJiUC/ehHdD77VhmBmG152NHfkd0lvMwZt3v2teDGlpU9U/Ui5jNfcNoDZM\n" +
            "ZifbAoGBANwIdSY/9Lw968hE+TleJuQYR0tQHMwYzOCro79ZbanK9DFhZ74sbPuC\n" +
            "SY9T4Xi4xHxcIY7FueBQmVXLevn1R5GmwgRT3zvvjBqiwin/EeZeXPFGe724v02N\n" +
            "Mg7gJiUbxW2Z0yI0G5o361IZPVWapSyK1LjxS8Xvl/dYYdwqUcYZAoGBAJlmnAbp\n" +
            "T8gUxCP9fy7xmS67j8WBwgAP22Yq9RNwEnldXQ8kwFdmia8V7gfGITAxPWfhRycn\n" +
            "5kCiDODv02noT0lmYJT8zBo43VmVR2ES4KGawo4ClaRmmzP55fLku/9AJ2/c/Aa/\n" +
            "EQDm19/Csfrc7d7zgt79B0lfaMahGySpQ7ExAoGBAJKwThl/+H1+nTAt+3uUGe1l\n" +
            "hNzgEzK7M0BybSo7nnEx+CDrmn7ISKesMQo366XQgv2SwQnZJpWLEOPc/KajhQvE\n" +
            "gVg36if1CBHB1saqC+7pk0uEUn57KjOzdrSVbsNn2PO74hbNZ7wlR4wQ045nGMhc\n" +
            "jdCg3S6fuqTllpLG4S67AoGAZkRoBJuKhWMtbVOqH0u7dH0Kg6vWqrU87sdODPVh\n" +
            "pj4+ChiAOkRbyg6er9lrdXYo7+uExMVEKxazQJ/iRprfhkRAY1MyvCXo5mOE62Hr\n" +
            "FmcsXqxjwu8SIqaZTJh9VNVvn+ioBH9gq0SP6oHL/JNJP00B6f4E25TwhGtnbcYt\n" +
            "IMsCgYBxOpjkM3zE4HUmUXFrFSa+CU2sKHPox4vTdJrfK+6SSsyF2tve5CdRzeKX\n" +
            "0UTPN8yHW3p1NvuLUzO2e88/fD9v1TUHDfRRMlSKDNaF/h9r21IqtpH8r9tIYaxx\n" +
            "ul1JnEvM0LLvh5PCMuVoWNf/y2aHiUGPEujiaOGE44SOCUSNOA==\n" +
            "-----END RSA PRIVATE KEY-----";
}
