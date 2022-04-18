package com.jeffrey.wechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import com.jeffrey.wechat.entity.mapper.UserUseTotalEntity;
import com.jeffrey.wechat.mapper.ProcessEventMessageDao;
import com.jeffrey.wechat.entity.message.BaseMessage;
import com.jeffrey.wechat.entity.message.EmptyMessage;
import com.jeffrey.wechat.entity.message.TextMessage;
import com.jeffrey.wechat.entity.mapper.ShareTableEntity;
import com.jeffrey.wechat.entity.mapper.UserInfo;
import com.jeffrey.wechat.service.GetFreeService;
import com.jeffrey.wechat.service.ProcessEventMessage;
import com.jeffrey.wechat.utils.GetAccessTokenUtil;
import com.jeffrey.wechat.utils.RequestUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
@Slf4j
public class ProcessEventMessageImpl implements ProcessEventMessage {

    private final ProcessEventMessageDao processEventMessageDao;

    private final WeChatAutoConfiguration.ServerInfo serverInfo;

    private final WeChatAutoConfiguration.WxConfig wxConfig;

    private final GetFreeService getFreeService;

    @Autowired
    public ProcessEventMessageImpl(ProcessEventMessageDao processEventMessageDao, WeChatAutoConfiguration.ServerInfo serverInfo, WeChatAutoConfiguration.WxConfig wxConfig, GetFreeService getFreeService) {
        this.processEventMessageDao = processEventMessageDao;
        this.serverInfo = serverInfo;
        this.wxConfig = wxConfig;
        this.getFreeService = getFreeService;
    }

    @SneakyThrows
    @Override
    public BaseMessage processSubscribe(Map<String, String> requestMap) {

        String oid = requestMap.get("FromUserName");
        HashMap<String, Object> httpParams = new HashMap<>();

        httpParams.put("token", GetAccessTokenUtil.getToken());
        httpParams.put("openid", oid);
        httpParams.put("lang", "zh_CN");

        ResponseEntity<UserInfo> entity = RequestUtil.getEntity(wxConfig.getWxGetUserInfoUrl(), UserInfo.class, httpParams);

        if (entity.getStatusCodeValue() == 200 && entity.getBody() != null) {
            UserInfo userInfo = entity.getBody();
            // 反射检查每一个字段信息
            for (Field field : userInfo.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(userInfo);
                // 如果字段类型为整形且对应的结果为 0 则重新设置为 -1
                if (value instanceof Integer && (int) value == 0) {
                    field.set(userInfo, -1);
                    // 如果字段类型为集合且对应的结果长度小于或等于 0 则重新设置为 -1，否则将原数据转换为 String 类型重新封装到对象中
                } else if (value instanceof List) {
                    if (((List<?>) value).size() <= 0) {
                        field.set(userInfo, "-1");
                    } else {
                        field.set(userInfo, userInfo.getTagid_list().toString());
                    }

                    // 如果字段类型为 String 且它没有任何有效的字符则重新设置为 null 后封装到对象中
                } else if (value instanceof String && !StringUtils.hasText(String.valueOf(value))) {
                    field.set(userInfo, null);
                }

                if ("subscribe_time".equalsIgnoreCase(field.getName())) {
                    field.set(userInfo, DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(LocalDateTime.now()));
                }
            }

//            boolean saveSuccess = processEventMessageDao.insert(userInfo) > 0;
            boolean saveSuccess = processEventMessageDao.insert(userInfo) > 0;


            if (getFreeService.notExistsUserUseTotal(oid)) {
                getFreeService.initialUserTotal(new UserUseTotalEntity(null, oid, wxConfig.getWxDayCanUse(), 'F', 0, 'T'));
            }

            log.info("savaStatus:{}", saveSuccess);
            if (saveSuccess) {
                return new TextMessage(requestMap, getFollowedResponseMessage(userInfo.getOpenid()));
            }
        }


        return new EmptyMessage();
    }

    @Override
    public BaseMessage processUnsubscribe(Map<String, String> requestMap) {

        String openid = requestMap.get("FromUserName");

        ShareTableEntity shareTableEntity = getFreeService.getShareTableEntityByOpenId(openid);

        if (processEventMessageDao.selectCount(new QueryWrapper<UserInfo>().eq("openid", openid)) > 0) {
            if (processEventMessageDao.delete(new QueryWrapper<UserInfo>().eq("openid", openid)) > 0) {
                log.info("移除用户：{}", openid);
            }
        }

        if (shareTableEntity != null && StringUtils.hasText(shareTableEntity.getMediaId())) {
            HashMap<String, Object> httpParams = new HashMap<>();
            httpParams.put("token", GetAccessTokenUtil.getToken());
            String resultMessage = RequestUtil.postObject(wxConfig.getWxDeleteMediaDataUrl(), "{\"media_id\": \"" + shareTableEntity.getMediaId() + "\"}", String.class, httpParams);
            log.info("移除用户相关素材：{}", resultMessage);
        }

        return new EmptyMessage();
    }

    @Override
    public BaseMessage processClick(Map<String, String> requestMap) {
        return new EmptyMessage();
    }

    @Override
    public BaseMessage processView(Map<String, String> requestMap) {
        return new EmptyMessage();
    }

    @Override
    public BaseMessage processPicPhotoOrAlbum(Map<String, String> requestMap) {
        log.info("通过菜单栏拍照或相册选择照片上传，服务器依然会收到 image 事件，所以当前事件返回空消息类型");
        return new EmptyMessage();
    }

    @Override
    public BaseMessage processScancodePush(Map<String, String> requestMap) {
        return new EmptyMessage();
    }

    @Override
    public BaseMessage processScancodeWaitMsg(Map<String, String> requestMap) {
        return new EmptyMessage();
    }

    @Override
    public BaseMessage processPicSysPhoto(Map<String, String> requestMap) {
        return new EmptyMessage();
    }

    @Override
    public BaseMessage processPicWeiXin(Map<String, String> requestMap) {
        return new EmptyMessage();
    }

    @Override
    public BaseMessage processLocationSelect(Map<String, String> requestMap) {
        return new EmptyMessage();
    }

    @Override
    public BaseMessage processViewMiniProgram(Map<String, String> requestMap) {
        return new EmptyMessage();
    }

    /*  private method  */

    private String getFollowedResponseMessage(String openid) {
        final String baseUrl = "<a href=\"URL\">TITLE</a>";
        return "欢迎关注本公众号，点击以下蓝色链接查看使用方式，发送一张照片试试吧！\n\n\n" +
                baseUrl.replace("URL", "https://mp.weixin.qq.com/s/3ypEPH04Q6CN7SS7CwGFXQ").replace("TITLE", "1、点击查看使用方式") + "\n\n\n" +
                baseUrl.replace("URL", serverInfo.getDomain() + "/question?openid=" + openid).replace("TITLE", "2、点击反馈问题或提出意见") + "\n\n\n" +
                baseUrl.replace("URL", "https://mp.weixin.qq.com/s/QGIVFfANp0gvXcwwgX15aA").replace("TITLE", "3、点击查看最新事件");
    }
}
