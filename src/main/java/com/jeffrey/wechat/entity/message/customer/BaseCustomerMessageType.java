package com.jeffrey.wechat.entity.message.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@AllArgsConstructor
public abstract class BaseCustomerMessageType {
    private String touser;
    private String msgtype;
}
