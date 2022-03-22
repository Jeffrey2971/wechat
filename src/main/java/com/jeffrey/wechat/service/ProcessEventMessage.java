package com.jeffrey.wechat.service;

import com.jeffrey.wechat.entity.message.BaseMessage;

import java.util.Map;

/**
 * 处理微信推送的事件类型消息
 */
public interface ProcessEventMessage {

    /**
     * 用户关注事件
     * @param requestMap requestMap
     * @return BaseMessage
     */
    BaseMessage processSubscribe(Map<String, String> requestMap);

    /**
     * 用户取关事件
     * @param requestMap requestMap
     * @return BaseMessage
     */
    BaseMessage processUnsubscribe(Map<String, String> requestMap);

    /**
     * 点击事件
     * @param requestMap requestMap
     * @return BaseMessage
     */
    BaseMessage processClick(Map<String, String> requestMap);

    /**
     * 视图事件
     * @param requestMap requestMap
     * @return BaseMessage
     */
    BaseMessage processView(Map<String, String> requestMap);

    /**
     * 弹出拍照或者相册发图的事件推送
     * @param requestMap requestMap
     * @return BaseMessage
     */
    BaseMessage processPicPhotoOrAlbum(Map<String, String> requestMap);

    /**
     *
     * @param requestMap requestMap
     * @return BaseMessage
     */
    BaseMessage processScancodePush(Map<String, String> requestMap);

    /**
     *
     * @param requestMap requestMap
     * @return BaseMessage
     */
    BaseMessage processScancodeWaitMsg(Map<String, String> requestMap);

    /**
     *
     * @param requestMap requestMap
     * @return BaseMessage
     */
    BaseMessage processPicSysPhoto(Map<String, String> requestMap);

    /**
     *
     * @param requestMap requestMap
     * @return BaseMessage
     */
    BaseMessage processPicWeiXin(Map<String, String> requestMap);

    /**
     *
     * @param requestMap requestMap
     * @return BaseMessage
     */
    BaseMessage processLocationSelect(Map<String, String> requestMap);

    /**
     *
     * @param requestMap requestMap
     * @return BaseMessage
     */
    BaseMessage processViewMiniProgram(Map<String, String> requestMap);
}
