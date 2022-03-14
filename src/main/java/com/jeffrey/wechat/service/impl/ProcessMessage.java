package com.jeffrey.wechat.service.impl;


import com.jeffrey.wechat.entity.message.BaseMessage;
import com.jeffrey.wechat.entity.message.TextMessage;
import com.jeffrey.wechat.entity.translation.TranslationData;
import com.jeffrey.wechat.service.MessageService;
import com.jeffrey.wechat.translate.GetTranslateMetaData;

import java.util.Map;


/**
 *
 *
 * @author jeffrey
 * @since JDK 1.8
 */


public class ProcessMessage implements MessageService {


    @Override
    public BaseMessage sendTextMessage(Map<String, String> requestMap) {
        return new TextMessage(requestMap, "测试");
    }

    @Override
    public BaseMessage sendImageMessage(Map<String, String> requestMap) {
        for (Map.Entry<String, String> entry : requestMap.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        TranslationData metaData = GetTranslateMetaData.getData(requestMap.get("PicUrl"));
        System.out.println(metaData);
        return null;
    }

    @Override
    public BaseMessage sendVoiceMessage(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage sendVideoMessage(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage sendMusicMessage(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage sendNewsMessage(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage sendLocationMessage(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage sendLinkMessage(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage sendShortVideoMessage(Map<String, String> requestMap) {
        return null;
    }
}
