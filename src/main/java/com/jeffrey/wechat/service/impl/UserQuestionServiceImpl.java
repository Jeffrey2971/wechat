package com.jeffrey.wechat.service.impl;

import com.jeffrey.wechat.dao.WeChatServiceDao;
import com.jeffrey.wechat.entity.FeedBack;
import com.jeffrey.wechat.service.UserQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
@Slf4j
public class UserQuestionServiceImpl implements UserQuestionService {

    private WeChatServiceDao weChatServiceDao;

    @Override
    public boolean isUser(String openid) {
//        return weChatServiceDao.isUser(openid);
        log.info("UserQuestionServiceImpl 判断是否为用户暂时为 true");
        return true;
    }

    @Override
    public String feedBackStatusMsg(FeedBack feedBack, Model model) {
        log.info(feedBack.toString());
        model.addAttribute("title", "反馈成功");
        model.addAttribute("msg", "反馈成功");
        return "feedback_status";

    }
}
