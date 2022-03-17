package com.jeffrey.wechat.config;

import com.google.gson.Gson;
import com.jeffrey.wechat.entity.message.*;
import com.thoughtworks.xstream.XStream;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;


@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Configuration
@EnableConfigurationProperties({
        WeChatAutoConfiguration.WxConfig.class,
        WeChatAutoConfiguration.EmailConfig.class,
        WeChatAutoConfiguration.BaiduTranslationConfig.class,
        WeChatAutoConfiguration.CliConfig.class,
        WeChatAutoConfiguration.ThreadPoolConfig.class
})
public class WeChatAutoConfiguration {

    @Autowired
    private ThreadPoolConfig config;

    @Bean
    public Executor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.corePoolSize);
        executor.setMaxPoolSize(config.maxPoolSize);
        executor.setKeepAliveSeconds(config.keepAliveSeconds);
        executor.setQueueCapacity(config.queueCapacity);
        return executor;
    }

    @Bean
    public Gson gson(){
        return new Gson();
    }

    @Bean
    public XStream xStream(){
        XStream xStream = new XStream();
        // 批量解析标注了 @XStreamAlias 的类，这些类只需要被解析一次
        xStream.processAnnotations(new Class[]{
                ImageMessage.class,
                LinkMessage.class,
                LocationMessage.class,
                MusicMessage.class,
                NewsMessage.class,
                SmallVideoMessage.class,
                TextMessage.class,
                VideoMessage.class,
                VoiceMessage.class
        });
        return xStream;
    }

    @Data
    @ConfigurationProperties(prefix = "pool")
    public static class ThreadPoolConfig{
        private int corePoolSize;
        private int maxPoolSize;
        private int keepAliveSeconds;
        private int queueCapacity;
    }

    @Data
    @ConfigurationProperties(prefix = "wechat")
    public static class WxConfig {
        private String wxToken;
        private String wxAppId;
        private String wxAppSecret;
    }

    @Data
    @ConfigurationProperties(prefix = "email")
    public static class EmailConfig {
        private String emailFrom;
        private String emailTo;
        private Integer emailPort;
        private String emailHost;
        private String emailCC;
        private String emailPassCode;
    }

    @Data
    @ConfigurationProperties(prefix = "baidu")
    public static class BaiduTranslationConfig {
        private String baiduTransactionAppId;
        private String baiduTransactionAppKey;
        private int eraseNone;
        private int eraseFull;
        private int eraseBlock;
        private int pasteNone;
        private int pasteFull;
        private int pasteBlock;
        private int close;
        private String zh, en, jp, kor, fra, spa, ru, pt, de, it, dan, nl, may, swe, id, pl, rom, tr, el, hu, auto;
        private String reqUrl;
        private String fileContentType;

    }

    @Data
    @ConfigurationProperties(prefix = "cli")
    public static class CliConfig {
        private String cliStyleId;
        private String cliActionType;
    }
}

