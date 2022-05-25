package com.jeffrey.wechat.config;

import com.jeffrey.wechat.interceptor.TotalInterceptor;
import com.jeffrey.wechat.interceptor.UserRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.util.UrlPathHelper;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Configuration
public class WeChatWebConfiguration implements WebMvcConfigurer {

    private final UserRequestInterceptor userRequestInterceptor;

    private final TotalInterceptor totalInterceptor;

    @Autowired
    public WeChatWebConfiguration(UserRequestInterceptor userRequestInterceptor, TotalInterceptor totalInterceptor) {
        this.userRequestInterceptor = userRequestInterceptor;
        this.totalInterceptor = totalInterceptor;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/feedback").setViewName("feedback.html");
        registry.addViewController("/document").setViewName("document_upload.html");
    }

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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userRequestInterceptor).addPathPatterns("/**").excludePathPatterns("/static/**");
        registry.addInterceptor(totalInterceptor).addPathPatterns("/**").excludePathPatterns("/static/**");
    }
}
