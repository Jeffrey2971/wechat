package com.jeffrey.wechat.controller;

import com.jeffrey.wechat.service.GetFreeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Controller
@Slf4j
public class GetFreeController {

    private final GetFreeService getFreeService;

    @Autowired
    public GetFreeController(GetFreeService getFreeService) {
        this.getFreeService = getFreeService;
    }

    @GetMapping("/free")
    public String getFree(
            @RequestParam(required = false) String openid,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        request.setAttribute("openid", openid);

        if (StringUtils.hasText(openid) && getFreeService.isUser(openid)) {

            response.setHeader("Cache-Control", "private, max-age=60");

            model.addAttribute("shareImg", getFreeService.getUserShareLink(openid));
            model.addAttribute("count", getFreeService.getUserShareTotal(openid));

            return "share";

        }

        model.addAttribute("title", "请先关注");
        model.addAttribute("label1", "您好像没有关注本公众号");
        model.addAttribute("label2", "请先长按以下二维码关注本公众号后再继续");
        return "error/4XX";

    }

    @GetMapping("/temp")
    public String getTempUseChance(@RequestParam(required = false) String openid, Model model, HttpServletRequest request){

        request.setAttribute("openid", openid);

        if (StringUtils.hasText(openid) && getFreeService.isUser(openid)) {
            if (getFreeService.getTempUseChance(openid)) {
                model.addAttribute("title", "获取成功");
                model.addAttribute("msg", "获取成功，请关闭该页面");
                return "feedback_success";
            }
        }

        model.addAttribute("title", "获取失败");
        model.addAttribute("label1", "获取失败");
        model.addAttribute("label2", "临时使用次数是为了在您推广时不受影响，您已获取过");
        return "error/4XX";
    }
}
