package com.jeffrey.wechat.entity.message.customer;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class CustomerMpnewsarticleMessage extends BaseCustomerMessageType {

    private Mpnewsarticle mpnewsarticle;

    public CustomerMpnewsarticleMessage(String touser, Mpnewsarticle mpnewsarticle) {
        super(touser, "mpnewsarticle");
        this.mpnewsarticle = mpnewsarticle;
    }

    @Data
    public static class Mpnewsarticle{
        private String article_id;
    }
}
