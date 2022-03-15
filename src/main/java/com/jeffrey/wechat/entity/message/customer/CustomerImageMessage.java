package com.jeffrey.wechat.entity.message.customer;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class CustomerImageMessage extends BaseCustomerMessageType {

    private Image image;

    public CustomerImageMessage(String touser, Image image) {
        super(touser, "image");
        this.image = image;
    }

    @Data
    private static class Image{
        private String media_id;
    }
}
