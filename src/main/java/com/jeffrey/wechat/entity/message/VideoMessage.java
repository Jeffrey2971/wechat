package com.jeffrey.wechat.entity.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.*;

import java.util.Map;

/**
 * 视频类型消息
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Getter
@Setter
@ToString(callSuper = true)
@XStreamAlias("xml")
public class VideoMessage extends BaseMessage {
    private String MediaId;
    private String ThumbMediaId;


    public VideoMessage(Map<String, String> requestMap, String mediaId, String thumbMediaId) {
        super(requestMap);
        setMsgType("video");
        this.MediaId = mediaId;
        this.ThumbMediaId = thumbMediaId;
    }
}
