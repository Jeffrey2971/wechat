package com.jeffrey.wechat.entity.message.customer;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class CustomerImageMessage extends BaseCustomerMessageType {

    public CustomerImageMessage(String touser, Image image) {
        super(touser, "image");
    }

    @Data
    public static class Image{
        private String media_id;
    }
}
