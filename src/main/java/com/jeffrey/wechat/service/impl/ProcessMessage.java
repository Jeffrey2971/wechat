package com.jeffrey.wechat.service.impl;

import com.google.gson.Gson;
import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import com.jeffrey.wechat.entity.TransResponseWrapper;
import com.jeffrey.wechat.entity.message.BaseMessage;
import com.jeffrey.wechat.entity.message.TextMessage;
import com.jeffrey.wechat.entity.message.customer.CustomerResponseStatusMessage;
import com.jeffrey.wechat.entity.message.customer.CustomerTextMessage;
import com.jeffrey.wechat.entity.translation.TranslationData;
import com.jeffrey.wechat.service.ProcessMessageService;
import com.jeffrey.wechat.translate.GetTranslateMetaData;
import com.jeffrey.wechat.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import java.io.File;
import java.util.*;


/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Slf4j
public class ProcessMessage implements ProcessMessageService {

    @Autowired
    private WeChatAutoConfiguration.ServerInfo serverInfo;

    @Autowired
    private Gson gson;

    @Autowired
    private HashMap<Long, TransResponseWrapper> userDataItem;

    @Autowired
    private WeChatAutoConfiguration.WxConfig wxConfig;

    @Autowired
    private WeChatAutoConfiguration.BaiduTranslationConfig baiduConfig;

    @Override
    public BaseMessage sendTextMessage(Map<String, String> requestMap) {
        return new TextMessage(requestMap, "测试");
    }

    @Override
    public BaseMessage sendImageMessage(Map<String, String> requestMap) {


        new Thread(() -> {
            File path = new File(Objects.requireNonNull(Objects.requireNonNull(ClassUtils.getDefaultClassLoader()).getResource("")).getPath(), "/static/image");

            String downloadImageMd5Name = FileDownloadUtil.download(path, requestMap.get("PicUrl"));
            String openid = requestMap.get("FromUserName");

            StringBuilder src = new StringBuilder(4096);
            StringBuilder dst = new StringBuilder(4096);

            TranslationData metaData = GetTranslateMetaData.getData(new File(path, downloadImageMd5Name + ".png"));



            if (metaData.getError_code() != 0) {
                String reqBody = gson.toJson(new CustomerTextMessage(openid, new CustomerTextMessage.Text(metaData.getError_msg())));
                ResponseEntity<CustomerResponseStatusMessage> responseEntity = SimpleSendCustomerTextUtil.send(reqBody, CustomerResponseStatusMessage.class);
                log.info("发送异步消息状态：{}", responseEntity);
                return;
            }

            for (TranslationData.DataInfo.SegmentedData segmentedData : metaData.getData().getContent()) {
                src.append("<p>").append(segmentedData.getSrc()).append("</p>");
                dst.append("<p>").append(segmentedData.getDst()).append("</p>");
            }

            TransResponseWrapper responseWrapper = new TransResponseWrapper();

            boolean isZh = EditDistance.getSimilarityRatio(metaData.getData().getSumSrc(), metaData.getData().getSumDst()) > 75.000000;

            responseWrapper.setOpenid(openid);
            responseWrapper.setExpiredTimeStamp(System.currentTimeMillis() + (30 * 60 * 1000));
            responseWrapper.setGetFreeLink(String.format("/free?openid=%s", openid));
            responseWrapper.setFeedBackLink(String.format("/question?openid=%s", openid));

            responseWrapper.setTransOriginalText(src.toString());
            responseWrapper.setTransSumOriginalText(metaData.getData().getSumSrc());// 分段原文

            if (!isZh) {
                responseWrapper.setTransImageBase64("data:image/png;base64," + metaData.getData().getPasteImg());
                responseWrapper.setTransText(dst.toString());
                responseWrapper.setTransSumText(metaData.getData().getSumDst());
            }

            while (true) {

                long expiresTime = System.currentTimeMillis() + (30 * 60 * 1000);

                if (!userDataItem.containsKey(expiresTime)) {

                    userDataItem.put(expiresTime, responseWrapper);

                    String respString = createRespUrl(openid, expiresTime, isZh);
                    String data = gson.toJson(new CustomerTextMessage(openid, new CustomerTextMessage.Text(respString)));

                    ResponseEntity<CustomerResponseStatusMessage> responseEntity = SimpleSendCustomerTextUtil.send(data, CustomerResponseStatusMessage.class);
                    log.info("发送异步消息状态：{}", responseEntity);
                    break;
                }
            }

        }).start();


        return new TextMessage(requestMap, "已收到您发送的图片，处理中，请稍后");
    }

    @Override
    public BaseMessage sendVoiceMessage(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage sendVideoMessage(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage sendMusicMessage(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage sendNewsMessage(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage sendLocationMessage(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage sendLinkMessage(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public BaseMessage sendShortVideoMessage(Map<String, String> requestMap) {
        return null;
    }

    // ****** private method ****** //

    private String createRespUrl(String openid, Long timestamp, boolean isZh) {
        StringBuilder sb = new StringBuilder(2048);
        sb.append("以下是图片中相关的信息，可点击蓝色字体查看：\n\n\n");
        String base = "<a href=\"" + serverInfo.getDomain() + "/info/%s;wrapper=" + timestamp + ";openid=" + openid + "\">点击查看</a>\n\n";
        if (isZh) {
            sb.append("1. 图片原文（分段）：").append(String.format(base, 1)); // noSplitOrigText
            sb.append("2. 图片原文（不分段）：").append(String.format(base, 2));
            sb.append("3. 点击永久免费使用：").append(String.format(base, 6));
            sb.append("4. 反馈问题或提出意见").append(String.format(base, 7));
        } else {
            sb.append("1. 图片实景翻译（推荐）：").append(String.format(base, 5));
            sb.append("2. 图片原文（分段）：").append(String.format(base, 1));
            sb.append("3. 图片译文（分段）").append(String.format(base, 3));
            sb.append("4. 图片原文（不分段）：").append(String.format(base, 2));
            sb.append("5. 图片译文（不分段）：").append(String.format(base, 4));
            sb.append("6. 点击永久免费使用：").append(String.format(base, 6));
            sb.append("7. 反馈问题或提出意见：").append(String.format(base, 7));
        }

        return sb.toString();
    }

}
