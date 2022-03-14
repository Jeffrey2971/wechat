package com.jeffrey.wechat.controller;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import com.jeffrey.wechat.service.WeChatService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/access")
public class WeChatAccessController {

    @Resource
    private WeChatService weChatService;

    @GetMapping(produces = "text/plain;charset=UTF-8")
    public String wechatSignatureAccess(HttpServletRequest request){

        log.info("校验微信签名");
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");

        if (signature != null && timestamp != null && nonce != null && echostr != null) {
            if (weChatService.check(timestamp, nonce, signature, echostr)) {
                log.info("接入成功");
                return echostr;
            }
        }

        throw new RuntimeException("接入失败");
    }

    @PostMapping(produces = "application/xml;charset=UTF-8")
    public String getMessageAndResponse(@RequestBody String context){
        log.info("收到消息");
        return processRequest(weChatService.parseRequestInputStream(context));
    }

    /**
     * Key:
     *      Content = 收到消息的内容体
     *      CreateTime = 消息的创建时间戳
     *      ToUserName = 目标 openid
     *      FromUserName = 发送信息的用户 openid
     *      MsgType = 推送的消息类型
     *      MsgId = 消息 id
     * @param requestMap 解析后的请求参数集合
     * @return 响应的 XML 字符串
     */
    private String processRequest(Map<String, String> requestMap){
        return weChatService.getResponse(requestMap);
    }
}
