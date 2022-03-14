package com.jeffrey.wechat.service;

import java.util.Map;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public interface WeChatService {

    boolean check(String timestamp, String nonce, String signature, String echostr);

    Map<String, String> parseRequestInputStream(String context);

    /**
     * 处理所有事件及响应数据
     * @param requestMap 请求信息
     * @return 响应信息
     */
    String getResponse(Map<String, String> requestMap);
}
