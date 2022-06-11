package com.jeffrey.wechat.controller;

import com.jeffrey.wechat.WechatApplication;
import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import com.jeffrey.wechat.entity.translation.DocTranslationData;
import com.jeffrey.wechat.service.DocumentTransService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
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
    private final WeChatAutoConfiguration.ServerInfo serverInfo;
    private final HashMap<String, ArrayList<String>> userCanReadDocument;
    private int prevHashCode;

    @Autowired
    public DocumentTranController(DocumentTransService documentTransService, WeChatAutoConfiguration.ServerInfo serverInfo, HashMap<String, ArrayList<String>> userCanReadDocument) {
        this.documentTransService = documentTransService;
        this.serverInfo = serverInfo;
        this.userCanReadDocument = userCanReadDocument;
    }

    @PostMapping(value = "/document_upload")
    public String receiveDocument(
            @RequestParam(value = "select-file", required = false) MultipartFile file,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @RequestParam(value = "uid", required = false) String openid,
            Model model) throws IOException, MimeTypeException {

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename().replace(" ", "") : null;

        if (StringUtils.hasText(openid) && documentTransService.isUser(openid)) {

            if (documentTransService.checkParams(file, from, to, openid, originalFilename)) {
                model.addAttribute("title", "上传失败");
                model.addAttribute("label1", "400 Bad Request");
                model.addAttribute("label2", "请求参数错误，请重试");
                return "error/4XX";
            }

            if (!documentTransService.checkFileType(file.getInputStream(), originalFilename)) {
                model.addAttribute("title", "上传失败");
                model.addAttribute("label1", "400 Bad Request");
                model.addAttribute("label2", String.format("上传的文件 [%s] 不是一个有效的文件", originalFilename));
                return "error/4XX";
            }

            from = new String(from.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            to = new String(to.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

            if (documentTransService.applyDocumentTranslation(file, originalFilename, from, to, openid)) {
                model.addAttribute("title", "上传成功");
                model.addAttribute("msg", "已收到您的翻译申请，翻译后的文档将通过消息模板的方式发到您的微信！");
                return "feedback_success";
            } else {
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
    public void callback(DocTranslationData docTranslationData) throws IOException {

        if (docTranslationData.hashCode() == prevHashCode) {
            log.info("重复的请求，已忽略");
            return;
        } else {
            synchronized (this) {
                prevHashCode = docTranslationData.hashCode();
            }
        }

        documentTransService.documentCallBack(docTranslationData);
    }

    @GetMapping("/doc")
    public String getDocument(
            @RequestParam(value = "name", required = false) String filename,
            @RequestParam(value = "openid", required = false) String openid,
            Model model
    ) throws IOException {

        filename = filename.replace(" ", "");

        // userCanReadDocument 集合的 key 为用户 openid ，value 为一个 ArrayList，其中包含了用户 openid 对应的可读取文件名称
        if (
                !new File(WechatApplication.documentResPath, String.format("/doc/%s/%s", openid, filename)).exists()
                        || !userCanReadDocument.containsKey(openid)  // 可读取文档集合中没有当前 openid
                        || !userCanReadDocument.get(openid).contains(filename) // 可读取文档集合中存在用户 openid ，但是请求的文档没有权限阅读（换句话说这个文档不属于当前用户）

        ) {
            model.addAttribute("title", "文档找不到");
            model.addAttribute("label1", "404 Not Fount");
            model.addAttribute("label2", "该文档不存在或已过期或它不属于您，请重新获取");

            return "error/4XX";
        }


        //--------------------------------------------- 处理用户文档在线预览 ---------------------------------------------//
        // 注意：这里的 /doc 并不是请求路由，而是文件夹名称，所以这个链接的请求地址不会到这个控制器，                                 //
        //      但是应该被拦截器 GetDocumentInterceptor 拦截，因为控制器无法校验直接访问静态资源的请求                            //
        //      [ http://localhost/doc != http://localhost/doc/test.pdf ]                                              //
        //                                                                                                             //

        String originUrl = String.format("%s/doc/%s/%s", serverInfo.getDomain(), openid, URLEncoder.encode(filename, "UTF-8")); // 资源路径
        String previewUrl = String.format("%s?openid=%s", originUrl, openid); // 带上 kkFileView 所需的文件名
        String encodeSourceLink = Base64.getEncoder().encodeToString(previewUrl.getBytes(StandardCharsets.UTF_8)); // 对资源路径进行 base64 编码
        String finalLink = String.format("%s?url=%s", serverInfo.getOnlinePreview(), encodeSourceLink); // 最终的资源访问路径


        return "redirect:" + finalLink;
    }
}
