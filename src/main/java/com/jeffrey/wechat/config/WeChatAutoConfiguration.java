package com.jeffrey.wechat.config;

import com.jeffrey.wechat.entity.TransResponseWrapper;
import com.jeffrey.wechat.entity.message.*;
import com.jeffrey.wechat.entity.translation.DocTranslation;
import com.thoughtworks.xstream.XStream;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.LinkedMultiValueMap;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;


@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Configuration
@EnableConfigurationProperties({
        WeChatAutoConfiguration.WxConfig.class,
        WeChatAutoConfiguration.EmailConfig.class,
        WeChatAutoConfiguration.BaiduTranslationConfig.class,
        WeChatAutoConfiguration.CliConfig.class,
        WeChatAutoConfiguration.ThreadPoolConfig.class,
        WeChatAutoConfiguration.ServerInfo.class,
        WeChatAutoConfiguration.BaiduDocApplyConfig.class
})
public class WeChatAutoConfiguration {

    private final ThreadPoolConfig config;

    private final EmailConfig emailConfig;

    @Autowired
    public WeChatAutoConfiguration(ThreadPoolConfig config, EmailConfig emailConfig) {
        this.config = config;
        this.emailConfig = emailConfig;
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.corePoolSize);
        executor.setMaxPoolSize(config.maxPoolSize);
        executor.setKeepAliveSeconds(config.keepAliveSeconds);
        executor.setQueueCapacity(config.queueCapacity);
        return executor;
    }

    @Bean
    public LinkedMultiValueMap<String, Object> transValueMap() {
        return new LinkedMultiValueMap<>();
    }

    @Bean
    public Random random() {
        return new Random();
    }

    @Bean
    public SimpleMailMessage simpleMailMessage() {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom(emailConfig.getEmailFrom());
        smm.setTo(emailConfig.getEmailTo());
        smm.setCc(emailConfig.getEmailCC());
        return smm;
    }

    @Bean
    public MimeMessage mimeMessage(JavaMailSender javaMailSender) {
        return javaMailSender.createMimeMessage();
    }

    @Bean
    public MimeMessageHelper mimeMessageHelper(MimeMessage mimeMessage) throws MessagingException {
        MimeMessageHelper mailHelper = new MimeMessageHelper(mimeMessage, true);
        mailHelper.setFrom(emailConfig.getEmailFrom());
        mailHelper.setTo(emailConfig.getEmailTo());
        mailHelper.setCc(emailConfig.getEmailCC());
        return mailHelper;
    }

    @Bean
    public HashMap<String, Long> userBlackMap() {
        return new HashMap<>();
    }

    @Bean
    public HashMap<String, Integer> userFrequency() {
        return new HashMap<>();
    }

    @Bean
    public HashMap<Long, TransResponseWrapper> userDataItem() {
        return new HashMap<>();
    }

    @Bean
    public HashMap<String, DocTranslation> applyMap(){
        return new HashMap<>();
    }

    @Data
    @ConfigurationProperties(prefix = "pool")
    public static class ThreadPoolConfig {
        private int corePoolSize;
        private int maxPoolSize;
        private int keepAliveSeconds;
        private int queueCapacity;
    }

    @Bean
    public HashSet<String> lang(){
        HashSet<String> lang = new HashSet<>();
        lang.add("中文");
        lang.add("英文");
        lang.add("韩文");
        lang.add("日文");
        return lang;
    }

    @Bean
    public HashMap<String, String> documentMd5Map(){
        return new HashMap<>();
    }

    @Bean
    public HashMap<String, String> types(){
        HashMap<String, String> types = new HashMap<>();
        types.put("doc", "application/msword"); // doc
        types.put("txt", "text/plain"); // txt
        types.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"); // docx
        types.put("pdf", "application/pdf"); // pdf
        types.put("xls", "application/vnd.ms-excel"); // xls
        types.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // xlsx
        types.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"); // pptx
        types.put("ppt", "application/vnd.ms-powerpoint"); // ppt
        return types;
    }

    @Bean
    public XStream xStream() {
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
    @ConfigurationProperties(prefix = "wechat")
    public static class WxConfig {
        private String wxToken;
        private String wxAppId;
        private String wxAppSecret;
        private String wxCustomerUrl;
        private String wxGetTokenUrl;
        private String wxGetUserInfoUrl;
        private String wxGetQrCodeUrl;
        private String wxUploadMediaDataUrl;
        private String wxGetUserOpenIdList;
        private String wxDeleteMediaDataUrl;
        private String wxDeleteMenuUrl;
        private String wxCreateMenuUrl;
        private String wxShowUseUrl;
        private String wxGetBatchUserOpenIdList;
        private String wxSendTemplateMessageUrl;
        private String wxGetSendTemplateId;
        private Integer wxShareThreshold;
        private Integer wxDayCanUse;
        private List<String> thankKeywords;
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
        private int version;
        private String cuid;
        private String mac;
        private String zh, en, jp, kor, fra, spa, ru, pt, de, it, dan, nl, may, swe, id, pl, rom, tr, el, hu, auto;
        private String reqUrl;
        private String fileContentType;
    }

    @Data
    @ConfigurationProperties(prefix = "baidu.doc")
    public static class BaiduDocApplyConfig {
        private String baiduTransactionAppId;
        private String baiduTransactionAppKey;
        private String reqCountUrl;
        private String reqTransUrl;
    }


    @Data
    @ConfigurationProperties(prefix = "cli")
    public static class CliConfig {
        private String cliStyleId;
        private String getQrCodeStyleUrl;
    }

    @Data
    @ConfigurationProperties(prefix = "domain")
    public static class ServerInfo {
        private String domain;
    }
}

