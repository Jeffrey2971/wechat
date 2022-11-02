package com.jeffrey.wechat.controller;

import com.jeffrey.wechat.entity.TransResponseWrapper;
import com.jeffrey.wechat.service.GetDocInfoService;
import com.jeffrey.wechat.utils.RedisUtil;
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
    ) {
        //--------------- 添加缓存 ---------------//

        request.setAttribute("openid", openid);
        response.setHeader("Cache-Control", "public, max-age=2626560");
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss 'GMT'", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String expires = df.format(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000));
        response.setHeader("expires", expires);

        //--------------- 判断参数是否正确 ---------------//
        if (id <= 0 || id > 5) {
            model.addAttribute("title", "参数错误");
            model.addAttribute("label1", "400 Bad Request");
            model.addAttribute("label2", "请求参数错误，请不要修改链接中的内容");
            return "error/4XX";
        }

        //--------------- 判断文档是否过期 ---------------//
        if (!RedisUtil.containsKey(wrapper)) {
            model.addAttribute("title", "文档已过期");
            model.addAttribute("label1", "404 Not Fount");
            model.addAttribute("label2", "可长按识别以下二维码到公众号重新获取");
            return "error/4XX";
        }

        //--------------- 判断用户请求的 openid 是否存在于响应 wrapper 中（即查看文档是否属于访问的用户） ---------------//
        if (!openid.equals(RedisUtil.deSerialJsonToClass(wrapper, TransResponseWrapper.class).getOpenid())) {
            model.addAttribute("title", "请先关注");
            model.addAttribute("label1", "401 Unauthorized");
            model.addAttribute("label2", "请先长按以下二维码关注本公众号后再继续");
            return "error/4XX";
        }

        //--------------- 准备响应数据 ---------------//
        return docInfoService.prepareData(id, wrapper, model);
    }
}
