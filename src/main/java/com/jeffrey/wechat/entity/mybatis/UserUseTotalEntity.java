package com.jeffrey.wechat.entity.mybatis;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class UserUseTotalEntity {

    /**
     * ID
     */
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
