package com.jeffrey.wechat.aop;

import com.jeffrey.wechat.entity.mapper.FeedBack;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Aspect
public class FeedBackAspect {

    private final JavaMailSender javaMailSender;

    private final SimpleMailMessage simpleMailMessage;

    @Autowired
    public FeedBackAspect(JavaMailSender javaMailSender, SimpleMailMessage simpleMailMessage) {
        this.javaMailSender = javaMailSender;
        this.simpleMailMessage = simpleMailMessage;
    }

    @AfterReturning(returning = "viewName", pointcut = "execution(* com.jeffrey.wechat.service.impl.UserQuestionServiceImpl.feedBackStatusMsg(..))")
    public void after(JoinPoint jp, String viewName) {
        if ("feedback_success".equals(viewName)) {

            FeedBack feedBack = (FeedBack) jp.getArgs()[0];
            StringBuilder content = new StringBuilder();

            content.append(String.format("时间：[%s]", feedBack.getCtime())).append("\n\n");
            content.append(String.format("问题标题：[%s]", feedBack.getTitle())).append("\n\n");
            content.append(String.format("问题详情：[%s]", feedBack.getMessage())).append("\n\n");
            content.append(String.format("联系方式：[%s]", feedBack.getWay())).append("\n\n");
            content.append(String.format("联系方式详情：[%s]", feedBack.getDetails())).append("\n\n");
            content.append(String.format("反馈用户 OpenId：[%s]", feedBack.getOpenid())).append("\n\n");

            simpleMailMessage.setSubject(String.format("--- 用户 [%s] 在 [%s] 时反馈问题或提出意见 ---", feedBack.getOpenid(), feedBack.getCtime()));
            simpleMailMessage.setText(content.toString());

            javaMailSender.send(simpleMailMessage);

        }
    }
}
