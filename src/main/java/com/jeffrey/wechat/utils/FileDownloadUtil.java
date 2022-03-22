package com.jeffrey.wechat.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URL;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Slf4j
public class FileDownloadUtil {

    /**
     * 下载给定链接中的文件并存储到给定的目录（只对图片有优化）
     * 如果给定参数 savePath 为一个目录，那么将使用下载文件的 md5 作为保存文件名并返回
     * 如果给定参数 savePath 不为一个目录（例如 /temp/save.png），那么将使直接进行保存并返回该文件的文件名
     *
     * @param savePath 下载文件后存储的路径
     * @param httpUrl  需要被下载的 http 文件路径
     * @return 按照给定的 File 选择性返回
     */
    public static String download(File savePath, String httpUrl) {
        log.info("开始下载：{}", httpUrl);
        if (!StringUtils.hasText(httpUrl) || savePath == null) {
            throw new RuntimeException("提供的参数为空");
        }
        try {
            InputStream is = new URL(httpUrl).openConnection().getInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream(4 * (1024 * 1024));
            byte[] buffer = new byte[4 * (1024 * 1024)];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.close();
            if (savePath.isDirectory()) {
                String md5Name = DigestUtils.md5DigestAsHex(os.toByteArray());
                FileCopyUtils.copy(os.toByteArray(), new FileOutputStream(new File(savePath, (md5Name + ".png")).toString()));
                return md5Name;
            } else {
                FileCopyUtils.copy(os.toByteArray(), new FileOutputStream(savePath));
                return savePath.getName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("文件下载异常");
    }
}
