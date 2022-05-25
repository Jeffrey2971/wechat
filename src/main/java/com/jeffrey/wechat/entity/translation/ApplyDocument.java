package com.jeffrey.wechat.entity.translation;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class ApplyDocument {
    private int error_code;

    private String error_msg;

    private Data data;

    @lombok.Data
    public static class Data{
        private int charCount;

        private String fileId;

        private int amount;
    }
}
