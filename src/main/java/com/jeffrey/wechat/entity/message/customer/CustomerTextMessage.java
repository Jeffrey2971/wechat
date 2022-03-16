package com.jeffrey.wechat.entity.message.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */
public class CustomerTextMessage extends BaseCustomerMessageType {

    private Text text;

    public CustomerTextMessage(String openid, Text text) {
        super(openid, "text");
        this.text = text;
    }

    @Data
    @AllArgsConstructor
    public static class Text {
        private String content;
    }
}
