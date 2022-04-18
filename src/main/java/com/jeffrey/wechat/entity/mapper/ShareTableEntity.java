package com.jeffrey.wechat.entity.mapper;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@TableName("share")
@AllArgsConstructor
@NoArgsConstructor
public class ShareTableEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String openid;
    @TableField("share_link")
    private String shareLink;
    @TableField("share_total")
    private Integer shareTotal;
    @TableField("media_id")
    private String mediaId;
}
