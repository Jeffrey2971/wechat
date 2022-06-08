package com.jeffrey.wechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.jeffrey.wechat.WechatApplication;
import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import com.jeffrey.wechat.entity.mapper.UserInfo;
import com.jeffrey.wechat.entity.template.TemplateMessage;
import com.jeffrey.wechat.entity.translation.DocTranslation;
import com.jeffrey.wechat.entity.translation.DocTranslationData;
import com.jeffrey.wechat.entity.translation.TransDocument;
import com.jeffrey.wechat.mapper.DocumentTransServiceDao;
import com.jeffrey.wechat.service.DocumentTransService;
import com.jeffrey.wechat.translate.DocumentTrans;
import com.jeffrey.wechat.utils.FileDownloadInputStreamUtil;
import com.jeffrey.wechat.utils.GetAccessTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
@Slf4j
public class DocumentTransServiceImpl implements DocumentTransService {

    private final Tika TIKA = new Tika();
    private final RestTemplate REST_TEMPLATE = new RestTemplate();
    private final WeChatAutoConfiguration.ServerInfo serverInfo;
    private final DocumentTransServiceDao documentTransServiceDao;

    private final WeChatAutoConfiguration.WxConfig config;
    private final HashMap<String, String> types;
    private final HashMap<String, DocTranslation> applyMap;
    private final HashSet<String> lang;
    private final HashMap<String, String> documentMd5Map;

    private final HashMap<String, ArrayList<String>> userCanReadDocument;

    private static final HttpHeaders HTTP_HEADERS = new HttpHeaders();

    @Autowired
    public DocumentTransServiceImpl(
            WeChatAutoConfiguration.ServerInfo serverInfo,
            DocumentTransServiceDao documentTransServiceDao,
            WeChatAutoConfiguration.WxConfig config,
            HashMap<String, String> types,
            HashMap<String, DocTranslation> applyMap,
            HashSet<String> lang,
            HashMap<String, String> documentMd5Map,
            HashMap<String, ArrayList<String>> userCanReadDocument) {
        this.serverInfo = serverInfo;
        this.documentTransServiceDao = documentTransServiceDao;
        this.config = config;
        this.types = types;
        this.applyMap = applyMap;
        this.lang = lang;
        this.documentMd5Map = documentMd5Map;
        this.userCanReadDocument = userCanReadDocument;
    }


    @Override
    public boolean checkParams(MultipartFile file, String from, String to, String openid) {

        return
                file.isEmpty()
                        && StringUtils.hasText(from)
                        && StringUtils.hasText(to)
                        && !from.equals(to)
                        && lang.contains(from)
                        && lang.contains(to);
    }

    @Override
    public boolean checkFileType(InputStream is, String filename) throws IOException {
        String type = TIKA.detect(is, filename);

        log.info("上传的文件实际类型：{}", type);
        return types.containsKey(filename.substring(filename.lastIndexOf(".") + 1))
                && types.containsValue(type);
    }

    @Override
    public void checkDocumentCount(InputStream is, String filename, String from, String to, String openid) throws IOException {

        // 暂时用不到所以传入了 null 替代参数
        /*new Thread(new Runnable() {*/
        /*    @SneakyThrows*/
        /*    @Override*/
        /*    public void run() {*/
        /*        ApplyDocument apply = DocumentTrans.apply(null, null, null, null, null);*/

        /*        // 调用模板发送*/
        /*        sendDocTransResultTemplateMessage(*/
        /*                new DocTranslationData(*/
        /*                        null,*/
        /*                        null,*/
        /*                        null,*/
        /*                        null,*/
        /*                        null,*/
        /*                        null, -1*/
        /*                ),*/
        /*                new DocTranslation(*/
        /*                        null,*/
        /*                        null,*/
        /*                        null,*/
        /*                        null,*/
        /*                        null,*/
        /*                        -1*/
        /*                )*/
        /*        );*/
        /*    }*/
        /*}).start();*/
    }

    @Override
    public boolean applyDocumentTranslation(MultipartFile file, String filename, String from, String to, String openid) throws IOException {

        String documentMd5 = DigestUtils.md5DigestAsHex(FileCopyUtils.copyToByteArray(file.getInputStream()));
        String documentMediaType = new Tika().detect(file.getInputStream(), filename);
        /*
            判断文档是否需要重新申请翻译，统一文档用户可能上传多次，需要避免这样的问题从而节省资源开销，以下是判断文档是否需要翻译的条件

                1. documentMd5Map 中不包含指定键 documentMd5

                2. 用户可能上传文档名相同、文档 md5 特征相同，但选择的语言方向不同，
                    所以 documentMd5Map 中键 documentMd5 对应值的原语种和目标语种其中有一个不一致都需进行翻译

                3. 对应的
         */
        String oldName = documentMd5Map.get(documentMd5);
        String[] languageDirection = null;
        if (oldName != null) {
            languageDirection = oldName.split("_");
        }


        if (
                !documentMd5Map.containsKey(documentMd5) || (
                !from.equalsIgnoreCase(languageDirection != null ? languageDirection[languageDirection.length - 2] : null)
                        || !to.equalsIgnoreCase(languageDirection[languageDirection.length - 1]
                ) || !new File(WechatApplication.documentResPath, String.format("/doc/%s/%s", openid, filename)).exists()
        )
        ) {
            log.info("用户 {} 申请文档翻译", openid);

            documentMd5Map.put(documentMd5, String.format("%s_%s_%s", filename, from, to));

            TransDocument docTranslationData = DocumentTrans.docTrans(
                    file.getInputStream(),
                    null,
                    filename,
                    from,
                    to,
                    documentMd5
            );

            String error_code = docTranslationData.getError_code();

            if (Integer.parseInt(error_code) == 52000) {
                applyMap.put(docTranslationData.getData().getRequestId(), new DocTranslation(filename, openid, from, to, documentMediaType, System.currentTimeMillis()));

                return true;
            }

            log.error("文档翻译申请失败，原因是：{} | {}", error_code, docTranslationData.getError_msg());
            return false;
        }


        // 文件已缓存，不需要再申请翻译，但是存在的文件要改名避免获取不到

        log.info("用户 {} 申请的文档存在有效缓存，无需再次申请", openid);

        String[] oldNameSplit = oldName.split("_");

        File documentRealPath = new File(String.format("%s/doc", WechatApplication.documentResPath));

        if (!oldName.replace(oldName.substring(oldName.lastIndexOf(oldNameSplit[oldNameSplit.length - 2]) - 1), "").equals(filename) && !new File(documentRealPath, oldName).renameTo(new File(documentRealPath, filename))) {
            log.error("用户上传了内容相同但文件名称不相同的文档，在重命名时发生了错误，翻译失败");
        }

        DocTranslationData docTranslationData = new DocTranslationData(
                "52000",
                null,
                null,
                String.format("%s/%s?name=%s&openid=%s", serverInfo.getDomain(), "doc", filename, openid),
                null,
                null,
                0
        );

        DocTranslation docTranslation = new DocTranslation(
                filename,
                openid,
                from,
                to,
                documentMediaType,
                System.currentTimeMillis()
        );


        sendDocTransResultTemplateMessage(docTranslationData, docTranslation);

        return true;

    }

    @Override
    public boolean isUser(String openid) {
        return documentTransServiceDao.selectCount(new QueryWrapper<UserInfo>().eq("openid", openid)) > 0;
    }

    @Override
    public boolean documentCallBack(Map<String, Object> params) throws IOException {

        String requestId = (String) params.get("requestId");

        if (!applyMap.containsKey(requestId)) {
            return false;
        }

        DocTranslation docTranslation = applyMap.get(requestId);

        File userFolder = new File(WechatApplication.documentResPath, String.format("/doc/%s", docTranslation.getOpenid()));

        // 为每一个用户创建一个专属的文件夹用于存储用户的文档
        if (!userFolder.exists() && !userFolder.mkdirs()) {
            log.error("创建用户文件夹失败");
            return false;
        }

        String filename = docTranslation.getFilename();

        File documentRealPath = new File(WechatApplication.documentResPath, String.format("/doc/%s/%s", docTranslation.getOpenid(), filename));

        if (FileCopyUtils.copy(
                FileDownloadInputStreamUtil.download((String) params.get("fileSrcUrl")),
                Files.newOutputStream(documentRealPath.toPath())) <= 0
        ) {
            return false;
        }

        //----------------- 这里处理用户的文档浏览功能 -----------------//

        String addTaskUrl = String.format("%s?url=%s/doc/%s/%s?openid=%s", serverInfo.getAddTask(), serverInfo.getDomain(), docTranslation.getOpenid(), filename, docTranslation.getOpenid());

        log.info("添加预浏览队列：{}", REST_TEMPLATE.getForObject(addTaskUrl, String.class));

        String reqLink = String.format("%s/doc?openid=%s&name=%s", serverInfo.getDomain(), docTranslation.getOpenid(), filename);

        //-----------------         发送模板         ----------------//

        sendDocTransResultTemplateMessage(new DocTranslationData(
                        (String) params.get("error_code"),
                        (String) params.get("error_msg"),
                        requestId,
                        reqLink,
                        (String) params.get("sign"),
                        (String) params.get("amount"),
                        Integer.parseInt(String.valueOf(params.get("charCount")))
                ), docTranslation
        );

        // 添加用户可读取文档，如果包含 key 说明对应的 value 一定不为 null 且一般都有元素。因为对象的引用机制，所以更新的值不需要重新放进 Map 中
        if (
                userCanReadDocument.containsKey(docTranslation.getOpenid()) &&
                        userCanReadDocument.get(docTranslation.getOpenid()).add(filename)
        ) {return true;}

        userCanReadDocument.put(docTranslation.getOpenid(), new ArrayList<>(Collections.singleton(filename)));

        return true;
    }

    @Override
    public void sendDocTransResultTemplateMessage(DocTranslationData docTranslationData, DocTranslation docTranslation) {

        int errorCode = Integer.parseInt(docTranslationData.getError_code());
        String from = docTranslation.getLangFrom();
        String to = docTranslation.getLangTo();

        //------------------------ 构建请求体 ------------------------//

        String requestBody = new Gson().toJson(new TemplateMessage(
                docTranslation.getOpenid(),
                config.getWxGetSendTemplateId(),
                errorCode == 52000 ? docTranslationData.getFileSrcUrl() : null,
                new TemplateMessage.Data(
                        new TemplateMessage.First("您上传的文档有新的动态", "#173177"),
                        new TemplateMessage.KeyWord1(docTranslation.getFilename(), "#173177"),
                        new TemplateMessage.KeyWord2(String.format("%s -> %s",

                                from.equalsIgnoreCase("zh")
                                        ? "中文" : from.equalsIgnoreCase("en")
                                        ? "英文" : from.equalsIgnoreCase("jp")
                                        ? "日文" : from.equalsIgnoreCase("kor")
                                        ? "韩文" : "未知",

                                to.equalsIgnoreCase("zh")
                                        ? "中文" : to.equalsIgnoreCase("en")
                                        ? "英文" : to.equalsIgnoreCase("jp")
                                        ? "日文" : to.equalsIgnoreCase("kor")
                                        ? "韩文" : "未知"), "#173177"),

                        new TemplateMessage.KeyWord3(errorCode == 52000 ? "成功" : "失败", "#173177"),
                        new TemplateMessage.KeyWord4(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(LocalDateTime.now()), "#173177"),
                        new TemplateMessage.Remark(errorCode == 52000 ? "点击详情下载翻译文档，感谢您的使用！" : "本次翻译失败，请检查您上传的文档！", "#173177")))
        );

        //----------------------- 解决模板消息乱码 -----------------------//

        HTTP_HEADERS.setContentType(MediaType.APPLICATION_JSON);
        HTTP_HEADERS.setContentLanguage(Locale.CHINA);
        HTTP_HEADERS.setContentLength(requestBody.length());

        HttpEntity<Object> httpEntity = new HttpEntity<>(requestBody, HTTP_HEADERS);

        REST_TEMPLATE.postForObject(config.getWxSendTemplateMessageUrl(), httpEntity, String.class, GetAccessTokenUtil.getToken());

        HTTP_HEADERS.clear();
    }
}