package com.jeffrey.wechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jeffrey.wechat.entity.mapper.UserInfo;
import com.jeffrey.wechat.mapper.UserQuestionServiceDao;
import com.jeffrey.wechat.entity.mapper.FeedBack;
import com.jeffrey.wechat.mapper.WeChatServiceDao;
import com.jeffrey.wechat.service.UserQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
@Slf4j
public class UserQuestionServiceImpl implements UserQuestionService {

    private final UserQuestionServiceDao userQuestionServiceDao;

    private final WeChatServiceDao weChatServiceDao;

    @Autowired
    public UserQuestionServiceImpl(UserQuestionServiceDao userQuestionServiceDao, WeChatServiceDao weChatServiceDao) {
        this.userQuestionServiceDao = userQuestionServiceDao;
        this.weChatServiceDao = weChatServiceDao;
    }

    @Override
    public boolean isUser(String openid) {
        return weChatServiceDao.selectCount(new QueryWrapper<UserInfo>().eq("openid", openid)) > 0;
    }

    @Override
    public Long feedBackIsExists(String openid) {
        return userQuestionServiceDao.selectCount(new QueryWrapper<FeedBack>().eq("openid", openid));
    }

    @Override
    public String feedBackStatusMsg(FeedBack feedBack, Model model) {

        if (userQuestionServiceDao.insert(feedBack) > 0) {
            log.info(feedBack.toString());
            model.addAttribute("title", "反馈成功");
            model.addAttribute("msg", "反馈成功");
            return "feedback_success";
        }

        return "error/4XX";
    }
}
