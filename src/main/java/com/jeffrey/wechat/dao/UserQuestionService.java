package com.jeffrey.wechat.dao;

import com.jeffrey.wechat.entity.FeedBack;

public interface UserQuestionService {

    /**
     * 存储反馈表单到数据库中
     * @param feedBack 反馈表单项
     * @return true / false
     */
    boolean saveFeedback(FeedBack feedBack);
}
