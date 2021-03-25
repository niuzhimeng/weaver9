package com.engine.nacos.entity;


import com.engine.nacos.constant.PushMsgConstant;
import com.engine.nacos.util.ObjectUtils;

public class PushMsg {

    /**
     * OA系统
     */
    private String ecology;

    /**
     * 消息推送接口
     */
    private String pushMessageInterface;

    /**
     * 标题
     */
    private String title;


    /**
     * 内容
     */
    private String context;


    /**
     * 消息来源
     */
    private String messageType;


    /**
     * 消息类型id
     */
    private String messageGroupType;


    /**
     * PC端链接
     */
    private String pcUrl;

    /**
     * PC端链接后面带的值
     */
    private String pcUrlParam;


    /**
     * 移动端链接
     */
    private String appUrl;

    /**
     * 移动端链接后面带的值
     */
    private String appUrlParam;

    /**
     * 链接编码或者加密方式
     */
    private String urlEnCode;


    /**
     * 接收人登录名、编号
     */
    private String userType;


    /**
     * 接收人（字符串形式）
     */
    private String userIds;


    /**
     * 消息接收人的分隔符（仅当消息接收人的形式为字符串时有效）
     */
    private String separator;


    /**
     * 接收人（列表形式）
     */
    private String userIdList;


    /**
     * 自定义参数
     */
    private String custom;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getPcUrl() {
        return pcUrl;
    }

    public void setPcUrl(String pcUrl) {
        this.pcUrl = pcUrl;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    public String getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(String userIdList) {
        this.userIdList = userIdList;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getEcology() {
        return ecology;
    }

    public void setEcology(String ecology) {
        this.ecology = ecology;
    }

    public String getMessageGroupType() {
        return messageGroupType;
    }

    public void setMessageGroupType(String messageGroupType) {
        this.messageGroupType = messageGroupType;
    }

    public String getPcUrlParam() {
        return pcUrlParam;
    }

    public void setPcUrlParam(String pcUrlParam) {
        this.pcUrlParam = pcUrlParam;
    }

    public String getAppUrlParam() {
        return appUrlParam;
    }

    public void setAppUrlParam(String appUrlParam) {
        this.appUrlParam = appUrlParam;
    }

    public String getUrlEnCode() {
        return urlEnCode;
    }

    public void setUrlEnCode(String urlEnCode) {
        this.urlEnCode = urlEnCode;
    }

    public String getPushMessageInterface() {
        return pushMessageInterface;
    }

    public void setPushMessageInterface(String pushMessageInterface) {
        this.pushMessageInterface = pushMessageInterface;
    }

    // 对象通过读取配置文件实例化
    private static class PushMsgHolder{
        private static PushMsg instance = ObjectUtils.newInstance(PushMsgConstant.WEAVER_PUSH_MESSAGE, PushMsg.class);
    }

    /**
     * Holder 单例化模式
     * 内部类只有在外部类被调用才加载，从而实现了延迟加载
     * 线程安全，且不用加锁。
     * @return
     */
    public static PushMsg getInstance(){
        return PushMsgHolder.instance;
    }


    // 建造者模式实例化
   /* public PushMsg() {
    }

    public PushMsg(PushMsgBuilder pushMsgBuilder) {
        this.ecology = pushMsgBuilder.ecology;
        this.title = pushMsgBuilder.title;
        this.context = pushMsgBuilder.context;
        this.messageType=pushMsgBuilder.messageType;
        this.messageGroupType = pushMsgBuilder.messageGroupType;
        this.pcUrl = pushMsgBuilder.pcUrl;
        this.pcUrlParam = pushMsgBuilder.pcUrlParam;
        this.appUrl = pushMsgBuilder.appUrl;
        this.appUrlParam = pushMsgBuilder.appUrlParam;
        this.urlEnCode = pushMsgBuilder.urlEnCode;
        this.userType = pushMsgBuilder.userType;
        this.userIds = pushMsgBuilder.userIds;
        this.separator=pushMsgBuilder.separator;
        this.userIdList = pushMsgBuilder.userIdList;
        this.custom = pushMsgBuilder.custom;

    }

    *//**
     * 建造者模式建造WeaPermission
     *//*
    public static class PushMsgBuilder {


        *//**
         * OA系统
         *//*
        private String ecology;

        *//**
         * 标题
         *//*
        private String title;


        *//**
         * 内容
         *//*
        private String context;

        *//**
         * 消息来源
         *//*
        private String messageType;


        *//**
         * 消息类型id
         *//*
        private String messageGroupType;


        *//**
         * PC端链接
         *//*
        private String pcUrl;

        *//**
         * PC端链接后面带的值
         *//*
        private String pcUrlParam;

        *//**
         * 移动端链接
         *//*
        private String appUrl;


        *//**
         * 移动端链接后面带的值
         *//*
        private String appUrlParam;

        *//**
         * 链接编码或者加密方式
         *//*
        private String urlEnCode;

        *//**
         * 接收人登录名或者是编号
         *//*
        private String userType;

        *//**
         * 接收人（字符串形式）
         *//*
        private String userIds;

        *//**
         * 消息接收人的分隔符（仅当消息接收人的形式为字符串时有效）
         *//*
        private String separator;


        *//**
         * 接收人（列表形式）
         *//*
        private String userIdList;

        *//**
         * 自定义参数
         *//*
        private String custom;


        public PushMsgBuilder setTitle(String title) {
            this.title = title;
            return this;
        }


        public PushMsgBuilder setContext(String context) {
            this.context = context;
            return this;
        }


        public PushMsgBuilder setPcUrl(String pcUrl) {
            this.pcUrl = pcUrl;
            return this;
        }


        public PushMsgBuilder setAppUrl(String appUrl) {
            this.appUrl = appUrl;
            return this;
        }

        public PushMsgBuilder setUserIds(String userIds) {
            this.userIds = userIds;
            return this;
        }


        public PushMsgBuilder setUserIdList(String userIdList) {
            this.userIdList = userIdList;
            return this;
        }



        public PushMsgBuilder setMessageType(String messageType) {
            this.messageType = messageType;
            return this;
        }


        public PushMsgBuilder setSeparator(String separator) {
            this.separator = separator;
            return this;
        }


        public PushMsgBuilder setCustom(String custom) {
            this.custom = custom;
            return this;
        }

        public PushMsgBuilder setUserType(String userType) {
            this.userType = userType;
            return this;
        }

        public PushMsgBuilder setEcology(String ecology) {
            this.ecology = ecology;
            return this;
        }

        public PushMsgBuilder setMessageGroupType(String messageGroupType) {
            this.messageGroupType = messageGroupType;
            return this;

        }


        public PushMsgBuilder setPcUrlParam(String pcUrlParam) {
            this.pcUrlParam = pcUrlParam;
            return this;
        }


        public PushMsgBuilder setAppUrlParam(String appUrlParam) {
            this.appUrlParam = appUrlParam;
            return this;
        }

        public PushMsgBuilder setUrlEnCode(String urlEnCode) {
            this.urlEnCode = urlEnCode;
            return this;
        }

        public static PushMsgBuilder create() {
            return new PushMsgBuilder();
        }

        public PushMsg build() {
            return new PushMsg(this);
        }
    }*/
}
