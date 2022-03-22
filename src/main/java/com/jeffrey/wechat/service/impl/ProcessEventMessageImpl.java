package com.jeffrey.wechat.service.impl;

import com.jeffrey.wechat.entity.message.BaseMessage;
import com.jeffrey.wechat.service.ProcessEventMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Slf4j
public class ProcessEventMessageImpl implements ProcessEventMessage {
    @Override
    public BaseMessage processSubscribe(Map<String, String> requestMap) {
        log.info(requestMap.get("FromUserName") + "：关注了");
        return null;
    }

    @Override
    public BaseMessage processUnsubscribe(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage processClick(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage processView(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage processPicPhotoOrAlbum(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage processScancodePush(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage processScancodeWaitMsg(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage processPicSysPhoto(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage processPicWeiXin(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage processLocationSelect(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage processViewMiniProgram(Map<String, String> requestMap) {
        return null;
    }
}
