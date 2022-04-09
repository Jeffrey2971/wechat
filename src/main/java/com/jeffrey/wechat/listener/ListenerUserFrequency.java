package com.jeffrey.wechat.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author jeffrey
 * @since JDK 1.8
 */

@EnableScheduling
@Slf4j
@Component
public class ListenerUserFrequency implements ApplicationListener<ApplicationStartedEvent> {

    private final HashMap<String, Integer> userFrequency;
    private final HashMap<String, Long> userBlackMap;

    private static final List<String> removeList = new ArrayList<>();
    private static final int FREQUENCY_THRESHOLD = 45;
    private static final int USER_WAIT_TIME = 300000;
    private static final int STEP = 15000;

    @Autowired
    public ListenerUserFrequency(HashMap<String, Integer> userFrequency, HashMap<String, Long> userBlackMap) {
        this.userFrequency = userFrequency;
        this.userBlackMap = userBlackMap;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("开始监听用户请求频率");
        listen();
    }

    @Scheduled(fixedDelay = STEP)
    private void listen() {
        for (Map.Entry<String, Integer> entry : userFrequency.entrySet()) {
            log.info("用户：{} | 频率：{}", entry.getKey(), entry.getValue());
            if (entry.getValue() > FREQUENCY_THRESHOLD) {
                log.info("异常访问频率：15秒 / {} 次 | 用户：{}", entry.getValue(), entry.getKey());
                userBlackMap.put(entry.getKey(), System.currentTimeMillis() + USER_WAIT_TIME);
            }
            removeList.add(entry.getKey());
        }

        removeList.forEach(userFrequency::remove);
        removeList.clear();

        userBlackMap.entrySet().removeIf(next -> System.currentTimeMillis() > next.getValue());
    }
}
