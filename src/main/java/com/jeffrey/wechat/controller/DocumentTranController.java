package com.jeffrey.wechat.controller;

import com.jeffrey.wechat.WechatApplication;
import com.jeffrey.wechat.service.DocumentTransService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 这个控制器用于处理用户上传的文档，包括文档的校验及统计
 *
 * @author jeffrey
 * @since JDK 1.8
 */
@Controller
@Slf4j
public class DocumentTranController {

    private final DocumentTransService documentTransService;
    private int prevHashCode;

    @Autowired
    public DocumentTranController(DocumentTransService documentTransService) {
        this.documentTransService = documentTransService;
    }

    @PostMapping(value = "/document_upload")
    public String doc(
            @RequestParam(value = "select-file", required = false) MultipartFile file,
            @RequestParam(value = "from", required = false) String param1,
            @RequestParam(value = "to", required = false) String param2,
            @RequestParam(value = "uid", required = false) String openid,
            Model model) throws IOException {

        if (StringUtils.hasText(openid) && documentTransService.isUser(openid)) {

            if (documentTransService.checkParams(file, param1, param2, openid)) {
                model.addAttribute("title", "上传失败");
                model.addAttribute("label1", "400 Bad Request");
                model.addAttribute("label2", "请求参数错误，请重试");
                return "error/4XX";
            }

            if (!documentTransService.checkFileType(file.getInputStream(), file.getOriginalFilename())) {
                model.addAttribute("title", "上传失败");
                model.addAttribute("label1", "400 Bad Request");
                model.addAttribute("label2", String.format("上传的文件 [%s] 不是一个有效的文件", file.getOriginalFilename()));
                return "error/4XX";
            }

            String from = new String(param1.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            String to = new String(param2.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

            if (documentTransService.transDocument(file, file.getOriginalFilename(), from, to, openid)) {
                model.addAttribute("title", "上传成功");
                model.addAttribute("msg", "已收到您的翻译申请，翻译后的文档将通过消息模板的方式发到您的微信！");
                return "feedback_success";
            }else{
                model.addAttribute("title", "文档申请翻译失败");
                model.addAttribute("label1", "500 Internal Server Error");
                model.addAttribute("label2", "您上传的文档在处理时出现了异常，请过段时间再试");
                return "error/4XX";
            }

        } else {
            model.addAttribute("title", "请先关注");
            model.addAttribute("label1", "401 Unauthorized");
            model.addAttribute("label2", "请先长按以下二维码关注本公众号后再继续");
            return "error/4XX";
        }
    }

    @GetMapping("/callback")
    @ResponseBody
    public void callback(@RequestParam Map<String, Object> params) throws IOException {

        if (params.hashCode() == prevHashCode) {
            log.info("重复的请求，已忽略");
            return;
        }

        synchronized (this) {
            prevHashCode = params.hashCode();
        }

        if (documentTransService.checkDocumentRequest(params)) {

        }

    }

    @GetMapping("/doc")
    public String getDocument(
            @RequestParam(value = "name", required = false) String filename,
            Model model
    ) {

        if (!new File(WechatApplication.documentResPath, String.format("/doc/%s", filename)).exists()) {
            model.addAttribute("title", "文档找不到");
            model.addAttribute("label1", "404 Not Fount");
            model.addAttribute("label2", "该文档不存在或已过期，请重新获取");

            return "error/4XX";
        }

        return String.format("forward:doc/%s", filename);
    }
}
