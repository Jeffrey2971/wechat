package com.jeffrey.wechat.entity.message.customer;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class CustomerMpnewsMessage extends BaseCustomerMessageType {

    private Mpnews mpnews;

    public CustomerMpnewsMessage(String touser, Mpnews mpnews) {
        super(touser, "mpnews");
        this.mpnews = mpnews;
    }

    @Data
    public static class Mpnews{
        private String media_id;
    }
}
