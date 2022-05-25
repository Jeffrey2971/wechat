package com.jeffrey.wechat.controller;

import com.jeffrey.wechat.service.ApplyDocumentService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@RestController
@Slf4j
public class ApplyDocumentController {

    private final ApplyDocumentService applyDocumentService;

    @Autowired
    public ApplyDocumentController(ApplyDocumentService applyDocumentService) {
        this.applyDocumentService = applyDocumentService;
    }

    @PostMapping(value = "/check-doc-count")
    public String doc(
            @RequestParam(value = "select-file", required = false) MultipartFile file,
            @RequestParam(value = "from", required = false) String param1,
            @RequestParam(value = "to", required = false) String param2,
            @RequestParam(value = "uid", required = false) String openid) throws IOException {

        if (applyDocumentService.checkParams(file, param1, param2, openid)) {
            return "参数不正确";
        }

        if (!applyDocumentService.checkFileType(file.getInputStream(), file.getOriginalFilename())) {
            return String.format("上传的文件 %s 不是一个有效的文件", file.getOriginalFilename());
        }

        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                String from = new String(param1.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                String to = new String(param2.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

                applyDocumentService.checkDocumentCount(file.getInputStream(), file.getOriginalFilename(), from, to, openid);
            }
        }).start();

        return "<br /><br /><h3 style='color: green; text-align: center;'>已收到您的翻译申请，统计后的文档将通过消息模板的方式发到您的微信！</h3>";

    }
}
