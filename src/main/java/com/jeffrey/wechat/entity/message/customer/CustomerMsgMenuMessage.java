package com.jeffrey.wechat.entity.message.customer;

import lombok.Data;


/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class CustomerMsgMenuMessage extends BaseCustomerMessageType {

    private MsgMenu msgMenu;

    public CustomerMsgMenuMessage(String touser, MsgMenu msgMenu) {
        super(touser, "msgmenu");
        this.msgMenu = msgMenu;
    }

    @Data
    public static class MsgMenu{
        private String head_content;
        private java.util.List<CustomerMsgMenuMessage.MsgMenu.List> list;
        private String tail_content;

        @Data
        public static class List{
            private Integer id;
            private String content;
        }
    }
}
