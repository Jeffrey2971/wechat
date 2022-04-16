package com.jeffrey.wechat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeffrey.wechat.dao.UserQuestionServiceDao;
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
public class UserQuestionServiceImpl extends ServiceImpl<UserQuestionServiceDao, FeedBack> implements UserQuestionService {}
