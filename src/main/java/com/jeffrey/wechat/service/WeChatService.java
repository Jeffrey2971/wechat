package com.jeffrey.wechat.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jeffrey.wechat.entity.mybatis.ShareTableEntity;
import com.jeffrey.wechat.entity.mybatis.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public interface WeChatService extends IService<UserInfo> {

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

}
