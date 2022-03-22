package com.jeffrey.wechat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 封装了每次响应时的所有数据
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransResponseWrapper {

    /**
     * 用户 openid
     */
    private String openid;

    /**
     * 分段译文
     */
    private String transText;

    /**
     * 不分段图片译文
     */
    private String transSumText;

    /**
     * 图片实景翻译，base64 字符串，已在前缀添加 data:image/png;base64,
     */
    private String transImageBase64;

    /**
     * 分段原文
     */
    private String transOriginalText;

    /**
     * 不分段原文
     */
    private String transSumOriginalText;

    /**
     * 当前对象的过期时间
     */
    private Long expiredTimeStamp;

    /**
     * 反馈链接
     */
    private String feedBackLink;

    /**
     * 获取免费使用链接
     */
    private String getFreeLink;
}
