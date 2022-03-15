package com.jeffrey.wechat.entity.message.customer;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class CustomerVideoMessage extends BaseCustomerMessageType {

    private Video video;

    public CustomerVideoMessage(String touser, Video video) {
        super(touser, "video");
        this.video = video;
    }

    @Data
    private static class Video{
        private String media_id;
        private String thumb_media_id;
        private String title;
        private String description;
    }
}
