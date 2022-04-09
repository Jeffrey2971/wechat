package com.jeffrey.wechat.entity.mybatis;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class UserInfo {
    private Integer subscribe;
    private String openid;
    private String language;
    private Long subscribe_time;
    private String unionid;
    private String remark;
    private Integer groupid;
    private Object tagid_list;
    private String subscribe_scene;
    private Integer qr_scene;
    private String qr_scene_str;
}
