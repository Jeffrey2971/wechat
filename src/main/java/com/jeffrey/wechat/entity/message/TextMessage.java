package com.jeffrey.wechat.entity.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 文本类型消息
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Getter
@Setter
@ToString(callSuper = true)
@XStreamAlias("xml")
public class TextMessage extends BaseMessage {

    private String Content;

    public TextMessage(Map<String, String> requestMap, String content) {
        super(requestMap);
        this.setMsgType("text");
        this.Content = content;
    }
}
