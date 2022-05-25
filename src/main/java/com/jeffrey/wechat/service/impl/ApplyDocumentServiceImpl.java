package com.jeffrey.wechat.service.impl;

import com.google.gson.Gson;
import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import com.jeffrey.wechat.entity.template.TemplateMessage;
import com.jeffrey.wechat.entity.translation.ApplyDocument;
import com.jeffrey.wechat.service.ApplyDocumentService;
import com.jeffrey.wechat.translate.DocumentTrans;
import com.jeffrey.wechat.utils.GetAccessTokenUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
@Slf4j
public class ApplyDocumentServiceImpl implements ApplyDocumentService {

    private static final Tika TIKA = new Tika();
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();


    private static WeChatAutoConfiguration.WxConfig config;
    private static HashMap<String, String> types;
    private static HashSet<String> lang;

    @Autowired
    public void setConfig(WeChatAutoConfiguration.WxConfig config, HashMap<String, String> types, HashSet<String> lang) {
        ApplyDocumentServiceImpl.config = config;
        ApplyDocumentServiceImpl.types = types;
        ApplyDocumentServiceImpl.lang = lang;
    }

    @Override
    public boolean checkParams(MultipartFile file, String from, String to, String openid) {

        return !file.isEmpty()
                && StringUtils.hasText(from)
                && StringUtils.hasText(to)
                && !from.equals(to)
                && lang.contains(from)
                && lang.contains(to);
    }

    @Override
    public boolean checkFileType(InputStream is, String filename) throws IOException {
        String type = TIKA.detect(is);
        log.info("上传的文件实际类型：{}", type);
        return types.containsKey(filename.substring(filename.lastIndexOf(".") + 1))
                && types.containsValue(type);
    }

    @Override
    public void checkDocumentCount(InputStream is, String filename, String from, String to, String openid) throws IOException {
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                ApplyDocument apply = DocumentTrans.apply(is, filename, from, to);
                String fileType = filename.substring(filename.lastIndexOf(".") + 1);
                // 调用模板发送
                sendTemplateMessage(apply, openid, fileType);
            }
        }).start();
    }

    @Override
    public void sendTemplateMessage(ApplyDocument applyDocument, String toUser, String fileType) {

        int errorCode = applyDocument.getError_code();

        if (errorCode != 52000) {
            log.info("无法发送模板消息，因为文档统计异常，状态码：{} ｜ 详情：{}", errorCode, applyDocument.getError_msg());
            return;
        }

        int charCount = applyDocument.getData().getCharCount();
        int amount = applyDocument.getData().getAmount();

        String requestBody = new Gson().toJson(new TemplateMessage(
                toUser,
                config.getWxGetSendTemplateId(),
                "",
                new TemplateMessage.Data(
                        new TemplateMessage.First("您上传的文档已统计完成", "#173177"),
                        new TemplateMessage.KeyWord1(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(LocalDateTime.now()), "#173177"),
                        new TemplateMessage.KeyWord2(fileType, "#173177"),
                        new TemplateMessage.KeyWord3(String.valueOf(charCount), "#173177"),
                        new TemplateMessage.KeyWord4(String.valueOf(amount), "#173177"),
                        new TemplateMessage.Remark("点击翻译文档，感谢您的使用！", "#173177")))
        );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setContentLength(requestBody.length());
        httpHeaders.setContentLanguage(Locale.CHINA);

        HttpEntity<Object> httpEntity = new HttpEntity<>(requestBody, httpHeaders);

        System.out.println(REST_TEMPLATE.postForObject(config.getWxSendTemplateMessageUrl(), httpEntity, String.class, GetAccessTokenUtil.getToken()));
        // System.out.println(REST_TEMPLATE.postForObject(config.getWxSendTemplateMessageUrl(), requestBody, String.class, GetAccessTokenUtil.getToken()));

    }
}
