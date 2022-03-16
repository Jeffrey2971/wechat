package com.jeffrey.wechat.entity.message.customer;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class CustomerMusicMessage extends BaseCustomerMessageType {

    private Music music;

    public CustomerMusicMessage(String touser, Music music) {
        super(touser, "music");
        this.music = music;
    }

    @Data
    public static class Music{
        private String title;
        private String description;
        private String musicurl;
        private String hqmusicurl;
        private String thumb_media_id;
    }
}
