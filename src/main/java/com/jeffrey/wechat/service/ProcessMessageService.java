package com.jeffrey.wechat.service;

import com.jeffrey.wechat.entity.message.BaseMessage;

import java.util.Map;

/**
 * 所有响应的消息
 */
public interface ProcessMessageService {
    /**
     * 返回文本消息
     *
     * @param requestMap 请求信息
     * @return TextMessage
     */
    BaseMessage sendTextMessage(Map<String, String> requestMap);

    /**
     * 返回图片消息
     *
     * @param requestMap 请求信息
     * @return ImageMessage
     */
    BaseMessage sendImageMessage(Map<String, String> requestMap);

    /**
     * 返回语音消息
     *
     * @param requestMap 请求信息
     * @return VoiceMessage
     */
    BaseMessage sendVoiceMessage(Map<String, String> requestMap);

    /**
     * 返回视频消息
     *
     * @param requestMap 请求信息
     * @return VideoMessage
     */
    BaseMessage sendVideoMessage(Map<String, String> requestMap);

    /**
     * 返回音乐消息
     *
     * @param requestMap 请求信息
     * @return MusicMessage
     */
    BaseMessage sendMusicMessage(Map<String, String> requestMap);

    /**
     * 返回图文消息
     *
     * @param requestMap 请求信息
     * @return NewsMessage
     */
    BaseMessage sendNewsMessage(Map<String, String> requestMap);

    /**
     * 返回地理位置信息
     *
     * @param requestMap 请求信息
     * @return LocationMessage
     */
    BaseMessage sendLocationMessage(Map<String, String> requestMap);

    /**
     * 返回链接信息
     *
     * @param requestMap 请求信息
     * @return LinkMessage
     */
    BaseMessage sendLinkMessage(Map<String, String> requestMap);

    /**
     * 返回短视频消息
     *
     * @param requestMap 请求信息
     * @return ShortVideoMessage
     */
    BaseMessage sendShortVideoMessage(Map<String, String> requestMap);
}
