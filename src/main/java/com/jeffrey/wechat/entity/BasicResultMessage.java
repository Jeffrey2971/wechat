package com.jeffrey.wechat.entity;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class BasicResultMessage {
    private Integer errcode;
    private String errmsg;
}
