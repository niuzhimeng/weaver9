package com.mytest;

/**
 * 首先枚举是一个特殊的class
 * 这个class相当于final static修饰，不能被继承
 * 他的构造方法强制被私有化，下面有一个默认的构造方法private ColorEnum();
 * 所有的枚举都继承自java.lang.Enum类。由于Java 不支持多继承，所以枚举对象不能再继承其他类
 */
public enum DanLiEnum {
    //每个枚举变量都是枚举类ColorEnum的实例，相当于RED=new ColorEnum（1），按序号来。
    //每个成员变量都是final static修饰
    INSTANCE;

    public void sysOk() {
        System.out.println("ok");
    }

//    public static DanLiEnum fromTypeName(String typeName) {
//        for (DanLiEnum type : DanLiEnum.values()) {
//            if (type.getTypeName().equals(typeName)) {
//                return type;
//            }
//        }
//        return null;
//    }
}
