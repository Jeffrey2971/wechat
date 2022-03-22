package com.jeffrey.wechat.config;

import com.jeffrey.wechat.interceptor.BlackUserInterceptor;
import com.jeffrey.wechat.interceptor.FollowInterceptor;
import com.jeffrey.wechat.interceptor.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Configuration
public class WeChatConfiguration implements WebMvcConfigurer {

    /**
     * 使矩阵变量生效
     *
     * @param configurer PathMatchConfigurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setRemoveSemicolonContent(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }

    @Bean
    public HandlerInterceptor blackUserInterceptor(){
        return new BlackUserInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(blackUserInterceptor()).addPathPatterns("/**");
    }
}
