package com.jeffrey.wechat.controller;

import com.jeffrey.wechat.entity.TransResponseWrapper;
import com.jeffrey.wechat.service.GetDocInfoService;
import com.jeffrey.wechat.utils.SaveAndReadImageDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Controller
@Slf4j
public class GetDocInfoController {

    private final GetDocInfoService docInfoService;

    @Autowired
    public GetDocInfoController(GetDocInfoService docInfoService) {
        this.docInfoService = docInfoService;
    }

    @GetMapping(value = "/info/{id}", produces = "text/html;charset=UTF-8")
    public String info(
            @PathVariable(required = false) Integer id,
            @MatrixVariable(required = false) String wrapper,
            @MatrixVariable(required = false) String openid,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response
    ){
        //--------------- 添加缓存 ---------------//

        request.setAttribute("openid", openid);
        response.setHeader("Cache-Control", "public, max-age=2626560");
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss 'GMT'", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String expires = df.format(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000));
        response.setHeader("expires", expires);

        if (SaveAndReadImageDocument.containsKey(wrapper) && id > 0 && id <= 5) {
            TransResponseWrapper item = SaveAndReadImageDocument.deSerialJsonToClass(wrapper, TransResponseWrapper.class);
            if (openid.equals(item.getOpenid())) {
                return docInfoService.prepareData(id, wrapper, model);
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
