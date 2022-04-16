package com.jeffrey.wechat.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jeffrey.wechat.entity.mybatis.ShareTableEntity;
import com.jeffrey.wechat.entity.mybatis.UserInfo;
import com.jeffrey.wechat.entity.mybatis.UserUseTotalEntity;
import com.jeffrey.wechat.service.GetFreeServiceShareTable;
import com.jeffrey.wechat.service.GetFreeServiceUserUseTotalTable;
import com.jeffrey.wechat.service.WeChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Slf4j
public class DayFlushSheetData implements InitializingBean {

    private final GetFreeServiceShareTable getFreeServiceShareTable;

    private final GetFreeServiceUserUseTotalTable getFreeServiceUserUseTotalTable;

    private final WeChatService weChatService;


    @Autowired
    public DayFlushSheetData(GetFreeServiceShareTable getFreeServiceShareTable, GetFreeServiceUserUseTotalTable getFreeServiceUserUseTotalTable, WeChatService weChatService) {
        this.getFreeServiceShareTable = getFreeServiceShareTable;
        this.getFreeServiceUserUseTotalTable = getFreeServiceUserUseTotalTable;
        this.weChatService = weChatService;
    }

    @Override
    public void afterPropertiesSet() {
        this.flushShareTable();
        this.flushUserUseTotalTable();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void flushShareTable() {

        log.info("刷新 share 表数据");
//        List<String> shareUserOpenIdList = getFreeService.selectShareUserOpenIdList();

        List<ShareTableEntity> shareUserOpenIdList = getFreeServiceShareTable.list(new QueryWrapper<ShareTableEntity>().select("openid"));

        shareUserOpenIdList.removeAll(weChatService.list(new QueryWrapper<UserInfo>().select("openid")));

        if (shareUserOpenIdList.size() > 0) {
            log.info("发现无效用户，正在将他们移除：");

            shareUserOpenIdList.forEach(System.out::print);

            QueryWrapper<ShareTableEntity> removeWrapper = new QueryWrapper<>();
            shareUserOpenIdList.forEach( oid -> getFreeServiceShareTable.remove(removeWrapper.eq("openid", oid)));
        }

    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void flushUserUseTotalTable() {

        log.info("重置用户每日使用量");
//        getFreeService.resetCanUseColumn(wxConfig.getWxDayCanUse());
        getFreeServiceShareTable.update(null, new UpdateWrapper<ShareTableEntity>().set("can_use", 5));

        log.info("移除表中无效用户");

//        List<String> GFSUserOpenIdList = getFreeService.selectUserUseTotalUserOpenIdList();

        List<UserUseTotalEntity> GFSUserOpenIdList = getFreeServiceUserUseTotalTable.list(new QueryWrapper<UserUseTotalEntity>().select("openid"));

        GFSUserOpenIdList.removeAll(weChatService.list(new QueryWrapper<UserInfo>().select("openid")));

        if (GFSUserOpenIdList.size() > 0) {
            log.info("发现无效用户，正在将他们移除：");

            GFSUserOpenIdList.forEach(System.out::print);

//            getFreeService.deleteListUser("user_use_total", GFSUserOpenIdList);
//            getFreeServiceUserUseTotalTable.remove(new QueryWrapper<>())
            GFSUserOpenIdList.forEach( item -> getFreeServiceUserUseTotalTable.remove(new QueryWrapper<UserUseTotalEntity>().eq("openid", item)));
        }

    }
}




