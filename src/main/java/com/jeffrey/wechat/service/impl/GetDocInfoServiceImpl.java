package com.jeffrey.wechat.service.impl;

import com.jeffrey.wechat.entity.TransResponseWrapper;
import com.jeffrey.wechat.service.GetDocInfoService;
import com.jeffrey.wechat.utils.SaveAndReadImageDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
@Slf4j
public class GetDocInfoServiceImpl implements GetDocInfoService {

    @Override
    public String prepareData(Integer id, String wrapperKey, Model model){

        TransResponseWrapper item = SaveAndReadImageDocument.deSerialJsonToClass(wrapperKey, TransResponseWrapper.class);
        String openid = item.getOpenid();
        String expiredTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(item.getExpiredTimeStamp()));

        switch (id) {
            case 1: // 图片原文（分段）
                log.info("获取图片分段原文：{}", openid);
                model.addAttribute("document", item.getTransOriginalText());
                model.addAttribute("expiredTime", expiredTime);
                return "text";
            case 2: // 图片原文（不分段）
                log.info("获取图片不分段原文：{}", openid);
                model.addAttribute("document", item.getTransSumOriginalText());
                model.addAttribute("expiredTime", expiredTime);
                return "text";
            case 3: // 图片译文（分段）
                log.info("获取图片分段译文：{}", openid);
                model.addAttribute("document", item.getTransText());
                model.addAttribute("expiredTime", expiredTime);
                return "text";
            case 4: // 图片译文（不分段）
                log.info("获取图片不分段译文：{}", openid);
                model.addAttribute("document", item.getTransSumText());
                model.addAttribute("expiredTime", expiredTime);
                return "text";
            case 5: // 图片实景翻译（推荐）
                log.info("获取图片实景翻译：{}", openid);
                model.addAttribute("expiredTime", expiredTime);
                model.addAttribute("base64Image", item.getTransImageBase64());
                return "image";
            default:
                throw new RuntimeException("UnKnownException，it should not be happen");
        }
    }
}

