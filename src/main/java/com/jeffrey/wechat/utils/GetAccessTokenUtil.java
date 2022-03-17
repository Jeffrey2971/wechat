package com.jeffrey.wechat.utils;

import com.jeffrey.wechat.entity.AccessToken;
import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;

/**
 * 获取微信 AccessToken
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@Component
@Slf4j
public class GetAccessTokenUtil {

    private static String access_token;
    private static int expires_in;
    public static Long expiresTime;

    private static WeChatAutoConfiguration.WxConfig config;

    @Autowired
    public void setConfig(WeChatAutoConfiguration.WxConfig config) {
        GetAccessTokenUtil.config = config;
    }

    public static String getToken() {
        if (!StringUtils.hasText(access_token) || System.currentTimeMillis() > expiresTime) {
            log.info("缓存的 token 已过期或不存在，将重新请求");
            String token = requestToken();
            access_token = token;
            expiresTime = System.currentTimeMillis() + (expires_in * 1000L);
            return token;
        }
        log.info("缓存中存在有效的 token ，返回缓存的 token");
        return access_token;
    }

    /**
     * 请求获取微信 AccessToken 返回 token 字符串以及 token 有效时间
     *
     * @return accessToken
     */
    private static String requestToken() {

        if (config.getWxAppId() == null || config.getWxAppSecret() == null || config.getWxToken() == null) {
            throw new RuntimeException("获取微信 Token 所需参数为空");
        }

        final String targetUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type={grant_type}&appid={appid}&secret={secret}";

        final HashMap<String, Object> params = new HashMap<>(3);
        params.put("grant_type", "client_credential");
        params.put("appid", config.getWxAppId());
        params.put("secret", config.getWxAppSecret());

        final ResponseEntity<AccessToken> token = RequestUtil.getEntity(targetUrl, AccessToken.class, params);

        if (token.getStatusCodeValue() == 200 && token.getBody() != null && StringUtils.hasText(token.getBody().getAccess_token())) {
            expires_in = token.getBody().getExpires_in();
            return token.getBody().getAccess_token();
        }

        throw new RuntimeException("获取 AccessToken 失败\nHttpStatus：" + token.getStatusCodeValue() + "\nBody：" + token.getBody());
    }
}
