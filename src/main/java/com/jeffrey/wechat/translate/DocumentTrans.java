package com.jeffrey.wechat.translate;

import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import com.jeffrey.wechat.entity.translation.ApplyDocument;
import com.jeffrey.wechat.entity.translation.TransDocument;
import com.jeffrey.wechat.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
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
     *
     * @param file     文档字节流
     * @param filename 文件名，需包括文件类型
     * @return ApplyDocument 内包含了
     * @throws IOException IOException
     */
    public static ApplyDocument apply(InputStream file, String filename, String from, String to, String documentMd5) throws IOException {

        Object[] objects = prepareRequest(file, from, to, filename, documentMd5, false);

        LinkedMultiValueMap<String, Object> multiValueMap = (LinkedMultiValueMap<String, Object>) objects[0];
        HttpHeaders httpHeaders = (HttpHeaders) objects[1];

        HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity<>(multiValueMap, httpHeaders);
        ResponseEntity<ApplyDocument> response = RequestUtil.postEntity(config.getReqCountUrl(), httpEntity, ApplyDocument.class, null);
        multiValueMap.clear();

        if (response.getStatusCodeValue() == 200 && response.getBody() != null) {
            return response.getBody();
        }

        throw new IOException();
    }

    public static TransDocument docTrans(@Nullable InputStream file,
                                         @Nullable String fileId,
                                         String filename,
                                         String from,
                                         String to,
                                         String documentMd5) throws IOException {

        Object[] objects = prepareRequest(file, from, to, filename, documentMd5,true);

        LinkedMultiValueMap<String, Object> multiValueMap = (LinkedMultiValueMap<String, Object>) objects[0];
        HttpHeaders httpHeaders = (HttpHeaders) objects[1];

        HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity<>(multiValueMap, httpHeaders);
        ResponseEntity<TransDocument> response = RequestUtil.postEntity(config.getReqTransUrl(), httpEntity, TransDocument.class, null);
        multiValueMap.clear();

        if (response.getStatusCodeValue() == 200 && response.getBody() != null) {
            return response.getBody();
        }

        throw new IOException();

    }

    /**
     * 文档的校验统计和文档的翻译所需的参数都差不多，所以写了这个方法
     */
    public static Object[] prepareRequest(
            InputStream file,
            String from,
            String to,
            String filename,
            String documentMd5,
            boolean isTrans
    ) throws IOException {

        Object[] obj = new Object[2];

        ByteArrayResource bar = new ByteArrayResource(FileCopyUtils.copyToByteArray(file)) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

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


        // 如果是 doc / docx / pdf 格式则保持原有格式输出
        if (isTrans) {

            String filetype = filename.substring(filename.lastIndexOf(".") + 1);
            String type;

            type = "doc".equalsIgnoreCase(filetype)
                    ? "doc" : "docx".equalsIgnoreCase(filetype)
                    ? "docx" : "pdf".equalsIgnoreCase(filetype)
                    ? "pdf" : null;
            if (type != null) {

                multiValueMap.add("outPutType", type);
            }
        }

        multiValueMap.add("sign", applyDocSign(multiValueMap.toSingleValueMap(), documentMd5));

        obj[0] = multiValueMap;
        obj[1] = httpHeaders;

        return obj;
    }


    /**
     * 文档统计校验签名
     *
     * @param stringParams 参与校验的字符串参数，不包括 sign 本身以及 file 字段
     * @param documentMd5  文档 MD5
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
