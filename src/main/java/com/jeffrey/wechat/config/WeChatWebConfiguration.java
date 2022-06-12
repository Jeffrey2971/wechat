package com.jeffrey.wechat.config;

import com.jeffrey.wechat.WechatApplication;
import com.jeffrey.wechat.interceptor.GetDocumentInterceptor;
import com.jeffrey.wechat.interceptor.TotalInterceptor;
import com.jeffrey.wechat.interceptor.UserRequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.util.UrlPathHelper;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Configuration
@Slf4j
public class WeChatWebConfiguration implements WebMvcConfigurer {

    private final UserRequestInterceptor userRequestInterceptor;

    private final TotalInterceptor totalInterceptor;

    private final GetDocumentInterceptor getDocumentInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc/**").addResourceLocations(String.format("file:%s/", new File(WechatApplication.documentResPath, "/doc")))
                .setCacheControl(CacheControl.maxAge(183, TimeUnit.DAYS)
                .cachePrivate())
                .setCachePeriod(183 * (24  * 60 * 60));

        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS)
                .cachePublic())
                .setCachePeriod(365 * (24 * 60 * 60));
    }

    @Autowired
    public WeChatWebConfiguration(UserRequestInterceptor userRequestInterceptor, TotalInterceptor totalInterceptor, GetDocumentInterceptor getDocumentInterceptor) {
        this.userRequestInterceptor = userRequestInterceptor;
        this.totalInterceptor = totalInterceptor;
        this.getDocumentInterceptor = getDocumentInterceptor;
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

    /**
     * 配置拦截器
     * @param registry InterceptorRegistry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userRequestInterceptor).addPathPatterns("/**").excludePathPatterns("/static/**");
        registry.addInterceptor(totalInterceptor).addPathPatterns("/**").excludePathPatterns("/static/**");
        registry.addInterceptor(getDocumentInterceptor).addPathPatterns("/doc/**");
    }
}
