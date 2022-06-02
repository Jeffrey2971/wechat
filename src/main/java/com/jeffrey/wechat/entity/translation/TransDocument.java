package com.jeffrey.wechat.entity.translation;

import lombok.Data;

/**
 * 文档申请翻译的返回值实体类（非文档翻译结果）
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class TransDocument {
    private String error_code;
    private String error_msg;
    private Data data;

    @lombok.Data
    public static class Data{
        private String requestId;
    }
}
