package com.jeffrey.wechat.entity.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.*;

import java.util.Map;

/**
 * 语音类型消息
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@EqualsAndHashCode(callSuper = true)
@Data
@XStreamAlias("xml")
public class VoiceMessage extends BaseMessage {

    private String MediaId;
    private String Format;
    private String Recognition;

    public VoiceMessage(Map<String, String> requestMap, String mediaId, String format, String recognition) {
        super(requestMap);
        setMsgType("voice");
        this.MediaId = mediaId;
        this.Format = format;
        this.Recognition = recognition;
    }
}








