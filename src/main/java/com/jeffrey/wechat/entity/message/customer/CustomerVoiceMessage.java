package com.jeffrey.wechat.entity.message.customer;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class CustomerVoiceMessage extends BaseCustomerMessageType {

    private Voice voice;

    public CustomerVoiceMessage(String touser, Voice voice) {
        super(touser, "voice");
        this.voice = voice;
    }

    @Data
    public static class Voice{
        private String media_id;
    }
}
