package com.jeffrey.wechat.service;

import com.jeffrey.wechat.entity.translation.ApplyDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
public interface ApplyDocumentService {

    /**
     * 判断传入的参数是否正确、有效
     * @param file 上传的文件
     * @param from 源语言
     * @param to 目标语言
     * @param openid 用户的 Openid
     * @return 传入的参数是否正确
     */
    boolean checkParams(MultipartFile file, String from, String to, String openid);

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
     * 发送模板消息
     * @param applyDocument 发送内容实体类
     *      toUser    发送模板消息给目标用户的 openid
     *      fTime     文档统计完成时间
     *      fileType  需翻译文档类型
     *      charCount 需翻译的文档总字符
     *      amount    翻译文档的所需金额
     *      first     标题
     *      remark    备注
     * @param toUser 发送模板消息给用户的 openid
     * @param fileType 文件类型
     */
    void sendTemplateMessage(ApplyDocument applyDocument, String toUser, String fileType);
}
