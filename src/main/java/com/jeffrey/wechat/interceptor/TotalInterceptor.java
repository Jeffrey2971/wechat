package com.jeffrey.wechat.interceptor;

import com.jeffrey.wechat.entity.mapper.UserUseTotalEntity;
import com.jeffrey.wechat.service.GetFreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 *
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
public class TotalInterceptor implements HandlerInterceptor {

    private final GetFreeService getFreeService;

    @Autowired
    public TotalInterceptor(GetFreeService getFreeService) {
        this.getFreeService = getFreeService;
    }

    /**
     * 如果用户发送的消息类型为 image 图片类型的话，在今日可使用次数的基础上减一
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        if (request.getAttribute("requestMap") instanceof HashMap) {
            HashMap<?, ?> requestMap = (HashMap<?, ?>) request.getAttribute("requestMap");
            String oid = (String) requestMap.get("FromUserName");
            if (StringUtils.hasText(oid)) {
                if ("image".equalsIgnoreCase((String) requestMap.get("MsgType"))) {

                    UserUseTotalEntity userUseTotalEntity = getFreeService.getUserUseTotalTableEntityByOpenId(oid);

                    // 如果结果不为 0 或 不为 null，在基础上 -1
                    Integer canUse = userUseTotalEntity.getCanUse();

                    // 在基础上 +1
                    Integer allUse = userUseTotalEntity.getAllUse();

                    if ("F".equalsIgnoreCase(String.valueOf(userUseTotalEntity.getFreeUser())) && canUse != null && canUse != 0) {
                        getFreeService.updateUserTotal(new UserUseTotalEntity(null, oid, --canUse, userUseTotalEntity.getFreeUser(), ++allUse, userUseTotalEntity.getFree()));
                    } else {
                        getFreeService.updateUserTotal(new UserUseTotalEntity(null, oid, canUse, userUseTotalEntity.getFreeUser(), ++allUse, userUseTotalEntity.getFree()));
                    }
                }
            }
        }
    }
}
