package com.jeffrey.wechat.translate;

import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import com.jeffrey.wechat.entity.translation.ApplyDocument;
import com.jeffrey.wechat.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
* @author jeffrey
* @since JDK 1.8
*/

@Component
public class DocumentTrans {

    private static WeChatAutoConfiguration.BaiduDocApplyConfig config;

    @Autowired
    public void setConfig(WeChatAutoConfiguration.BaiduDocApplyConfig config) {
        DocumentTrans.config = config;
    }

    /**
     * 统计校验上传的文档，包括返回的错误码、错误信息、数据集、文档字符数、文件 ID、预估消费金额
     * @param document 文档字节流
     * @param filename 文件名，需包括文件类型
     * @return ApplyDocument 内包含了
     * @throws IOException IOException
     */
    public static ApplyDocument apply(InputStream document, String filename, String from, String to) throws IOException {

        // 文档字节流
        ByteArrayResource bar = new ByteArrayResource(FileCopyUtils.copyToByteArray(document)) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        // 文档 md5
        String documentMd5 = DigestUtils.md5DigestAsHex(FileCopyUtils.copyToByteArray(bar.getInputStream()));

        // 请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.setContentLength(bar.contentLength());

        LinkedMultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();

        multiValueMap.add("appid", config.getBaiduTransactionAppId());
        multiValueMap.add("file", bar);
        multiValueMap.add("from", from);
        multiValueMap.add("to", to);
        multiValueMap.add("timestamp", System.currentTimeMillis() / 1000L); // ms -> s
        multiValueMap.add("type", filename.substring(filename.lastIndexOf(".") + 1));
        multiValueMap.add("sign", applyDocSign(multiValueMap.toSingleValueMap(), documentMd5));

        HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity<>(multiValueMap, httpHeaders);
        ResponseEntity<ApplyDocument> response = RequestUtil.postEntity(config.getReqCountUrl(), httpEntity, ApplyDocument.class, null);
        multiValueMap.clear();

        if (response.getStatusCodeValue() == 200 && response.getBody() != null) {
            return response.getBody();
        }

        throw new IOException();
    }


    /**
     * 文档统计校验签名
     * @param stringParams 参与校验的字符串参数，不包括 sign 本身以及 file 字段
     * @param documentMd5 文档 MD5
     * @return sign 签名
     */
    private static String applyDocSign(Map<String, Object> stringParams, String documentMd5) {
        ArrayList<String> params = new ArrayList<>(stringParams.keySet());
        params.remove("file");

        Collections.sort(params);
        StringBuilder splicingParams = new StringBuilder();
        for (String param : params) {
            splicingParams.append(param).append("=").append(stringParams.get(param)).append("&");
        }
        splicingParams.append(documentMd5);
        splicingParams.append(config.getBaiduTransactionAppKey());
        return DigestUtils.md5DigestAsHex(splicingParams.toString().getBytes(StandardCharsets.UTF_8));
    }
}
