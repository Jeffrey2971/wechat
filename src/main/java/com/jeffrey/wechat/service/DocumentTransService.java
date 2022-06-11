package com.jeffrey.wechat.service;

import org.apache.tika.mime.MimeTypeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.jeffrey.wechat.entity.translation.DocTranslation;
import com.jeffrey.wechat.entity.translation.DocTranslationData;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
public interface DocumentTransService {

    /**
     * 判断传入的参数是否正确、有效
     * @param from 源语言
     * @param to 目标语言
     * @param openid 用户的 Openid
     * @param originFileName 上传的文件名
     * @return 传入的参数是否正确
     */
    boolean checkParams(MultipartFile file, String from, String to, String openid, String originFileName);

    /**
     * 严格校验上传文件的后缀名以及文件的实际类型
     *
     * @param is       文件的输入流
     * @param filename 文件名
     * @return 是否是一个正确的文件
     * @throws IOException IOException
     */
    boolean checkFileType(InputStream is, String filename) throws IOException;

    /**
     * 统计校验上传的文档，包括返回的错误码、错误信息、数据集、文档字符数、文件 ID、预估消费金额，该方法不直接返回数据，而是直接调用模板发送
     *
     * @param is       文档的输入流
     * @param filename 文件名
     * @param openid 给目标用户发送模板消息的 Openid
     * @throws IOException IOException
     */
    void checkDocumentCount(InputStream is, String filename, String from, String to, String openid) throws IOException;

    /**
     * 申请文档翻译，文档的申请的翻译结果不是同步的，具体的翻译结果是通过异步调用接口实现
     * @param file 上传的文件
     * @param filename 上传的文件名
     * @param from 文档原语种
     * @param to 文档目标语种
     * @param openid 申请文档翻译的用户 openId
     * @return 文档翻译申请是否成功
     * @throws IOException IOException
     * @throws MimeTypeException MimeTypeException
     */
    boolean applyDocumentTranslation(MultipartFile file, String filename, String from, String to, String openid) throws IOException, MimeTypeException;

    /**
     * 判断给定的 Openid 是否为一个有效的用户
     * @param openid 用户 openId
     * @return 是否为一个有效用户
     */
    boolean isUser(String openid);


    void documentCallBack(DocTranslationData docTranslationData) throws IOException;

    /**
     * 文档翻译结束后，发送模板消息给申请的微信用户，模板消息发送的结果由微信服务推送
     * @param docTranslationData 封装翻译结果的实体类对象
     * @param docTranslation 封装了微信模板消息发送的所需信息
     */
    void sendDocTransResultTemplateMessage(DocTranslationData docTranslationData, DocTranslation docTranslation);
}
