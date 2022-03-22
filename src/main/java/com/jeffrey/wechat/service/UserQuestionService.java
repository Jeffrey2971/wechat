package com.jeffrey.wechat.service;

import com.jeffrey.wechat.entity.FeedBack;
import org.springframework.ui.Model;

public interface UserQuestionService {

    /**
     * 判断是否为一个用户
     * @param openid 用户 openid
     * @return true / false
     */
    boolean isUser(String openid);

    /**
     * 返回用户提交反馈后的状态内容
     * @param feedBack
     * @return
     */
    String feedBackStatusMsg(FeedBack feedBack, Model model);
}
