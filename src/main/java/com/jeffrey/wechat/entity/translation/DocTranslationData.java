package com.jeffrey.wechat.entity.translation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 封装翻译结果的实体类对象
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocTranslationData {
    private String error_code;
    private String error_msg;
    private String requestId;
    private String fileSrcUrl;
    private String sign;
    private String amount;
    private int charCount;
}
