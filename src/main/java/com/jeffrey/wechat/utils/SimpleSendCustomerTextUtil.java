package com.jeffrey.wechat.utils;

import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
public class SimpleSendCustomerTextUtil {

    private static WeChatAutoConfiguration.WxConfig wxConfig;

    @Autowired
    private void setWxConfig(WeChatAutoConfiguration.WxConfig wxConfig) {
        SimpleSendCustomerTextUtil.wxConfig = wxConfig;
    }

    public static <T> ResponseEntity<T> send(String content, Class<T> clazz){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
        httpHeaders.setAccept(MediaType.parseMediaTypes(MediaType.APPLICATION_JSON_VALUE));
        httpHeaders.setAcceptLanguage(Locale.LanguageRange.parse("zh"));

        HttpEntity<Object> httpEntity = new HttpEntity<>(content, httpHeaders);
        Map<String, Object> httpParams = new HashMap<>();
        httpParams.put("token", GetAccessTokenUtil.getToken());
        return RequestUtil.postEntity(wxConfig.getWxCustomerUrl(), httpEntity, clazz, httpParams);
    }
}
