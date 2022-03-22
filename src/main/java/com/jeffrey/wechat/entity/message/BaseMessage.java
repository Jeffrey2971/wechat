package com.jeffrey.wechat.entity.message;

import lombok.*;

import java.util.Map;

/**
 * 所有消息类型的基础属性
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@ToString(callSuper = true)
@NoArgsConstructor
public abstract class BaseMessage {

    private String ToUserName;
    private String FromUserName;
    private Long CreateTime;
    private String MsgType;


    public BaseMessage(Map<String, String> requestMap){
        this.ToUserName = requestMap.get("FromUserName");
        this.FromUserName = requestMap.get("ToUserName");
        this.CreateTime = System.currentTimeMillis() / 1000;
    }
}
