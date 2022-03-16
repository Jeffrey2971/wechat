package com.jeffrey.wechat.entity.message.customer;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class CustomerMiniprogrampageMessage extends BaseCustomerMessageType {

    private Miniprogrampage miniprogrampage;

    public CustomerMiniprogrampageMessage(String touser, Miniprogrampage miniprogrampage) {
        super(touser, "miniprogrampage");
        this.miniprogrampage = miniprogrampage;
    }

    @Data
    public static class Miniprogrampage {
        private String title;
        private String appid;
        private String pagepath;
        private String thumb_media_id;
    }
}
