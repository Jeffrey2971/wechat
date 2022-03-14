package com.jeffrey.wechat.entity.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 短视频类型消息
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Getter
@Setter
@ToString(callSuper = true)
@XStreamAlias("xml")
public class SmallVideoMessage extends BaseMessage {
    private String MediaId;
    private String ThumbMediaId;

    public SmallVideoMessage(Map<String, String> requestMap, String mediaId, String thumbMediaId) {
        super(requestMap);
        setMsgType("shortvideo");
        this.MediaId = mediaId;
        this.ThumbMediaId = thumbMediaId;
    }
}
