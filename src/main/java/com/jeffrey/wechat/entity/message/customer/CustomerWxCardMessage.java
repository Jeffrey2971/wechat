package com.jeffrey.wechat.entity.message.customer;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class CustomerWxCardMessage extends BaseCustomerMessageType {

    private WxCard wxcard;

    public CustomerWxCardMessage(String touser, WxCard wxcard) {
        super(touser, "wxcard");
        this.wxcard = wxcard;
    }

    @Data
    private static class WxCard{
        private String card_id;
    }
}
