package com.jeffrey.wechat.controller;

import com.jeffrey.wechat.entity.FeedBack;
import com.jeffrey.wechat.service.UserQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 该 Controller 用于处理用户选择反馈模式以及反馈表单
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Controller
@RequestMapping("/question")
@Slf4j
public class UserQuestionController {

    @Autowired
    private UserQuestionService userQuestionService;

    @GetMapping
    public String chooseFeedBackMode(@RequestParam(required = false) String openid, Model model) {
        if (!StringUtils.hasText(openid)) {
            model.addAttribute("label1", "400");
            model.addAttribute("label2", "参数错误，请不要修改链接中的内容");
            return "4xx";
        }
        if (!userQuestionService.isUser(openid)) {
            model.addAttribute("label1", "401");
            model.addAttribute("label2", "请先长按以下二维码关注本公众号后再继续");
            return "4xx";
        }
        model.addAttribute("openid", openid);
        return "feedback";
    }


    /**
     * 用户选择了以表单的形式反馈（某些选项即使调用着做了响应判断也仍需再次判断，避免非法请求）
     *
     * @param feedBack 具体的反馈内容
     * @return 告知用户处理结果
     */
    @PostMapping(produces = "text/html;charset=UTF-8")
    public String getFormFeedBack(FeedBack feedBack, Model model) {

        // 使用反射机制校验页面项封装到 FeedBack 对象的字段
        for (Field field : feedBack.getClass().getDeclaredFields()) {

            field.setAccessible(true);

            if ("ctime".equals(field.getName())) {
                continue;
            }

            try {
                if ("openid".equalsIgnoreCase(field.getName()) && !StringUtils.hasText((String) field.get(feedBack))) {
                    model.addAttribute("label1", "400 Bad Request");
                    model.addAttribute("label2", "请求参数错误，请不要修改链接中的内容");
                    return "4xx";
                } else if ("openid".equalsIgnoreCase(field.getName()) && !userQuestionService.isUser((String) field.get(feedBack))) {
                    model.addAttribute("label1", "401");
                    model.addAttribute("label2", "请先长按以下二维码关注本公众号后再继续");
                    return "4xx";
                }

                if ("select".equals(field.getName()) && "Phone".equals(field.get(feedBack))) {
                    Matcher phoneReg = Pattern.compile("^(?:(?:\\+|00)86)?1\\d{10}$").matcher(feedBack.getDetails());
                    if (!phoneReg.matches()) {
                        model.addAttribute("label1", "400");
                        model.addAttribute("label2", "手机格式不正确");
                        return "4xx";
                    }
                }

                if (!StringUtils.hasText((String) field.get(feedBack))) {
                    model.addAttribute("label1", "403");
                    model.addAttribute("label2", String.format("表单项 %s 不能为空", fieldName(field.getName())));
                    return "4xx";
                }

            } catch (IllegalAccessException e) {
                log.error("发生异常", e);
            }
        }

        feedBack.setCtime(System.currentTimeMillis());

        return userQuestionService.feedBackStatusMsg(feedBack);
    }

    private String fieldName(String field) {
        if ("title".equals(field)) {
            return "问题简介";
        } else if ("message".equals(field)) {
            return "问题详情";
        } else if ("select".equals(field)) {
            return "联系方式不能为空";
        } else {
            return "联系方式详情";
        }
    }
}
