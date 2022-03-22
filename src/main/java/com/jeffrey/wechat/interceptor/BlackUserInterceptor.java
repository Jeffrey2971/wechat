package com.jeffrey.wechat.interceptor;

import com.google.gson.Gson;
import com.jeffrey.wechat.entity.message.customer.CustomerResponseStatusMessage;
import com.jeffrey.wechat.entity.message.customer.CustomerTextMessage;
import com.jeffrey.wechat.utils.GetRequestAddressUtil;
import com.jeffrey.wechat.utils.SimpleSendCustomerTextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * 拦截黑名单用户
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Slf4j
public class BlackUserInterceptor implements HandlerInterceptor {

    @Autowired
    private HashMap<String, Long> userBlackMap;

    @Autowired
    private Gson gson;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String openid = request.getParameter("openid");
        if (openid != null && "Mozilla/4.0".equalsIgnoreCase(request.getHeader("User-Agent")) && request.getParameterMap().size() == 4) {
            if (userBlackMap.containsKey(openid)) {
                // 包含 openid 请求参数的请求
                SimpleSendCustomerTextUtil.send(gson.toJson(new CustomerTextMessage(openid, new CustomerTextMessage.Text("您的请求异常频繁，请稍后再试"))), CustomerResponseStatusMessage.class);
                log.info("拦截请求：{}", openid);
                return false;
            } else {
                return true;
            }
        } else {
            // 来自浏览器请求
            if (openid != null && userBlackMap.containsKey(openid)) {
                writeMsg(response, "请求异常频繁，请过段时间再试");
                log.info("拦截请求：{}", GetRequestAddressUtil.getIPAddress(request));
                return false;
            } else {
                if (userBlackMap.containsKey(GetRequestAddressUtil.getIPAddress(request))) {
                    response.getWriter().write("请求异常频繁，请过段时间再试");
                    log.info("拦截请求：{}", GetRequestAddressUtil.getIPAddress(request));
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private void writeMsg(HttpServletResponse response, String context){
        response.setContentType("text/plain;charset=UTF-8");
        try {
            response.getWriter().write(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
