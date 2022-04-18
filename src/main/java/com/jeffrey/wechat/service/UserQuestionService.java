package com.jeffrey.wechat.service;

import com.jeffrey.wechat.entity.mapper.FeedBack;
import org.springframework.ui.Model;

public interface UserQuestionService {

    /**
     * 返回用户提交反馈后的状态内容
     * @param feedBack 反馈对象
     * @return 反馈状态
     */
    String feedBackStatusMsg(FeedBack feedBack, Model model);

    /**
     * 判断是否为一个用户，基于 basic_user_info 表
     * @param openid 查询的用户 openid
     * @return  传入的 Openid 是否在 basic_user_info 表中
     */
    boolean isUser(String openid);

    /**
     * 判断反馈是否已经存在
     * @param openid 用户唯一 id
     * @return 为 0 则表示不存在
     */
    Long feedBackIsExists(String openid);

}
