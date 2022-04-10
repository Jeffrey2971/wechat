package com.jeffrey.wechat.exception;

import com.jeffrey.wechat.entity.message.EmptyMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;


/**
 * @author jeffrey
 * @since JDK 1.8
 */

@ControllerAdvice
@Slf4j
public class WeChatExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String getQrCodeException(Exception exception, Model model, HttpServletRequest request) {

        log.error("尝试处理异常", exception);

        model.addAttribute("title", "HTTP 500 - 内部服务器错误-发生错误");
        model.addAttribute("label1", "HTTP 500 - 内部服务器错误-你所浏览的页面暂时无法访问");
        model.addAttribute("label2", "你可以返回上一页重试，或直接向我们反馈错误报告");
        model.addAttribute("label3", "返回主页");
        model.addAttribute("label4", "反馈错误");

        model.addAttribute("openid", request.getAttribute("openid"));

        return "error/5XX";
    }
}