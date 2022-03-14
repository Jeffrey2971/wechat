package com.jeffrey.wechat.config;

import com.jeffrey.wechat.interceptor.FollowInterceptor;
import com.jeffrey.wechat.interceptor.FrequencyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Configuration
public class WeChatConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new FollowInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new FollowInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new FrequencyInterceptor()).addPathPatterns("/**");
    }
}
