package com.jeffrey.wechat.entity;

import lombok.Data;

import java.util.List;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class UploadMediaResult {
    private String media_id;
    private String url;
    private List<Object> item;
}
