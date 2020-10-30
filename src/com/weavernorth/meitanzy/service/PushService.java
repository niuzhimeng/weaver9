package com.weavernorth.meitanzy.service;

public interface PushService {
    /**
     * 推送建模信息至合同系统
     *
     * @param ids   建模主表数据id  1,2,3
     * @param token 接口认证token
     * @return 接口返回信息
     */
    String push(String ids, String token);
}
