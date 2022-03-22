package com.jeffrey.wechat.controller;

import com.jeffrey.wechat.entity.TransResponseWrapper;
import com.jeffrey.wechat.service.GetDocInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Controller
@Slf4j
public class GetDocInfoController {

    @Autowired
    private GetDocInfoService docInfoService;

    @Autowired
    private HashMap<Long, TransResponseWrapper> userDataItem;

    @GetMapping(value = "/info/{id}", produces = "text/html;charset=UTF-8")
    public String info(@PathVariable Integer id, @MatrixVariable Long wrapper, @MatrixVariable String openid, Model model){
        if (userDataItem.containsKey(wrapper) && id > 0 && id <= 7) {
            TransResponseWrapper item = userDataItem.get(wrapper);
            if (docInfoService.isUser(openid) && openid.equals(item.getOpenid())) {
                return docInfoService.prepareData(id, wrapper, model, userDataItem);
            } else {
                model.addAttribute("title", "请先关注");
                model.addAttribute("label1", "您好像没有关注本公众号");
                model.addAttribute("label2", "请先长按以下二维码关注本公众号后再继续");
                return "4xx";
            }
        } else {
            model.addAttribute("title", "文档已过期");
            model.addAttribute("label1", "该文档不存在或已过期");
            model.addAttribute("label2", "可长按识别以下二维码到公众号重新获取");
            return "4xx";
        }
    }
}
