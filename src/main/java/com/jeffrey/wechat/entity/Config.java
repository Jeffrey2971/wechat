package com.jeffrey.wechat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Component
public class Config {

    @Component
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ConfigurationProperties(prefix = "wechat")
    public static class WxConfig {
        private String wxToken;
        private String wxAppId;
        private String wxAppSecret;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ConfigurationProperties(prefix = "email")
    @Component
    public static class EmailConfig {
        private String emailFrom;
        private String emailTo;
        private Integer emailPort;
        private String emailHost;
        private String emailCC;
        private String emailPassCode;
    }


    @Component
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
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
    @NoArgsConstructor
    @AllArgsConstructor
    @ConfigurationProperties(prefix = "cli")
    @Component
    public static class CliConfig {
        private String cliStyleId;
        private String cliActionType;
    }
}

