package com.jeffrey.wechat.entity.message.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

public class CustomerNewsMessage extends BaseCustomerMessageType {

    private News news;

    public CustomerNewsMessage(String touser, News news) {
        super(touser, "news");
        this.news = news;
    }

    @Data
    @AllArgsConstructor
    public static class News{

        private Articles articles;

        @Data
        @AllArgsConstructor
        public static class Articles{
            private String title;
            private String description;
            private String url;
            private String picurl;
        }
    }
}
