package com.jeffrey.wechat.service.impl;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jeffrey.wechat.aop.UserShareAOP;
import com.jeffrey.wechat.mapper.WeChatServiceDao;
import com.jeffrey.wechat.entity.message.BaseMessage;
import com.jeffrey.wechat.entity.message.EmptyMessage;
import com.jeffrey.wechat.entity.mapper.UserInfo;
import com.jeffrey.wechat.service.ProcessEventMessage;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import lombok.extern.slf4j.Slf4j;
import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jeffrey.wechat.service.WeChatService;

@Service
@Slf4j
public class WeChatServiceImpl implements WeChatService {

    private final XStream xStream;

    private final ProcessMessage processMessage;

    private final ProcessEventMessage processEventMessage;

    private final WeChatAutoConfiguration.WxConfig config;

    private final WeChatServiceDao weChatServiceDao;

    @Autowired
    public WeChatServiceImpl(XStream xStream, ProcessMessage processMessage, ProcessEventMessage processEventMessage, WeChatAutoConfiguration.WxConfig config, WeChatServiceDao weChatServiceDao) {
        this.xStream = xStream;
        this.processMessage = processMessage;
        this.processEventMessage = processEventMessage;
        this.config = config;
        this.weChatServiceDao = weChatServiceDao;
    }

    @Override
    public boolean check(String timestamp, String nonce, String signature, String echostr) {

        if (config != null) {
            String[] strs = new String[]{config.getWxToken(), timestamp, nonce};
            Arrays.sort(strs);
            return signature.equalsIgnoreCase(DigestUtils.sha1Hex(strs[0] + strs[1] + strs[2]));
        }
        log.info("微信接入校验失败");
        return false;
    }

    @Override
    public Map<String, String> parseRequestInputStream(String context) throws Exception{
        Map<String, String> requestMap = new HashMap<>();

        Document document = DocumentHelper.parseText(context);
        Element rootElement = document.getRootElement();
        List<Element> elements = rootElement.elements();
        for (Element element : elements) {
            requestMap.put(element.getName(), element.getStringValue());
        }

        return requestMap;
    }

    @Override
    @UserShareAOP
    public String getResponse(Map<String, String> requestMap) throws Exception{
        String msgType = requestMap.get("MsgType");
        BaseMessage msg = null;
        switch (msgType) {
            case "event":
                switch (requestMap.get("Event").toLowerCase()) {
                    case "subscribe":
                        msg = processEventMessage.processSubscribe(requestMap);
                        break;
                    case "unsubscribe":
                        msg = processEventMessage.processUnsubscribe(requestMap);
                        break;
                    case "click":
                        msg = processEventMessage.processClick(requestMap);
                        break;
                    case "view":
                        msg = processEventMessage.processView(requestMap);
                        break;
                    case "pic_photo_or_album":
                        msg = processEventMessage.processPicPhotoOrAlbum(requestMap);
                        break;
                    case "scancode_push":
                        msg = processEventMessage.processScancodePush(requestMap);
                        break;
                    case "scancode_waitmsg":
                        msg = processEventMessage.processScancodeWaitMsg(requestMap);
                        break;
                    case "pic_sysphoto":
                        msg = processEventMessage.processPicSysPhoto(requestMap);
                        break;
                    case "pic_weixin":
                        msg = processEventMessage.processPicWeiXin(requestMap);
                        break;
                    case "location_select":
                        msg = processEventMessage.processLocationSelect(requestMap);
                        break;
                    case "view_miniprogram":
                        msg = processEventMessage.processViewMiniProgram(requestMap);
                        break;
                    default:
                        msg = new EmptyMessage();
                }
                break;
            case "text":
                msg = processMessage.sendTextMessage(requestMap);
                break;
            case "image":
                msg = processMessage.sendImageMessage(requestMap);
                break;
            case "voice":
                msg = processMessage.sendVoiceMessage(requestMap);
                break;
            case "shortvideo":
            case "video":
                msg = processMessage.sendVideoMessage(requestMap);
                break;
            case "location":
                msg = processMessage.sendLocationMessage(requestMap);
                break;
            case "link":
                msg = processMessage.sendLinkMessage(requestMap);
                break;
            default:
                break;
        }

        if (msg instanceof EmptyMessage || msg == null) {
            log.info("收到的消息类型为空，返回空字符串避免微信重复请求");
            return "";
        }

        return xStream.toXML(msg);
    }

    @Override
    public boolean isUser(String openid) {
        return weChatServiceDao.selectCount(new QueryWrapper<UserInfo>().eq("openid", openid)) > 0;
    }

    @Override
    public List<String> selectUserOpenIdList() {
        ArrayList<String> openIdList = new ArrayList<>();
        weChatServiceDao.selectList(new QueryWrapper<UserInfo>().select("openid")).forEach(item -> openIdList.add(item.getOpenid()));
        return openIdList;
    }
}
