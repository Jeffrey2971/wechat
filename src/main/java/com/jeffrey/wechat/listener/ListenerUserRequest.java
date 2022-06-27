package com.jeffrey.wechat.listener;

import com.jeffrey.wechat.utils.GetRequestAddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@WebListener
@Component
@Slf4j
public class ListenerUserRequest implements ServletRequestListener {

    private final HashMap<String, Integer> userFrequency;

    public ListenerUserRequest(HashMap<String, Integer> userFrequency) {
        this.userFrequency = userFrequency;
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {

        WebApplicationContextUtils.getRequiredWebApplicationContext(sre.getServletContext())
                .getAutowireCapableBeanFactory().autowireBean(this);

        String openid = sre.getServletRequest().getParameter("openid");
        if (StringUtils.hasText(openid)) {
            if (userFrequency.containsKey(openid)) {
                userFrequency.put(openid, userFrequency.get(openid) + 1);
            } else {
                userFrequency.put(openid, 0);
            }
        } else {
            HttpServletRequest httpServletRequest = (HttpServletRequest) sre.getServletRequest();
            String requestIp = GetRequestAddressUtil.getIPAddress(httpServletRequest);

            if (userFrequency.containsKey(requestIp)) {
                userFrequency.put(requestIp, userFrequency.get(requestIp) + 1);
            } else {
                userFrequency.put(requestIp, 0);
            }
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        ServletRequestListener.super.requestDestroyed(sre);
    }
}
