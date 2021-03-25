package com.engine.nacos.entity;

/**
 * @Description:
 * @Author: wlj
 * @Date: 2021/3/24 13:26
 * @Modified By:
 */
public class BeatInfo {

    private String ip;

    private String port;

    private String serviceName;

    private boolean scheduled;


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }
}
