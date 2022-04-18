package com.jeffrey.wechat.entity.mapper;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 封装用户表单反馈结果
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@TableName("user_feedback")
@AllArgsConstructor
@NoArgsConstructor
public class FeedBack {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 问题标题
     */
    private String title;

    /**
     * 问题详情
     */
    private String message;

    /**
     * 联系方式
     */
    private String way;

    /**
     * 联系方式详情
     */
    private String details;

    /**
     * 反馈用户的 openid
     */
    private String openid;

    /**
     * 反馈问题的时间
     */
    private String ctime;
}
