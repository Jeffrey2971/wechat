package com.jeffrey.wechat.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jeffrey.wechat.entity.mybatis.UserInfo;
import com.jeffrey.wechat.service.WeChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


@Component
public class BaseCode {

    private static WeChatService weChatService;

    @Autowired
    private void setWeChatService(WeChatService weChatService) {
        BaseCode.weChatService = weChatService;
    }

    /**
     * 判断不是一个用户
     * @param openid 需要判断的 openid （在 basic_user_info 中查找）
     * @param model Model
     * @return 是否不是一个用户
     */
    public static boolean notUser(String openid, @Nullable Model model) {

        if (!StringUtils.hasText(openid) && openid.length() < 28) {
            if (model != null) {
                model.addAttribute("label1", "400 Bad Request");
                model.addAttribute("label2", "请求参数错误，请不要修改链接中的内容");
                return true;
            }
            return true;
        } else if (!StringUtils.hasText(openid) && openid.length() < 28 && weChatService.count(new QueryWrapper<UserInfo>().eq("openid", openid)) <= 0) {
            if (model != null) {
                model.addAttribute("label1", "401 Unauthorized");
                model.addAttribute("label2", "请先长按以下二维码关注本公众号后再继续");
                return true;
            }
            return true;
        }

        return false;
    }
}
