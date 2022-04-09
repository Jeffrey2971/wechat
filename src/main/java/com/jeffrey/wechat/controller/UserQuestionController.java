package com.jeffrey.wechat.controller;

import com.jeffrey.wechat.entity.FeedBack;
import com.jeffrey.wechat.service.UserQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 该 Controller 用于处理用户选择反馈模式以及反馈表单
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Slf4j
@Controller
public class UserQuestionController {

    private final UserQuestionService userQuestionService;

    @Autowired
    public UserQuestionController(UserQuestionService userQuestionService) {
        this.userQuestionService = userQuestionService;
    }

    @GetMapping("/question")
    public String chooseFeedBackMode(@RequestParam(required = false) String openid, Model model, HttpServletRequest request) {

        request.setAttribute("openid", openid);

        if (!StringUtils.hasText(openid)) {
            model.addAttribute("label1", "400");
            model.addAttribute("label2", "参数错误，请不要修改链接中的内容");
            return "error/4XX";
        }
        if (!userQuestionService.isUser(openid)) {
            model.addAttribute("label1", "401");
            model.addAttribute("label2", "请先长按以下二维码关注本公众号后再继续");
            return "error/4XX";
        }

        model.addAttribute("openid", openid);

        return "choose";
    }

    /**
     * 用户选择了以表单的形式反馈（某些选项即使调用着做了响应判断也仍需再次判断，避免非法请求）
     *
     * @param feedBack 具体的反馈内容
     * @return 告知用户处理结果
     */
    @PostMapping(value = "/question", produces = "text/html;charset=UTF-8")
    public String getFormFeedBack(FeedBack feedBack, Model model, HttpServletRequest request) throws Exception {

        String openid = feedBack.getOpenid().replace("openid=", "");

        request.setAttribute("openid", openid);

        feedBack.setOpenid(openid);
        feedBack.setTitle(new String(feedBack.getTitle().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
        feedBack.setMessage(new String(feedBack.getMessage().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));

        // 使用反射机制校验页面项封装到 FeedBack 对象的字段
        for (Field field : feedBack.getClass().getDeclaredFields()) {

            field.setAccessible(true);

            if ("ctime".equals(field.getName())) {
                continue;
            }

            // 校验用户 openid
            if ("openid".equalsIgnoreCase(field.getName()) && !checkUser((String) field.get(feedBack), model))
                return "error/4XX";

            // 校验用户是否已反馈过
            if ("openid".equalsIgnoreCase(field.getName()) && userQuestionService.feedBackIsExists((String) field.get(feedBack)) > 0) {
                model.addAttribute("label1", "403");
                model.addAttribute("label2", "您已反馈过，请等待处理");
                return "error/4XX";
            }

            // 校验手机号
            if ("way".equals(field.getName()) && "phone".equals(field.get(feedBack))) {
                Matcher phoneReg = Pattern.compile("^(?:(?:\\+|00)86)?1\\d{10}$").matcher(feedBack.getDetails());
                if (!phoneReg.matches()) {
                    model.addAttribute("label1", "400");
                    model.addAttribute("label2", "手机格式不正确");
                    return "error/4XX";
                }
            }

            // 校验微信号
            if ("way".equals(field.getName()) && "wechat".equals(field.get(feedBack))) {
                Matcher wechatReg = Pattern.compile("^[a-zA-Z][-_a-zA-Z0-9]{5,19}$").matcher(feedBack.getDetails());
                if (!wechatReg.matches()) {
                    model.addAttribute("label1", "400");
                    model.addAttribute("label2", "微信号格式不正确");
                    return "error/4XX";
                }
            }

            // 校验邮箱
            if ("way".equals(field.getName()) && "email".equals(field.get(feedBack))) {
                Matcher emailRule = Pattern.compile("^([a-z0-9A-Z]+[-|.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$").matcher(feedBack.getDetails());
                if (!emailRule.matches()) {
                    model.addAttribute("label1", "400");
                    model.addAttribute("label2", "邮箱格式不正确");
                    return "error/4XX";
                }
            }

            // 后段校验空表单项
            if (!StringUtils.hasText((String) field.get(feedBack))) {
                model.addAttribute("label1", "400");
                model.addAttribute("label2", String.format("表单项 %s 不能为空", fieldName(field.getName())));
                return "error/4XX";
            }

        }

        feedBack.setCtime(new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date(System.currentTimeMillis())));

        return userQuestionService.feedBackStatusMsg(feedBack, model);
    }

    private String fieldName(String field) {
        if ("title".equals(field)) {
            return "问题简介";
        } else if ("message".equals(field)) {
            return "问题详情";
        } else if ("way".equals(field)) {
            return "联系方式不能为空";
        } else {
            return "联系方式详情";
        }
    }

    private boolean checkUser(String openid, Model model) {

        if (!StringUtils.hasText(openid) && openid.length() < 28) {
            model.addAttribute("label1", "400 Bad Request");
            model.addAttribute("label2", "请求参数错误，请不要修改链接中的内容");
            return false;
        } else if (!StringUtils.hasText(openid) && openid.length() < 28 && !userQuestionService.isUser(openid)) {
            model.addAttribute("label1", "401 Unauthorized");
            model.addAttribute("label2", "请先长按以下二维码关注本公众号后再继续");
            return false;
        }

        return true;
    }
}
