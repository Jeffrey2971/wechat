package com.jeffrey.wechat.service.impl;

import java.util.*;

import com.jeffrey.wechat.entity.message.*;
import com.thoughtworks.xstream.XStream;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import java.security.MessageDigest;
import com.jeffrey.wechat.entity.Config;
import org.springframework.stereotype.Service;
import java.security.NoSuchAlgorithmException;
import org.springframework.beans.BeansException;
import com.jeffrey.wechat.service.WeChatService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Service
@Slf4j
public class WeChatServiceImpl implements WeChatService, ApplicationContextAware {

    private static ApplicationContext context;
    private static final XStream XSTREAM = new XStream();
    private static final ProcessMessage PROCESS_MESSAGE = new ProcessMessage();

    // 批量解析标注了 @XStreamAlias 的类，这些类只需要被解析一次
    static {
        XSTREAM.processAnnotations(new Class[]{
                ImageMessage.class,
                LinkMessage.class,
                LocationMessage.class,
                MusicMessage.class,
                NewsMessage.class,
                SmallVideoMessage.class,
                TextMessage.class,
                VideoMessage.class,
                VoiceMessage.class,
        });
    }

    @Override
    public boolean check(String timestamp, String nonce, String signature, String echostr) {
        String mysig = "";
        if (context.containsBean("config.WxConfig")) {
            Config.WxConfig wxConfig = context.getBean("config.WxConfig", Config.WxConfig.class);
            String[] strs = new String[]{wxConfig.getWxToken(), timestamp, nonce};
            Arrays.sort(strs);
            String str = strs[0] + strs[1] + strs[2];
            try {
                mysig = sha1Encode(str);
                return signature.equalsIgnoreCase(mysig);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        log.info("微信接入校验失败\n原参与校验的参数：{}, {}, {}\n正确的结果应该为：{}\n但校验后得到的结果：{}", timestamp, nonce, signature, echostr, mysig);
        return false;
    }

    @Override
    public Map<String, String> parseRequestInputStream(String context) {
        Map<String, String> requestMap = new HashMap<>();
        try {
            Document document = DocumentHelper.parseText(context);
            Element rootElement = document.getRootElement();
            List<Element> elements = rootElement.elements();
            for (Element element : elements) {
                requestMap.put(element.getName(), element.getStringValue());
            }
        } catch (DocumentException e) {
            throw new RuntimeException("解析 XML 过程中发生了错误");
        }


        return requestMap;
    }

    @Override
    public String getResponse(Map<String, String> requestMap) {
        String msgType = requestMap.get("MsgType");
        BaseMessage msg = null;
        switch (msgType) {
            /*
                事件消息
             */
            case "event":
                // msg = sendEvent(requestMap);
                break;
            /*
                文本消息
             */
            case "text":
                msg = PROCESS_MESSAGE.sendTextMessage(requestMap);
                break;
            /*
                图片消息
             */
            case "image":
                msg = PROCESS_MESSAGE.sendImageMessage(requestMap);
                break;
            /*
                语音消息
             */
            case "voice":
                msg = PROCESS_MESSAGE.sendVoiceMessage(requestMap);
                break;
            /*
                短视频消息（和 video 属性一样无需重复调用）
             */
            case "shortvideo":
            /*
                视频消息
             */
            case "video":
                msg = PROCESS_MESSAGE.sendVideoMessage(requestMap);
                break;

            /*
                地理位置消息
             */
            case "location":
                // msg = new TextMessage(requestMap, "人家还不支持上门识别翻译噢，请发送图片吧！");
                msg = PROCESS_MESSAGE.sendLocationMessage(requestMap);
                break;
            /*
                链接消息
             */
            case "link":
                msg = PROCESS_MESSAGE.sendLinkMessage(requestMap);
                break;
            default:
                // msg = notReady(requestMap);
                break;
        }
        return XSTREAM.toXML(msg);
    }

    private String sha1Encode(String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("sha1");
        byte[] digest = md.digest(str.getBytes());
        char[] participating = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'
        };
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(participating[b >> 4 & 15]).append(participating[b & 15]);
        }
        return sb.toString();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
