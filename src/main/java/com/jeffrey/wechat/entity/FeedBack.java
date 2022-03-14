package com.jeffrey.wechat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 封装用户表单反馈结果
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedBack {
    private String title;
    private String message;
    private String select;
    private String way;
    private String details;
    private String table;
    private Long timeStamp;
}
