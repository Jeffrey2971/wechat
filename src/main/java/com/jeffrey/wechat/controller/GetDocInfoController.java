package com.jeffrey.wechat.controller;

import com.jeffrey.wechat.entity.TransResponseWrapper;
import com.jeffrey.wechat.service.GetDocInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Controller
@Slf4j
public class GetDocInfoController {

    private final GetDocInfoService docInfoService;

    private final HashMap<Long, TransResponseWrapper> userDataItem;

    @Autowired
    public GetDocInfoController(GetDocInfoService docInfoService, HashMap<Long, TransResponseWrapper> userDataItem) {
        this.docInfoService = docInfoService;
        this.userDataItem = userDataItem;
    }

    @GetMapping(value = "/info/{id}", produces = "text/html;charset=UTF-8")
    public String info(@PathVariable(required = false) Integer id, @MatrixVariable(required = false) Long wrapper, @MatrixVariable(required = false) String openid, Model model, HttpServletRequest request){

        request.setAttribute("openid", openid);

        if (userDataItem.containsKey(wrapper) && id > 0 && id <= 5) {
            TransResponseWrapper item = userDataItem.get(wrapper);
            if (openid.equals(item.getOpenid())) {
                return docInfoService.prepareData(id, wrapper, model, userDataItem);
            } else {
                model.addAttribute("title", "请先关注");
                model.addAttribute("label1", "401 Unauthorized");
                model.addAttribute("label2", "请先长按以下二维码关注本公众号后再继续");
                return "error/4XX";
            }
        } else {
            model.addAttribute("title", "文档已过期");
            model.addAttribute("label1", "404 Not Fount");
            model.addAttribute("label2", "可长按识别以下二维码到公众号重新获取");
            return "error/4XX";
        }
    }
}
