package com.jeffrey.wechat;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import java.util.Objects;

@SpringBootApplication
@MapperScan("com.jeffrey.wechat.dao")
@Slf4j
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class WechatApplication {

    public static boolean projectStartedMode;

    public static void main(String[] args) {
        String path = Objects.requireNonNull(WechatApplication.class.getResource("/")).getPath();
        String[] split = path.split("/");
        for (String str : split) {
            if (str.contains(".jar")) {
                projectStartedMode = true;
                break;
            }
        }

        SpringApplication.run(WechatApplication.class, args);

        log.info("项目正在以 {} 方式启动", projectStartedMode ? "jar 环境" : "非 jar 环境");
    }
}
