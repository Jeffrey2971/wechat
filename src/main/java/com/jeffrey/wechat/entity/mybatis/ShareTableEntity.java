package com.jeffrey.wechat.entity.mybatis;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class ShareTableEntity {
    private Integer id;
    private String openid;
    private String shareLink;
    private Integer shareTotal;
    private String media_id;
}
