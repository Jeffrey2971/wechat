package com.jeffrey.wechat.utils;

import com.google.gson.Gson;
import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import com.jeffrey.wechat.entity.UploadMediaResult;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.HashMap;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
public class FileUploadMediaDataUtil {

    private static WeChatAutoConfiguration.WxConfig wxConfig;

    @Autowired
    private void setWxConfig(WeChatAutoConfiguration.WxConfig wxConfig) {
        FileUploadMediaDataUtil.wxConfig = wxConfig;
    }

    @SneakyThrows // 待处理异常
    public static UploadMediaResult fileUpload(@NotNull InputStream is, @NotNull String fileName, @NotNull String fileSuffix) {

        // 请求头
        LinkedMultiValueMap<String, Object> data = new LinkedMultiValueMap<>();

        ByteArrayResource bar = new ByteArrayResource(FileCopyUtils.copyToByteArray(is)) {
            @Override
            public String getFilename() {
                return fileName + "." + fileSuffix;
            }
        };

        data.add("media", bar);

        // 头部分
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.setContentLength(bar.contentLength());

        HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity<>(data, httpHeaders);

        HashMap<String, Object> httpParams = new HashMap<>();
        httpParams.put("token", GetAccessTokenUtil.getToken());
        httpParams.put("type", fileSuffix);


        ResponseEntity<String> resultResponseEntity = RequestUtil.postEntity(wxConfig.getWxUploadMediaDataUrl(), httpEntity, String.class, httpParams);


        if (resultResponseEntity.getStatusCodeValue() == 200 && resultResponseEntity.getBody() != null) {
            String body = resultResponseEntity.getBody();
            return new Gson().fromJson(body, UploadMediaResult.class);
        }

        return null;

    }


}
