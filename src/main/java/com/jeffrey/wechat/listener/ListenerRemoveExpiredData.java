package com.jeffrey.wechat.listener;

import com.jeffrey.wechat.entity.TransResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@EnableScheduling
@Slf4j
public class ListenerRemoveExpiredData implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private HashMap<Long, TransResponseWrapper> userInfoData;

    /**
     * 检查文档集合的间隔事件
     */
    private static final int STEP = 60000;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("开始监听用户数据对象缓存");
        event();
    }

    @Scheduled(fixedDelay = STEP)
    private void event() {
        Set<Long> times = userInfoData.keySet();
        log.info("缓存的数据项剩余：{}", times.size());
        ArrayList<Long> waitingRemoveKey = new ArrayList<>();

        for (Long time : times) {
            if (System.currentTimeMillis() > time) {
                waitingRemoveKey.add(time);
            }
        }

        for (Long key : waitingRemoveKey) {
            log.info("移除对象：{}", key);
            userInfoData.remove(key);
        }

        waitingRemoveKey.clear();
    }
}
