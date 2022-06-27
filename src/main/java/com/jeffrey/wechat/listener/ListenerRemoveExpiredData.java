package com.jeffrey.wechat.listener;

import com.google.gson.Gson;
import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import com.jeffrey.wechat.entity.menu.*;
import com.jeffrey.wechat.utils.GetAccessTokenUtil;
import com.jeffrey.wechat.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@EnableScheduling
@Slf4j
public class ListenerRemoveExpiredData implements ApplicationListener<ApplicationStartedEvent> {

    private final WeChatAutoConfiguration.WxConfig wxConfig;

    @Autowired
    public ListenerRemoveExpiredData(WeChatAutoConfiguration.WxConfig wxConfig) {
        this.wxConfig = wxConfig;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("开始监听用户数据对象缓存");

        // UriParams
        HashMap<String, Object> uriParams = new HashMap<>();
        uriParams.put("token", GetAccessTokenUtil.getToken());

        // HttpHeaders
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setContentLanguage(Locale.CHINESE);

        removeMenu(uriParams);
        createMenu(uriParams, httpHeaders);
    }

    private void createMenu(HashMap<String, Object> uriParams, HttpHeaders httpHeaders) {

        Button button = new Button();

        button.getButton().add(new SubButton("使用方式", Arrays.asList(
                new ViewButton("图片翻译", wxConfig.getWxShowUseUrlImage()),
                new ViewButton("文档翻译", wxConfig.getWxShowUseUrlDocument())
                )));


        button.getButton().add(new PicPhotoOrAlbumButton("发送图片", "menu_image_upload"));
        button.getButton().add(new ClickButton("文档翻译", "documentTrans"));

        HttpEntity<String> httpEntity = new HttpEntity<>(new Gson().toJson(button), httpHeaders);
        log.info("创建菜单栏：{}", RequestUtil.postEntity(wxConfig.getWxCreateMenuUrl(), httpEntity, String.class, uriParams));

    }

    private void removeMenu(HashMap<String, Object> uriParams) {
        log.info("移除菜单栏：{}", RequestUtil.getEntity(wxConfig.getWxDeleteMenuUrl(), String.class, uriParams).getBody());
    }
}
