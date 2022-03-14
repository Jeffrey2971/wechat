package com.jeffrey.wechat.dao;

public interface WeChatServiceDao {

    /**
     * 查看是否为一个用户
     * @param openid userid
     * @return 是否为一个用户
     */
    boolean isUser(String openid);

}
