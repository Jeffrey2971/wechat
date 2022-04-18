package com.jeffrey.wechat.entity.mapper;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("user_use_total")
@NoArgsConstructor
@AllArgsConstructor
public class UserUseTotalEntity {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 查询的用户唯一 openid
     */
    private String openid;

    /**
     * 查询用户今日可使用次数
     */
    private Integer canUse;

    /**
     * 查询当前用户是否免费使用
     */
    private char freeUser;

    /**
     * 查询当前用户总使用量
     */
    private Integer allUse;

    /**
     * 查询是否已获取过临时次数
     */
    private char free;
}
