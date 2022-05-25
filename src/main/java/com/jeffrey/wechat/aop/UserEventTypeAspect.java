package com.jeffrey.wechat.aop;

import com.jeffrey.wechat.entity.mapper.ShareTableEntity;
import com.jeffrey.wechat.entity.mapper.UserUseTotalEntity;
import com.jeffrey.wechat.service.GetFreeService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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
public class UserEventTypeAspect {

    private final GetFreeService getFreeService;

    public UserEventTypeAspect(GetFreeService getFreeService) {
        this.getFreeService = getFreeService;
    }

    @Pointcut("@annotation(com.jeffrey.wechat.aop.UserEventTypeAOP)")
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
            /*
                判断是否为一个 event 类型的消息
             */

            if ("event".equalsIgnoreCase((String) requestMap.get("MsgType"))) {
                switch (event) {
                    case "subscribe":
                        shared(requestMap, eventKey);
                        break;
                    case "unsubscribe":
                        break;
                    case "SCAN":
                        break;
                    case "LOCATION":
                        break;
                    case "CLICK":
                        break;
                    case "VIEW":
                        break;
                    default:
                        break;
                }
            }


        }
    }

    private void shared(Map<?, ?> requestMap, String eventKey) {
        if (

                StringUtils.hasText(eventKey) && eventKey.startsWith("qrscene_")
        ) {

            // 分享者 openid
            String sharer = eventKey.substring(eventKey.indexOf('_') + 1);

            log.info("用户 {} 通过分享者 {} 分享的二维码关注了", requestMap.get("FromUserName"), sharer);

            UserUseTotalEntity userUseTotalEntity = getFreeService.getUserUseTotalTableEntityByOpenId(sharer);

            ShareTableEntity shareTableEntity = getFreeService.getShareTableEntityByOpenId(sharer);

            Integer shareTotal = shareTableEntity.getShareTotal();

            getFreeService.updateShareTotal(++shareTotal, sharer);

            getFreeService.updateUserTotal(new UserUseTotalEntity(null, sharer, userUseTotalEntity.getCanUse(), shareTotal >= 3 ? 'T' : 'F', userUseTotalEntity.getAllUse(), userUseTotalEntity.getFree()));

        }
    }
}
