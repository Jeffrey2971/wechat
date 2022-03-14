package com.jeffrey.wechat.controller;

import com.jeffrey.wechat.entity.FeedBack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 该 Controller 用于处理用户选择反馈模式以及反馈表单
 * @author jeffrey
 * @since JDK 1.8
 */

@RestController
@RequestMapping("/question")
@Slf4j
public class UserQuestionController {

    /**
     * 用户选择反馈的方式
     *      选择微信反馈则跳转到图片
     *      选择表单反馈则跳转到表单页
     * @param openid 用户唯一 openid
     * @return 图片或表单
     */

    @GetMapping
    public String chooseFeedBackMode(@RequestParam(required = false) String openid){
        return "";
    }

    /**
     * 用户选择了以表单的形式反馈
     * @param feedBack 具体的反馈内容
     * @return 告知用户处理结果
     */
    @PostMapping
    public String getFormFeedBack(FeedBack feedBack){
        return "";
    }

}
