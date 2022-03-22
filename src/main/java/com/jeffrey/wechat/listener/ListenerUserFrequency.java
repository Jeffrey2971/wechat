package com.jeffrey.wechat.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;


/**
 * @author jeffrey
 * @since JDK 1.8
 */

@EnableScheduling
@Slf4j
@Component
public class ListenerUserFrequency implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private HashMap<String, Integer> userFrequency;

    @Autowired
    private HashMap<String, Long> userBlackMap;

    private static final int FREQUENCY_THRESHOLD = 15;
    private static final int USER_WAIT_TIME = 300000;
    private static final int STEP = 15000;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("开始监听用户请求频率");
        listen();
    }

    @Scheduled(fixedDelay = STEP)
    private void listen() {
        for (Map.Entry<String, Integer> entry : userFrequency.entrySet()) {
            if (entry.getValue() > FREQUENCY_THRESHOLD) {
                log.info("异常访问频率：15秒 / {} 次 | 用户：{}", entry.getValue(), entry.getKey());
                userBlackMap.put(entry.getKey(), System.currentTimeMillis() + USER_WAIT_TIME);
            }
            userFrequency.remove(entry.getKey());
        }

        userBlackMap.entrySet().removeIf(next -> System.currentTimeMillis() > next.getValue());
    }
}
