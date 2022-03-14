package com.jeffrey.wechat.entity.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 音乐类型消息
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Getter
@Setter
@ToString(callSuper = true)
@XStreamAlias("xml")
public class MusicMessage extends BaseMessage {

    private Music music;

    public MusicMessage(Map<String, String> requestMap, Music music) {
        super(requestMap);
        setMsgType("music");
        this.music = music;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    static class Music {
        private String Title;
        private String Description;
        private String MusicUrl;
        private String HQMusicUrl;
        private String ThumbMediaId;
    }
}
