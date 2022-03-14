package com.jeffrey.wechat.entity.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.*;

import java.util.Map;

/**
 * 图文类型消息
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Getter
@Setter
@ToString(callSuper = true)
@XStreamAlias("xml")
public class NewsMessage extends BaseMessage {

    private Articles articles;

    public NewsMessage(Map<String, String> requestMap, Articles articles) {
        super(requestMap);
        setMsgType("news");
        this.articles = articles;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    @AllArgsConstructor
    public static class Articles {

        private Item item;

        @Getter
        @Setter
        @ToString(callSuper = true)
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Item {
            private String Title;
            private String Description;
            private String PicUrl;
            private String Url;
        }

    }
}
