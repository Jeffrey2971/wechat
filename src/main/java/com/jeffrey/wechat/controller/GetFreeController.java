package com.jeffrey.wechat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Controller
@Slf4j
public class GetFreeController {

    @GetMapping("/free")
    public String getFree(Model model) {
        log.info("获取免费使用");
        model.addAttribute("count", "333");
        return "share";
    }
}
