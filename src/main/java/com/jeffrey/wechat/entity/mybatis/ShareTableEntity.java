package com.jeffrey.wechat.entity.mybatis;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("share")
public class ShareTableEntity {
    private Integer id;
    private String openid;
    private String shareLink;
    private Integer shareTotal;
    private String media_id;
}
