package com.jeffrey.wechat.entity.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 链接消息类型
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Getter
@Setter
@ToString(callSuper = true)
@XStreamAlias("xml")
public class LinkMessage extends BaseMessage {
    private String Title;
    private String Description;
    private String Url;

    public LinkMessage(Map<String, String> requestMap, String title, String description, String url) {
        super(requestMap);
        setMsgType("link");
        this.Title = title;
        this.Description = description;
        this.Url = url;
    }
}
