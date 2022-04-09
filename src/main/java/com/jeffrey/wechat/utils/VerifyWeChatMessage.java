package com.jeffrey.wechat.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * 校验消息是否来自于微信服务器，一般有两种情况
 *      1. 接入请求
 *      2. 消息事件
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Slf4j
public class VerifyWeChatMessage {

    public static boolean check(String token, String timestamp, String nonce, String signature) {
        log.info("校验消息是否来自于微信服务器");
        if (!StringUtils.hasText(token) || !StringUtils.hasText(timestamp) || !StringUtils.hasText(nonce) || !StringUtils.hasText(signature)){
            return false;
        }
        String[] strs = new String[]{token, timestamp, nonce};
        Arrays.sort(strs);
        return new DigestUtils("sha1").digestAsHex(strs[0] + strs[1] + strs[2]).equals(signature);
    }
}
