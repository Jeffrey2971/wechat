package com.jeffrey.wechat.interceptor;

import com.google.gson.Gson;
import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import com.jeffrey.wechat.entity.BasicResultMessage;
import com.jeffrey.wechat.entity.message.customer.CustomerTextMessage;
import com.jeffrey.wechat.utils.GetRequestAddressUtil;
import com.jeffrey.wechat.utils.SimpleSendCustomerTextUtil;
import com.jeffrey.wechat.utils.VerifyWeChatMessage;
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
 * 拦截访问频率异常的用户
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Slf4j
public class UserRequestInterceptor implements HandlerInterceptor {

    private final WeChatAutoConfiguration.WxConfig config;

    private final HashMap<String, Long> userBlackMap;


    @Autowired
    public UserRequestInterceptor(WeChatAutoConfiguration.WxConfig config, HashMap<String, Long> userBlackMap) {
        this.config = config;
        this.userBlackMap = userBlackMap;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){


        boolean cameFromWeChat = VerifyWeChatMessage.check(config.getWxToken(), request.getParameter("timestamp"), request.getParameter("nonce"), request.getParameter("signature"));
        String oid = request.getParameter("openid");

        // 来自于微信的请求
        if (cameFromWeChat) {
            if (userBlackMap.containsKey(oid)) {
                // 包含 openid 请求参数的请求
                SimpleSendCustomerTextUtil.send(new Gson().toJson(new CustomerTextMessage(oid, new CustomerTextMessage.Text("您的请求异常频繁，请稍后再试"))), BasicResultMessage.class);
                log.info("拦截微信端请求：{}", oid);
                return false;
            }
        } else {

            // 来自浏览器请求，携带了正确的 openid
            if (userBlackMap.containsKey(oid)) {
                writeMsg(response);
                log.info("拦截携带 openid 浏览器端请求：{}", GetRequestAddressUtil.getIPAddress(request));
                return false;
            } else {
                // 来自浏览器请求，未携带正确的 openid
                if (userBlackMap.containsKey(GetRequestAddressUtil.getIPAddress(request))) {
                    writeMsg(response);
                    log.info("拦截为携带 openid 浏览器端请求：{}", GetRequestAddressUtil.getIPAddress(request));
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private void writeMsg(HttpServletResponse response) {
        response.setContentType("text/plain;charset=UTF-8");
        try {
            response.getWriter().write("请求异常频繁，请过段时间再试");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
