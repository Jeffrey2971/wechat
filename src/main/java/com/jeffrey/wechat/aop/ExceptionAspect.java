package com.jeffrey.wechat.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Aspect
@Slf4j
public class ExceptionAspect {

    private final JavaMailSender javaMailSender;

    private final MimeMessageHelper mimeMessageHelper;

    @Autowired
    public ExceptionAspect(JavaMailSender javaMailSender, MimeMessageHelper mimeMessageHelper) {
        this.javaMailSender = javaMailSender;
        this.mimeMessageHelper = mimeMessageHelper;
    }

    @Pointcut("@annotation(com.jeffrey.wechat.aop.ExceptionAOP)")
    public void pointCut() {}

    @AfterThrowing(value =
            "execution(* com.jeffrey.wechat.dao..*.*(..)) || " +
                    "execution(* com.jeffrey.wechat.controller..*.*(..)) || " +
                    "execution(* com.jeffrey.wechat.service..*.*(..))) ", throwing = "exception")
    public void throwExceptionSendMail(JoinPoint jp, Exception exception) {

        String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));

        log.info("在 [ {} ] 时发生了一个异常", datetime);

        log.error("异常信息：", exception);

        StringBuilder details = new StringBuilder();

        details.append(String.format("<h3>异常抛出时间：[ %s ]<h3 />", datetime));
        details.append(String.format("<h3>抛出异常的类型：[ %s ]<h3 />", exception.getClass()));
        details.append(String.format("<h3>相关用户 openid：[ %s ]<h3 />", jp.getArgs()[0]));
        details.append(String.format("<h3>相关方法：[ %s.%s ]</h3>", jp.getSignature().getDeclaringTypeName(), jp.getSignature().getName()));
        details.append("<h3>异常信息<h3/>");
        details.append("<b>").append(exception.getMessage()).append("</b>");

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            mimeMessageHelper.setSubject(String.format("---在 [%s] 时抛出了一个异常---", datetime));
            mimeMessageHelper.setText(details.toString(), true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        javaMailSender.send(mimeMessage);
    }
}
