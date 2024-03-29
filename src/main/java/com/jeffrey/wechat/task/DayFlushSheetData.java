package com.jeffrey.wechat.task;

import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import com.jeffrey.wechat.entity.TransResponseWrapper;
import com.jeffrey.wechat.service.GetFreeService;
import com.jeffrey.wechat.service.WeChatService;
import com.jeffrey.wechat.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@EnableScheduling
@Slf4j
public class DayFlushSheetData {

    private final GetFreeService getFreeService;

    private final WeChatService weChatService;

    private final WeChatAutoConfiguration.WxConfig wxConfig;

    @Autowired
    public DayFlushSheetData(GetFreeService getFreeService, WeChatService weChatService, WeChatAutoConfiguration.WxConfig wxConfig) {
        this.getFreeService = getFreeService;
        this.weChatService = weChatService;
        this.wxConfig = wxConfig;
    }

    //--------------------移除 share 表中无效数据--------------------//
    @Scheduled(cron = "0 0 0 * * ?")
    public void flushShareTable() {

        log.info("刷新 share 表数据");
        List<String> shareUserOpenIdList = getFreeService.selectShareUserOpenIdList();
        shareUserOpenIdList.removeAll(weChatService.selectUserOpenIdList());

        if (shareUserOpenIdList.size() > 0) {
            log.info("发现无效用户，正在将他们移除：");

            shareUserOpenIdList.forEach(System.out::print);

            log.info("成功移除了 {} 位用户", getFreeService.deleteListUser(shareUserOpenIdList));
        }

    }

    //--------------------刷新 user_use_total 表内用户的每日免费试用量--------------------//
    @Scheduled(cron = "0 0 0 * * ?")
    public void flushUserUseTotalTable() {

        log.info("重置用户每日使用量");

        getFreeService.resetCanUseColumn(wxConfig.getWxDayCanUse());

        log.info("移除表中无效用户");

        List<String> GFSUserOpenIdList = getFreeService.selectUserUseTotalUserOpenIdList();

        GFSUserOpenIdList.removeAll(weChatService.selectUserOpenIdList());

        if (GFSUserOpenIdList.size() > 0) {
            log.info("发现无效用户，正在将他们移除：");

            GFSUserOpenIdList.forEach(System.out::print);

            log.info("成功移除了 {} 位用户", getFreeService.deleteListUser(GFSUserOpenIdList));
        }
    }

    //----------------------------移除 redis 中无效数据---------------------------//
    @Scheduled(cron = "0 0 3 * * ?")
    public void flushRedisNotFollowUserData() {

        log.info("begin remove redis bad data");

        Map<String, String> entities = RedisUtil.getEntities();

        if (entities == null || entities.size() == 0) return;

        List<String> openIdList = weChatService.selectUserOpenIdList();

        ArrayList<String> removeKeys = new ArrayList<>();

        entities.forEach((redisKey, openId) -> {
            if (!openIdList.contains(openId)) {
                removeKeys.add(redisKey);
                log.info("remove -> [ redisKey = {} | openId = {} ]", redisKey, openId);
            }
        });

        RedisUtil.removeKeyList(removeKeys);
    }
}


