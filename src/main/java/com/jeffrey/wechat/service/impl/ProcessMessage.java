package com.jeffrey.wechat.service.impl;

import java.util.*;
import java.io.InputStream;
import java.io.IOException;
import com.google.gson.Gson;
import com.jeffrey.wechat.entity.message.NewsMessage;
import lombok.extern.slf4j.Slf4j;
import com.jeffrey.wechat.utils.*;
import com.jeffrey.wechat.entity.TransResponseWrapper;
import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import com.jeffrey.wechat.entity.message.customer.CustomerTextMessage;
import com.jeffrey.wechat.entity.message.BaseMessage;
import com.jeffrey.wechat.entity.message.EmptyMessage;
import com.jeffrey.wechat.entity.message.TextMessage;
import com.jeffrey.wechat.entity.BasicResultMessage;
import com.jeffrey.wechat.entity.mapper.UserUseTotalEntity;
import com.jeffrey.wechat.entity.translation.TranslationData;
import com.jeffrey.wechat.service.GetFreeService;
import com.jeffrey.wechat.service.ProcessMessageService;
import com.jeffrey.wechat.translate.GetTextTranslateMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
@Slf4j
public class ProcessMessage implements ProcessMessageService {

    private final WeChatAutoConfiguration.ServerInfo serverInfo;

    private final WeChatAutoConfiguration.WxConfig wxConfig;

    private final GetFreeService getFreeService;

    @Autowired
    public ProcessMessage(WeChatAutoConfiguration.ServerInfo serverInfo, WeChatAutoConfiguration.WxConfig wxConfig, GetFreeService getFreeService) {
        this.serverInfo = serverInfo;
        this.wxConfig = wxConfig;
        this.getFreeService = getFreeService;
    }

    @Override
    public BaseMessage sendTextMessage(Map<String, String> requestMap) {
        return new TextMessage(requestMap, responseMessage(requestMap));
    }

    @Override
    public BaseMessage sendImageMessage(Map<String, String> requestMap) {

        String oid = requestMap.get("FromUserName");

        if (getFreeService.notExistsUserUseTotal(oid)) {
            log.info("旧用户，初始化一条新的数据：{}", oid);
            getFreeService.initialUserTotal(new UserUseTotalEntity(null, oid, wxConfig.getWxDayCanUse(), 'F', 0, 'T'));
        }

        if (!getFreeService.continueUser(oid) && getFreeService.getUserTotal(oid) <= 0 && getFreeService.getUserShareTotal(oid) < wxConfig.getWxShareThreshold()) {

            UserUseTotalEntity entityByOpenId = getFreeService.getUserUseTotalTableEntityByOpenId(oid);

            String getFreeLink = String.format("<a href=\"%s/%s?openid=%s\">%s</a>", serverInfo.getDomain(), "free", oid, "获取每日使用不限次数权益");

            if ("T".equalsIgnoreCase(String.valueOf(entityByOpenId.getFree()))) {

                /*
                    您今日的使用次数已达上限噢，如需永久免费使用请点击：获取永久使用权限
                    但为了不影响您的使用，您可点击：获取临时使用次数
                 */
                SimpleSendCustomerTextUtil.send(new Gson().toJson(
                        new CustomerTextMessage(oid,
                                new CustomerTextMessage.Text(
                                        String.format("您今日的使用次数已达上限，如需获取每日不限使用次数权益请点击：%s \n\n但为了不影响您的使用，您可点击以下链接获取临时使用次数", getFreeLink)
                                ))), BasicResultMessage.class);

                return new NewsMessage(
                        requestMap,
                        new NewsMessage.Articles(
                                new NewsMessage.Articles.Item(
                                        "临时使用次数",
                                        "点击获取临时使用次数",
                                        String.format("%s/image/newsImg.png", serverInfo.getDomain()),
                                        String.format("%s/%s?openid=%s", serverInfo.getDomain(), "temp", oid))
                        ), 1);
            }

            return new TextMessage(requestMap, String.format("您今日的使用次数已达上限，如需每日不限次数使用请点击：%s", getFreeLink));

        }

        //-------------------------------发送消息-------------------------------//

        new Thread(() -> {

            InputStream imageInputStream;

            try {
                imageInputStream = FileDownloadInputStreamUtil.download(requestMap.get("PicUrl"));
            } catch (IOException e) {
                log.error("下载图片字节流发生了异常，后续翻译流程将终止", e);
                return;
            }

            String openid = requestMap.get("FromUserName");

            StringBuilder src = new StringBuilder(4096);
            StringBuilder dst = new StringBuilder(4096);

            TranslationData metaData;

            try {
                metaData = GetTextTranslateMetaData.getData(imageInputStream);
            } catch (IOException e) {
                log.error("翻译过程中发生了异常，本次翻译终止", e);
                return;
            } finally {
                if (imageInputStream != null) {
                    try {
                        imageInputStream.close();
                    } catch (IOException e) {
                        log.error("关闭图片下载流失败", e);
                    }
                }
            }

            log.info("本次翻译状态信息：{} | {}", metaData.getError_code(), metaData.getError_msg());
            if (metaData.getError_code() != 0) {
                String reqBody = new Gson().toJson(new CustomerTextMessage(openid, new CustomerTextMessage.Text(metaData.getError_msg())));
                ResponseEntity<BasicResultMessage> responseEntity = SimpleSendCustomerTextUtil.send(reqBody, BasicResultMessage.class);
                log.error("翻译时出现了异常，响应的状态码不为 0，后续翻流程终止 | {}", responseEntity);
                return;
            }

            for (TranslationData.DataInfo.SegmentedData segmentedData : metaData.getData().getContent()) {
                src.append("<p>").append(segmentedData.getSrc()).append("</p>");
                dst.append("<p>").append(segmentedData.getDst()).append("</p>");
            }

            TransResponseWrapper responseWrapper = new TransResponseWrapper();

            boolean isZh = EditDistance.getSimilarityRatio(metaData.getData().getSumSrc(), metaData.getData().getSumDst()) > 75.000000;

            responseWrapper.setOpenid(openid);

            responseWrapper.setExpiredTimeStamp(System.currentTimeMillis() + ((30L) * (24 * ((60 * 60) * 1000))));
            responseWrapper.setTransOriginalText(src.toString());
            responseWrapper.setTransSumOriginalText(metaData.getData().getSumSrc());

            if (!isZh) {
                responseWrapper.setTransImageBase64("data:image/png;base64," + metaData.getData().getPasteImg());
                responseWrapper.setTransText(dst.toString());
                responseWrapper.setTransSumText(metaData.getData().getSumDst());
            }

            while (true) {

                String key = String.valueOf(System.currentTimeMillis());

                if (!SaveAndReadImageDocument.containsKey(key)) {

                    if (!SaveAndReadImageDocument.serialToJsonAndSave(key, responseWrapper)) {
                        log.error("翻译成功，但存储翻译对象的时候出现异常");
                    }

                    String respString = createRespUrl(openid, key, isZh,
                            src.toString().contains("pptx")
                            || src.toString().toLowerCase(Locale.CHINA).contains("ppt")
                            || src.toString().toLowerCase(Locale.CHINA).contains("doc")
                            || src.toString().toLowerCase(Locale.CHINA).contains("docx")
                            || src.toString().toLowerCase(Locale.CHINA).contains("txt")
                            || src.toString().toLowerCase(Locale.CHINA).contains("xls")
                            || src.toString().toLowerCase(Locale.CHINA).contains("xlsx")
                            || src.toString().toLowerCase(Locale.CHINA).contains("pdf")
                    );
                    String data = new Gson().toJson(new CustomerTextMessage(openid, new CustomerTextMessage.Text(respString)));

                    ResponseEntity<BasicResultMessage> responseEntity = SimpleSendCustomerTextUtil.send(data, BasicResultMessage.class);

                    log.info("发送异步消息状态：{}", responseEntity);

                    break;
                }
            }
        }).start();

        return new TextMessage(requestMap, "已收到照片，处理中，请稍后");
    }

    @Override
    public BaseMessage sendVoiceMessage(Map<String, String> requestMap) {
        return new TextMessage(requestMap, "在和我说悄悄话嘛？");
    }

    @Override
    public BaseMessage sendVideoMessage(Map<String, String> requestMap) {
        return new TextMessage(requestMap, "人家看不懂视频啦");
    }

    @Override
    public BaseMessage sendMusicMessage(Map<String, String> requestMap) {
        return new TextMessage(requestMap, "真好听!");
    }

    @Override
    public BaseMessage sendNewsMessage(Map<String, String> requestMap) {
        return new EmptyMessage();
    }

    @Override
    public BaseMessage sendLocationMessage(Map<String, String> requestMap) {
        return new TextMessage(requestMap, "人家还不支持上门翻译噢");
    }

    @Override
    public BaseMessage sendLinkMessage(Map<String, String> requestMap) {
        return new TextMessage(requestMap, "人家还不支持网页翻译噢");
    }

    @Override
    public BaseMessage sendShortVideoMessage(Map<String, String> requestMap) {
        return new TextMessage(requestMap, "人家看不懂视频啦");
    }

    // ****** private method ****** //

    private String responseMessage(Map<String, String> requestMap) {
        String openid = requestMap.get("FromUserName");
        List<String> thankKeywords = wxConfig.getThankKeywords();
        String content = requestMap.get("Content").replace(" ", "").replace("\n", "").replace("\t", "");
        for (String item : thankKeywords) {
            if (content.equals(item)) {
                return "很高兴能帮帮助您，可以的话将本公众号推荐给更多人噢！！！";
            }
        }

        final String baseUrl = "<a href=\"URL\">TITLE</a>";
        return "额，不明白您的意思噢，请发送图片，具体使用可点击以下蓝色字体查看\n\n\n" +
                baseUrl.replace("URL", "https://mp.weixin.qq.com/s/3ypEPH04Q6CN7SS7CwGFXQ").replace("TITLE", "1、点击查看使用方式") + "\n\n" +
                baseUrl.replace("URL", serverInfo.getDomain() + "/question?openid=" + openid).replace("TITLE", "2、遇到问题或有疑问？");
    }

    private String createRespUrl(String openid, String key, boolean isZh, boolean similarDocument) {
        StringBuilder sb = new StringBuilder(2048);
        sb.append("以下是图片中相关的信息，可点击蓝色字体查看：\n\n\n");
        String base1 = "<a href=\"" + serverInfo.getDomain() + "/info/%s;wrapper=" + key + ";openid=" + openid + "\">点击查看</a>\n\n";
        String base2 = "<a href=\"" + serverInfo.getDomain() + "/question?openid=" + openid + "\">点击查看</a>\n\n";
        String base3 = "<a href=\"" + serverInfo.getDomain() + "/free?openid=" + openid + "\">点击查看</a>\n\n";
        if (isZh) {
            sb.append("1. 图片原文（分段）：").append(String.format(base1, 1));
            sb.append("2. 图片原文（不分段）：").append(String.format(base1, 2));
            sb.append("3. 点击不限次数使用：").append(String.format(base3, 6));
            sb.append("4. 反馈问题或提出意见：").append(base2);
        } else {
            sb.append("1. 图片实景翻译（推荐）：").append(String.format(base1, 5));
            sb.append("2. 图片原文（分段）：").append(String.format(base1, 1));
            sb.append("3. 图片译文（分段）").append(String.format(base1, 3));
            sb.append("4. 图片原文（不分段）：").append(String.format(base1, 2));
            sb.append("5. 图片译文（不分段）：").append(String.format(base1, 4));
            sb.append("6. 点击不限次数使用：").append(String.format(base3, 6));
            sb.append("7. 反馈问题或提出意见：").append(base2);
        }

        if (similarDocument) {
            sb.append("您翻译的内容看起来像文档，建议您使用：").append("<a href=\"" + serverInfo.getDomain() + "/document?uid=" + openid + "\">文档翻译</a>\n\n");
        }

        return sb.toString();
    }

}
