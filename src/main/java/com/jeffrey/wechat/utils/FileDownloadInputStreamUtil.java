package com.jeffrey.wechat.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URL;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Slf4j
public class FileDownloadInputStreamUtil {

    public static InputStream download(String httpUrl) throws IOException {

        if (!StringUtils.hasText(httpUrl)) {
            log.error("提供的链接不能为空");
        }

        log.info("开始下载文件字节流：{}", httpUrl);
        return new URL(httpUrl).openConnection().getInputStream();
    }
}
