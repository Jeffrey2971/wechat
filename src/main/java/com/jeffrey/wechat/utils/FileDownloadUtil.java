package com.jeffrey.wechat.utils;

import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class FileDownloadUtil {

    /**
     * 下载给定链接中的文件并存储到给定的目录
     * 如果给定参数 savePath 为一个目录，那么将使用下载文件的 md5 作为保存文件名并返回
     * 如果给定参数 savePath 不为一个目录（例如 /temp/save.png），那么将使直接进行保存并返回该文件的文件名
     *
     * @param savePath 下载文件后存储的路径
     * @param httpUrl  需要被下载的 http 文件路径
     * @return 按照给定的 File 选择性返回
     */
    public static String download(File savePath, String httpUrl) {
        try {
            URL url = new URL(httpUrl);
            URLConnection urlConnection = url.openConnection();
            InputStream is = urlConnection.getInputStream();
            if (savePath.isDirectory()) {
                String md5Name = DigestUtils.md5DigestAsHex(is); // 计算 MD5
                FileCopyUtils.copy(is, new FileOutputStream(new File(savePath, (md5Name + ".png")).toString()));
                return md5Name;
            } else {
                FileCopyUtils.copy(is, new FileOutputStream(savePath));
                return savePath.getName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("文件下载异常");
    }

    public static void main(String[] args) {
        download(new File("/Users/jeffrey/IdeaProjects/wechat/src/test"), "https://xyq.gdl.netease.com/MHXY-JD-3.0.414.exe");
    }
}
