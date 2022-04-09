package com.jeffrey.wechat.entity;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class QrCodeResult {
    private String ticket;
    private Long expire_seconds;
    private String url;
}
