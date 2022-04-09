package com.jeffrey.wechat.utils;

import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import com.jeffrey.wechat.entity.QrCodeResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;

/**
 * 获取微信二维码相关信息工具类
 * PS：哪个傻逼设计的接口
 */
@Slf4j
@Component
public class GetQrCodeUtil {
    private static WeChatAutoConfiguration.CliConfig cliConfig;

    private static WeChatAutoConfiguration.WxConfig wxConfig;

    @Autowired
    private void setGson(WeChatAutoConfiguration.CliConfig cliConfig, WeChatAutoConfiguration.WxConfig wxConfig) {
        GetQrCodeUtil.cliConfig = cliConfig;
        GetQrCodeUtil.wxConfig = wxConfig;
    }

    /**
     * 创建一个附带场景值的二维码并返回该二维码的相关信息
     * QR_SCENE为临时的整型参数值，
     * QR_STR_SCENE为临时的字符串参数值，
     * QR_LIMIT_SCENE为永久的整型参数值，
     * QR_LIMIT_STR_SCENE为永久的字符串参数值
     *
     * @param actionType    生成二维码的类型
     * @param sceneStr      附带的场景值字符串，该参数和 sceneId 其中一个必须为 null
     * @param sceneId       附带的场景值 ID，该参数和 sceneStr 其中一个必须为 null
     * @param expireSeconds 创建二维码的有效事超时时间a，只有生成二维码的类型为 QR_SCENE 或 QR_STR_SCENE 才可设置该值，其他类型应该置为 null
     * @return ResponseEntity<QrCodeResult> 获取二维码的相关信息
     */
    public static ResponseEntity<QrCodeResult> getQrCode(@NotNull String actionType, @Nullable String sceneStr, @Nullable Integer sceneId, @Nullable Integer expireSeconds) {


        checkParams(actionType, sceneStr, sceneId, expireSeconds);


        // 获取带场景值 ID 或 自定义字符串的永久二维码信息
        if ("QR_LIMIT_SCENE".equalsIgnoreCase(actionType) || "QR_LIMIT_STR_SCENE".equalsIgnoreCase(actionType)) {

            // 使用场景值 ID
            if ("QR_LIMIT_SCENE".equalsIgnoreCase(actionType) && sceneId != null && sceneId > 0 && sceneId < 100000) {
                return basicCode("{\"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": " + sceneId + "}}}");
                // 使用自定义字符串
            } else if ("QR_LIMIT_STR_SCENE".equalsIgnoreCase(actionType) && sceneStr != null && sceneStr.length() < 64 && sceneStr.length() > 1) {
                return basicCode("{\"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"" + sceneStr + "\"}}}");
            }

            // 获取带场景值 ID 或 自定义字符串的临时二维码信息
        } else if ("QR_SCENE".equalsIgnoreCase(actionType) || "QR_STR_SCENE".equalsIgnoreCase(actionType)) {
            // 使用场景值 ID
            if ("QR_SCENE".equalsIgnoreCase(actionType) && sceneId != null && sceneId > 0 && sceneId < 100000) {
                return basicCode("{\"expire_seconds\": " + expireSeconds + ", \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": " + sceneId + "}}}");
                // 使用自定义字符串
            } else if ("QR_STR_SCENE".equalsIgnoreCase(actionType) && sceneStr != null && sceneStr.length() < 64 && sceneStr.length() > 1) {
                return basicCode("{\"expire_seconds\": " + expireSeconds + ", \"action_name\": \"QR_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"" + sceneStr + "\"}}}");
            }
        }

        throw new RuntimeException("生成二维码时发生了异常");


    }

    public static String getStyleShareOrCode(String qrCodeUrl) {

        return addStyle(qrCodeUrl);

    }

    /**
     * 通过草料网
     *
     * @return
     */
    private static String addStyle(String qrCodeUrl){
        HashMap<String, Object> httpParams = new HashMap<>();
        httpParams.put("text", qrCodeUrl);
        httpParams.put("mhid", cliConfig.getCliStyleId());
        String content = RequestUtil.getObject(cliConfig.getGetQrCodeStyleUrl(), String.class, httpParams);
        if (StringUtils.hasText(content)) {
            String split = content.substring(content.indexOf("//qr.api.cli.im"));
            return "https:" + split.substring(0, split.indexOf("\""));
        }
        throw new RuntimeException("获取样式二维码失败");
    }


    /**
     * 检查参数
     *
     * @param actionType    获取二维码信息的类型
     * @param sceneStr      附带的场景字符串
     * @param sceneId       附带的场景 ID
     * @param expireSeconds 设置二维码过期时间，只有临时二维码类型才生效，否则应该置为 null
     */
    private static void checkParams(@NotNull String actionType, @Nullable String sceneStr, @Nullable Integer sceneId, @Nullable Integer expireSeconds) {

        if (!"QR_SCENE".equalsIgnoreCase(actionType) && !"QR_STR_SCENE".equalsIgnoreCase(actionType) && !"QR_LIMIT_SCENE".equalsIgnoreCase(actionType) && !"QR_LIMIT_STR_SCENE".equalsIgnoreCase(actionType)) {
            throw new RuntimeException("actionType 只能为 QR_SCENE / QR_STR_SCENE / QR_LIMIT_SCENE / QR_LIMIT_STR_SCENE");
        }

        if (sceneStr == null && sceneId == null) {
            throw new RuntimeException("参数 sceneStr 和 sceneId 只能一个为 null");
        }

        if (sceneStr != null && sceneId != null) {
            throw new RuntimeException("参数 sceneStr 和 sceneId 必须其中一个为 null");
        }

        if ("QR_SCENE".equalsIgnoreCase(actionType) || "QR_STR_SCENE".equalsIgnoreCase(actionType)) {
            if (expireSeconds == null || expireSeconds < 0 || expireSeconds > 2592000) {
                throw new RuntimeException("参数 actionType 为 QR_SCENE 或 QR_STR_SCENE 时，expireSeconds 不能为空且有效");
            }
        }

    }


    private static ResponseEntity<QrCodeResult> basicCode(String body){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HashMap<String, Object> httpParams = new HashMap<>();
        httpParams.put("token", GetAccessTokenUtil.getToken());

        HttpEntity<String> httpEntity = new HttpEntity<>(body, httpHeaders);

        ResponseEntity<QrCodeResult> entity = RequestUtil.postEntity(wxConfig.getWxGetQrCodeUrl(), httpEntity, QrCodeResult.class, httpParams);

        if (entity.getStatusCodeValue() == 200 && entity.getBody() != null) {
            return entity;
        }

        throw new RuntimeException("获取二维码失败");
    }
}


