package com.jeffrey.wechat.service.impl;

import com.jeffrey.wechat.entity.TransResponseWrapper;
import com.jeffrey.wechat.service.GetDocInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import java.util.HashMap;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Slf4j
public class GetDocInfoServiceImpl implements GetDocInfoService {


//    @Autowired
//    private WeChatServiceDao weChatServiceDao;

    @Override
    public boolean isUser(String openid) {
//        weChatServiceDao.isUser(openid);
        log.info("GetDocInfoServiceImpl 判断是否为用户暂时为 true");
        return true;
    }

    @Override
    public String prepareData(Integer id, Long wrapper, Model model, HashMap<Long, TransResponseWrapper> userDataItem) {

        TransResponseWrapper item = userDataItem.get(wrapper);
        String openid = item.getOpenid();
        int expiredTime = (int) (item.getExpiredTimeStamp() - System.currentTimeMillis()) / 1000 / 60;
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
            case 6: // 点击永久免费使用
                log.info("请求免费使用：{}", openid);
                System.out.println(item.getGetFreeLink());
                return String.format("forward:%s", item.getGetFreeLink());
            case 7: // 反馈问题或提出意见
                log.info("反馈问题或提出意见：{}", openid);
                System.out.println(item.getFeedBackLink());
                return String.format("forward:%s", item.getFeedBackLink());
            default:
                throw new RuntimeException("UnKnownException，it should not be happen");
        }
    }
}

