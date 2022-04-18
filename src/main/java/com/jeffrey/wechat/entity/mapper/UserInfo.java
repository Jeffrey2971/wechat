package com.jeffrey.wechat.entity.mapper;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@TableName("basic_user_info")
public class UserInfo {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer subscribe;
    private String openid;
    private String language;
    private Object subscribe_time;
    private String unionid;
    private String remark;
    private Integer groupid;
    private Object tagid_list;
    private String subscribe_scene;
    private Integer qr_scene;
    private String qr_scene_str;
}
