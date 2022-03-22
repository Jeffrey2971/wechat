package com.jeffrey.wechat.entity.message.customer;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class CustomerResponseStatusMessage {
    private Integer errcode;
    private String errmsg;
}
