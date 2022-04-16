package com.jeffrey.wechat.aop;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jeffrey.wechat.entity.mybatis.ShareTableEntity;
import com.jeffrey.wechat.entity.mybatis.UserUseTotalEntity;
import com.jeffrey.wechat.service.GetFreeServiceShareTable;
import com.jeffrey.wechat.service.GetFreeServiceUserUseTotalTable;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Aspect
@Slf4j
@Component
public class UserShareAspect {

    private final GetFreeServiceUserUseTotalTable getFreeServiceUserUseTotalTable;
    private final GetFreeServiceShareTable getFreeServiceShareTable;

    @Autowired
    public UserShareAspect(GetFreeServiceUserUseTotalTable getFreeServiceUserUseTotalTable, GetFreeServiceShareTable getFreeServiceShareTable) {
        this.getFreeServiceUserUseTotalTable = getFreeServiceUserUseTotalTable;
        this.getFreeServiceShareTable = getFreeServiceShareTable;
    }

    @Pointcut("@annotation(com.jeffrey.wechat.aop.UserShareAOP)")
    private void pointCut() {
    }

    @After(value = "pointCut()")
    public void shared(JoinPoint jp) {
        log.info("AOP ---> 检查事件并更新数据");
        Object arg = jp.getArgs()[0];
        if (arg instanceof Map) {
            Map<?, ?> requestMap = (Map<?, ?>) arg;
            log.info("发生事件：{} | 事件场景：{}", requestMap.get("Event"), requestMap.get("EventKey"));
            String eventKey = (String) requestMap.get("EventKey");
            String event = (String) requestMap.get("Event");
            if (
                    "event".equalsIgnoreCase((String) requestMap.get("MsgType")) &&
                            "subscribe".equalsIgnoreCase(event) &&
                            StringUtils.hasText(eventKey) &&
                            eventKey.startsWith("qrscene_")
            ) {

                // 分享者 openid
                String sharer = eventKey.substring(eventKey.indexOf('_') + 1);

                log.info("用户 {} 通过分享者 {} 分享的二维码关注了", requestMap.get("FromUserName"), sharer);

                UserUseTotalEntity userUseTotalEntity = getFreeServiceUserUseTotalTable.getOne(new QueryWrapper<UserUseTotalEntity>().eq("openid", sharer));

//                UserUseTotalEntity userUseTotalEntity = getFreeService.getUserUseTotalTableEntityByOpenId(sharer);

//                ShareTableEntity shareTableEntity = getFreeService.getShareTableEntityByOpenId(sharer);

                ShareTableEntity shareTableEntity = getFreeServiceShareTable.getOne(new QueryWrapper<ShareTableEntity>().eq("openid", sharer));


                Integer shareTotal = shareTableEntity.getShareTotal();


                getFreeServiceShareTable.update(new UpdateWrapper<ShareTableEntity>().eq("openid", sharer).set("shareTotal", ++shareTotal));

//                getFreeService.updateShareTotal(++shareTotal, sharer);

//                getFreeService.updateUserTotal(userUseTotalEntity.getCanUse(), shareTotal >= 3 ? 'T' : 'F', userUseTotalEntity.getAllUse(), userUseTotalEntity.getFree(), sharer);
                getFreeServiceUserUseTotalTable.update(new UpdateWrapper<UserUseTotalEntity>().eq("openid", sharer).setEntity(
                        new UserUseTotalEntity(
                                null,
                                sharer,
                                userUseTotalEntity.getCanUse(),
                                shareTotal >= 3 ? 'T' : 'F',
                                userUseTotalEntity.getAllUse(),
                                userUseTotalEntity.getFree())
                        )
                );

            }
        }
    }
}
