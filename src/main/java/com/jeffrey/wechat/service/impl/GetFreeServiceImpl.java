package com.jeffrey.wechat.service.impl;

import com.jeffrey.wechat.dao.GetFreeServiceDao;
import com.jeffrey.wechat.entity.UploadMediaResult;
import com.jeffrey.wechat.entity.mybatis.ShareTableEntity;
import com.jeffrey.wechat.entity.mybatis.UserUseTotalEntity;
import com.jeffrey.wechat.service.GetFreeService;
import com.jeffrey.wechat.utils.FileDownloadInputStreamUtil;
import com.jeffrey.wechat.utils.FileUploadMediaDataUtil;
import com.jeffrey.wechat.utils.GetQrCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
public class GetFreeServiceImpl implements GetFreeService {

    private final GetFreeServiceDao getFreeServiceDao;

    @Autowired
    public GetFreeServiceImpl(GetFreeServiceDao getFreeServiceDao) {
        this.getFreeServiceDao = getFreeServiceDao;
    }

    @Override
    public Integer getUserShareTotal(String openid) {
        Integer shareTotal = getFreeServiceDao.getShareTotal(openid);
        return shareTotal == null ? 0 : shareTotal;
    }

    @Override
    public String getUserShareLink(String openid) throws IOException {
        String userShareLink = getFreeServiceDao.getUserShareLink(openid);

        if (userShareLink != null) {
            // 如果已存在分享的链接则直接返回
            return userShareLink;
        } else {
            // 如不存在分享的链接则先获取保存了场景字符串的微信二维码 URL ，URL 通过草料网进行渲染后返回已渲染的 URL 地址，根据这个地址下载该二维码的输入流并上传到微信永久素材库中返回 URL
            String qrCodeUrl = GetQrCodeUtil.getQrCode("QR_LIMIT_STR_SCENE", openid, null, null).getBody().getUrl();
            String addStyleQrCodeUrl = GetQrCodeUtil.getStyleShareOrCode(qrCodeUrl);
            InputStream is = FileDownloadInputStreamUtil.download(addStyleQrCodeUrl);
            UploadMediaResult uploadMediaResult = FileUploadMediaDataUtil.fileUpload(is, openid + "png", "png");
            getFreeServiceDao.cacheShareLink(openid, uploadMediaResult.getUrl(), 0, uploadMediaResult.getMedia_id());
            return uploadMediaResult.getUrl();
        }
    }

    @Override
    public int getUserTotal(String openid) {
        Integer useTotal = getFreeServiceDao.getUseTotal(openid);
        return useTotal == null ? 0 : useTotal;
    }

    @Override
    public void updateUserTotal(Integer canUse, char freeUser, Integer allUse, char free, String openid) {
        try {
            getFreeServiceDao.updateUserTotal(canUse, freeUser, allUse, free, openid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateShareTotal(Integer newShareTotal, String openid) {
        getFreeServiceDao.updateShareTotal(newShareTotal, openid);
    }

    @Override
    public void initialUserTotal(String openid, Integer canUse, char freeUser, Integer allUse, char free) {
        getFreeServiceDao.initialUserTotal(openid, canUse, freeUser, allUse, free);
    }

    @Override
    public boolean continueUser(String openid) {
        String continueUser = getFreeServiceDao.continueUser(openid);
        if (!StringUtils.hasText(continueUser)) {
            return false;
        }

        return continueUser.equalsIgnoreCase("T");
    }

    @Override
    public UserUseTotalEntity getUserUseTotalTableEntityByOpenId(String openid) {
        return getFreeServiceDao.getUserUseTotalTableEntityByOpenId(openid);
    }

    @Override
    public ShareTableEntity getShareTableEntityByOpenId(String openid) {

        return getFreeServiceDao.getShareTableEntityByOpenId(openid);
    }

    @Override
    public boolean existsUserUseTotal(String openid) {
        return getFreeServiceDao.existsUserUseTotal(openid) > 0;
    }

    @Override
    public boolean getTempUseChance(String openid) {

        UserUseTotalEntity entityByOpenId = getFreeServiceDao.getUserUseTotalTableEntityByOpenId(openid);
        if ("T".equalsIgnoreCase(String.valueOf(entityByOpenId.getFree()))) {
            getFreeServiceDao.getTempUseChance(openid);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void resetCanUseColumn(Integer canUse) {
        getFreeServiceDao.resetCanUseColumn(canUse);
    }

    @Override
    public List<String> selectUserUseTotalUserOpenIdList() {

        return getFreeServiceDao.selectUserUseTotalUserOpenIdList();
    }

    @Override
    public List<String> selectShareUserOpenIdList() {
        return getFreeServiceDao.selectShareUserOpenIdList();
    }

    @Override
    public void deleteListUser(String tName, List<String> lists) {
        getFreeServiceDao.deleteListUser(tName, lists);
    }
}
