package com.jeffrey.wechat.entity.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 地理位置类型消息
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Getter
@Setter
@ToString(callSuper = true)
@XStreamAlias("xml")
public class LocationMessage extends BaseMessage {
    private float LocationX;
    private float LocationY;
    private int Scale;
    private String Label;

    public LocationMessage(Map<String, String> requestMap, float locationX, float locationY, int scale, String label) {
        super(requestMap);
        setMsgType("location");
        this.LocationX = locationX;
        this.LocationY = locationY;
        this.Scale = scale;
        this.Label = label;
    }
}
