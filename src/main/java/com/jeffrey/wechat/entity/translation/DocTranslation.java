package com.jeffrey.wechat.entity.translation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 封装了微信模板消息发送的所需信息
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocTranslation {
    private String filename;
    private String openid;
    private String langFrom;
    private String langTo;
    private String mediaType;
    private long timestamp;
}
