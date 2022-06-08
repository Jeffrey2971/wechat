package com.jeffrey.wechat;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Objects;

@SpringBootApplication
@MapperScan("com.jeffrey.wechat.mapper")
@Slf4j
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class WechatApplication {

    public static boolean projectStartedMode;
    public static String documentResPath;

    public static void main(String[] args) throws IOException {
        for (String str : Objects.requireNonNull(WechatApplication.class.getResource("/")).getPath().split("/")) {
            if (str.contains(".jar")) {
                projectStartedMode = true;
                break;
            }
        }

        ApplicationHome applicationHome = new ApplicationHome(WechatApplication.class);

        File source = applicationHome.getSource();

        if (source == null) {
            throw new RuntimeException("无法确定项目资源路径");
        }

        documentResPath = source.getParentFile().getPath();

        File view = new File(documentResPath, "/view");
        if (!view.exists() && !view.mkdirs()) {
            throw new RuntimeException("创建浏览视图文件目录失败");
        }

        File doc = new File(documentResPath, "/doc");
        if (!doc.exists() && !doc.mkdirs()) {
            throw new RemoteException("创建文档存储目录失败");
        }

        if (Runtime.getRuntime().exec("lsof -i:8012").getInputStream().read() == -1) {
            throw new RuntimeException("kkFileView 没有启动，没有找到 8012 端口");
        }

        SpringApplication.run(WechatApplication.class, args);

        log.info("项目正运行于 {}", documentResPath);
        log.info("项目正在以 {} 方式启动", projectStartedMode ? "jar 环境" : "非 jar 环境");


    }
}
