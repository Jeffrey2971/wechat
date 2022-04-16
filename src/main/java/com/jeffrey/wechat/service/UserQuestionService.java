package com.jeffrey.wechat.service;

import com.jeffrey.wechat.entity.FeedBack;
import org.springframework.ui.Model;

public interface UserQuestionService {

    /**
     * 返回用户提交反馈后的状态内容
     * @param feedBack 反馈对象
     * @return
     */
    String feedBackStatusMsg(FeedBack feedBack, Model model);

    /**
     * 判断是否为一个用户
     * @param openid 用户唯一 Id
     * @return
     */
    boolean isUser(String openid);

    /**
     * 判断反馈是否已经存在
     * @param openid 用户唯一 id
     * @return
     */
    Long feedBackIsExists(String openid);

}
