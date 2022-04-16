package com.jeffrey.wechat.entity.mybatis;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class ShareTableEntity {
    /**
     * Id 主键
     */
    private Integer id;

    /**
     * 用户 openId
     */
    private String openid;

    /**
     * 用户分享链接
     */
    private String shareLink;

    /**
     * 用户分享数量
     */
    private Integer shareTotal;

    /**
     * 媒体 Id
     */
    private String media_id;
}
