package com.jeffrey.wechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jeffrey.wechat.mapper.UserQuestionServiceDao;
import com.jeffrey.wechat.entity.FeedBack;
import com.jeffrey.wechat.service.UserQuestionService;
import com.jeffrey.wechat.service.WeChatService;
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

    private final WeChatService weChatService;

    @Autowired
    public UserQuestionServiceImpl(UserQuestionServiceDao userQuestionServiceDao, WeChatService weChatService) {
        this.userQuestionServiceDao = userQuestionServiceDao;
        this.weChatService = weChatService;

    }

    @Override
    public boolean isUser(String openid) {
        return weChatService.isUser(openid);
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

        return "4xx";
    }
}
