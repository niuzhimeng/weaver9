package com.engine.nacos.entity;

public enum EncryptEnum {
    URLEncoder("URLEncoder"), //编码
    Base64("Base64"),         //base64 加密
    Hexadecimal("Hexadecimal"), //16进制
    ;

    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    EncryptEnum(String name) {
        this.name = name;
    }

    public static EncryptEnum getEncryptEnum(String name) {
        for (EncryptEnum encryptEnum : EncryptEnum.values()) {
            if (encryptEnum.getName().equals(name))
                return encryptEnum;
        }
        return null;
    }


}
