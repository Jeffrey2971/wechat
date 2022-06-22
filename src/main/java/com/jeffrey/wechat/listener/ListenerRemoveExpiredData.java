package com.jeffrey.wechat.listener;

import com.google.gson.Gson;
import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import com.jeffrey.wechat.entity.TransResponseWrapper;
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
import org.springframework.scheduling.annotation.Scheduled;
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

    private final HashMap<Long, TransResponseWrapper> userInfoData;

    private static final List<Long> waitingRemoveKey = new ArrayList<>();

    /**
     * 检查文档集合的间隔事件
     */
    private static final int STEP = 60000;

    @Autowired
    public ListenerRemoveExpiredData(HashMap<Long, TransResponseWrapper> userInfoData, WeChatAutoConfiguration.WxConfig wxConfig) {
        this.userInfoData = userInfoData;
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
        event();
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

    @Scheduled(fixedDelay = STEP)
    private void event() {
        Set<Long> times = userInfoData.keySet();

        for (Long time : times) {
            if (System.currentTimeMillis() > time) {
                waitingRemoveKey.add(time);
            }
        }

        waitingRemoveKey.forEach(userInfoData::remove);

        waitingRemoveKey.clear();
    }
}
