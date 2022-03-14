package com.jeffrey.wechat.entity.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 图片消息类型
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Getter
@Setter
@ToString(callSuper = true)
@XStreamAlias("xml")
public class ImageMessage extends BaseMessage {

    private String PicUrl;
    private String MediaId;

    public ImageMessage(Map<String, String> requestMap, String picUrl, String mediaId) {
        super(requestMap);
        setMsgType("image");
        this.PicUrl = picUrl;
        this.MediaId = mediaId;
    }
}
