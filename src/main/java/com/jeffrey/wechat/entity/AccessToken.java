package com.jeffrey.wechat.entity;

import lombok.Data;

/**
 * 封装了关于微信 AccessToken 的信息
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class AccessToken {

    /**
     * 微信 accessToken
     */
    private String access_token;

    /**
     * 微信 token 有效时间
     */
    private int expires_in;

    /**
     * 状态吗
     */
    private Integer errcode;

    /**
     * 状态信息
     */
    private String errmsg;
}
