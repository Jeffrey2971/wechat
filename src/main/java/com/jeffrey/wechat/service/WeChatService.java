package com.jeffrey.wechat.service;

import java.util.List;
import java.util.Map;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public interface WeChatService {

    /**
     * 微信接入校验
     * @param timestamp 时间戳
     * @param nonce nonce
     * @param signature signature
     * @param echostr 加密后的结果需要和微信的结果一致
     * @return 校验是否正确
     */
    boolean check(String timestamp, String nonce, String signature, String echostr);

    /**
     * 解析微信发送的 inputStream 为 Map
     * @param context 请求体
     * @return Map<String, String> map
     */
    Map<String, String> parseRequestInputStream(String context) throws Exception;

    /**
     * 处理所有事件及响应数据，如发生异常应该返回一个 EmptyMessage
     * @param requestMap 请求信息
     * @return 响应信息
     */
    String getResponse(Map<String, String> requestMap) throws Exception;

    /**
     * 判断用户是否关注公众号（表 basic_user_info）
     * @param openid 用户唯一 openid
     * @return true / false
     */
    boolean isUser(String openid);

    /**
     * 获取所有用户基本信息
     * @return 用户信息集合
     */
    List<String> selectUserOpenIdList();
}
