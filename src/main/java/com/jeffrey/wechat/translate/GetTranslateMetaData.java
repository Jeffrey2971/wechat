package com.jeffrey.wechat.translate;

import com.google.gson.Gson;
import com.jeffrey.wechat.entity.Config;
import com.jeffrey.wechat.entity.translation.TranslationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

/**
 * 根据给定的图片返回该图片中的数据信息
 */
@Component
@Slf4j
public class GetTranslateMetaData {

    private static Config.BaiduTranslationConfig config;

    private static HttpParams params;

    private static final HttpClient httpClient = new HttpClient();


    @Autowired
    private void setParams(HttpParams params) {
        GetTranslateMetaData.params = params;
    }

    @Autowired
    private void setConfig(Config.BaiduTranslationConfig config) {
        GetTranslateMetaData.config = config;
    }


    /**
     * @param localPicUrlPath 本地照片路径
     * @return 响应体封装对象
     * 传递一个本地的图片地址，返回 TranslationData 对象
     * TranslationData 内部封装了关于图片中的信息
     */
    public static TranslationData getData(String localPicUrlPath) {

        final File image = new File(localPicUrlPath);

        if (!image.exists() && !image.isFile()) {
            log.error("给定的图片路径参数：{}，不是一个有效的本地图片路径", image.getAbsolutePath());
            throw new RuntimeException("给定的文件路径不存在或它不是图片");
        }

        params.put("image", image, config.getFileContentType());
        params.put("from", config.getAuto());
        params.put("to", config.getZh());
        params.put("appid", config.getBaiduTransactionAppId());
        params.put("salt", 1631027881);
        params.put("cuid", "APICUID");
        params.put("mac", "mac");
        params.put("version", "3");
        params.put("paste", config.getPasteFull());
        params.put("erase", config.getEraseNone());
        try {
            params.put("sign", sign(config, params));
        } catch (IOException e) {
            throw new RuntimeException("参数加密过程中出现了异常");
        }

        return new Gson().fromJson(httpClient.post(config.getReqUrl(), params), TranslationData.class);
    }

    private static String sign(Config.BaiduTranslationConfig config, HttpParams params) throws IOException {
        if (config == null || params == null) {
            throw new RuntimeException("传递的参数 Config.BaiduTranslationConfig config 或 HttpParams params 不能为空");
        }
        Map<String, String> stringParams = params.getStringParams();
        Map<String, HttpParams.FileWrapper> fileParams = params.getFileParams();


        HttpParams.FileWrapper file = fileParams.get("image");


//        String md5Image = DigestUtils.md5DigestAsHex(Files.readAllBytes(file.file.toPath()));
        String md5Image = DigestUtils.md5DigestAsHex(FileCopyUtils.copyToByteArray(file.file));


        return DigestUtils.md5DigestAsHex((config.getBaiduTransactionAppId() + md5Image + stringParams.get("salt") + stringParams.get("cuid") + stringParams.get("mac") + config.getBaiduTransactionAppKey()).getBytes()).toLowerCase();
    }
}
