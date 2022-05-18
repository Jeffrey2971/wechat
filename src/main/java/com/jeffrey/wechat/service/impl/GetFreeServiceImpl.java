package com.jeffrey.wechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jeffrey.wechat.entity.QrCodeResult;
import com.jeffrey.wechat.entity.mapper.UserInfo;
import com.jeffrey.wechat.mapper.GerFreeServiceForUserUseTotalTableDao;
import com.jeffrey.wechat.mapper.GetFreeServiceForShareTotalEntityTableDao;
import com.jeffrey.wechat.mapper.WeChatServiceDao;
import org.springframework.beans.factory.annotation.Autowired;
import com.jeffrey.wechat.utils.FileDownloadInputStreamUtil;
import com.jeffrey.wechat.entity.mapper.UserUseTotalEntity;
import com.jeffrey.wechat.entity.mapper.ShareTableEntity;
import com.jeffrey.wechat.utils.FileUploadMediaDataUtil;
import com.jeffrey.wechat.entity.UploadMediaResult;
import com.jeffrey.wechat.service.GetFreeService;
import org.springframework.stereotype.Component;
import com.jeffrey.wechat.utils.GetQrCodeUtil;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
public class GetFreeServiceImpl implements GetFreeService {

    private final GerFreeServiceForUserUseTotalTableDao gerFreeServiceForUserUseTotalTableDao;

    private final GetFreeServiceForShareTotalEntityTableDao getFreeServiceForShareTotalEntityTableDao;

    private final WeChatServiceDao weChatServiceDao;


    @Autowired
    public GetFreeServiceImpl(GerFreeServiceForUserUseTotalTableDao gerFreeServiceForUserUseTotalTableDao, GetFreeServiceForShareTotalEntityTableDao getFreeServiceForShareTotalEntityTableDao, WeChatServiceDao weChatServiceDao) {
        this.gerFreeServiceForUserUseTotalTableDao = gerFreeServiceForUserUseTotalTableDao;
        this.getFreeServiceForShareTotalEntityTableDao = getFreeServiceForShareTotalEntityTableDao;
        this.weChatServiceDao = weChatServiceDao;
    }

    @Override
    public boolean isUser(String openid) {
        return weChatServiceDao.selectCount(new QueryWrapper<UserInfo>().eq("openid", openid)) > 0;
    }

    @Override
    public Integer getUserShareTotal(String openid) {
        ShareTableEntity shareTableEntity = getFreeServiceForShareTotalEntityTableDao.selectOne(new QueryWrapper<ShareTableEntity>().eq("openid", openid).select("share_total"));
        return shareTableEntity == null || shareTableEntity.getShareTotal() == 0 ? 0 : shareTableEntity.getShareTotal();
    }

    @Override
    public String getUserShareLink(String openid) throws IOException {


        ShareTableEntity shareTableEntity = getFreeServiceForShareTotalEntityTableDao.selectOne(new QueryWrapper<ShareTableEntity>().eq("openid", openid).select("share_link"));

        if (shareTableEntity != null) {
            return shareTableEntity.getShareLink();
        } else {
            // 如不存在分享的链接则先获取保存了场景字符串的微信二维码 URL ，URL 通过草料网进行渲染后返回已渲染的 URL 地址，根据这个地址下载该二维码的输入流并上传到微信永久素材库中返回 URL
//            String qrCodeUrl = GetQrCodeUtil.getQrCode("QR_LIMIT_STR_SCENE", openid, null, null).getBody().getUrl();
            QrCodeResult qrCodeResult = GetQrCodeUtil.getQrCode("QR_LIMIT_STR_SCENE", openid, null, null).getBody();
            if (qrCodeResult != null && StringUtils.hasText(qrCodeResult.getUrl())) {
                String addStyleQrCodeUrl = GetQrCodeUtil.getStyleShareOrCode(qrCodeResult.getUrl());
                InputStream is = FileDownloadInputStreamUtil.download(addStyleQrCodeUrl);
                UploadMediaResult uploadMediaResult = FileUploadMediaDataUtil.fileUpload(is, openid + "png", "png");
                if (uploadMediaResult != null && StringUtils.hasText(uploadMediaResult.getMedia_id())) {
                    if (getFreeServiceForShareTotalEntityTableDao.insert(new ShareTableEntity(null, openid, uploadMediaResult.getUrl(), 0, uploadMediaResult.getMedia_id())) > 0) {
                        return uploadMediaResult.getUrl();
                    }
                }
            }

            throw new IOException("生成分享二维码失败");
        }
    }

    @Override
    public long getUserTotal(String openid) {
        Integer canUse = gerFreeServiceForUserUseTotalTableDao.selectOne(new QueryWrapper<UserUseTotalEntity>().eq("openid", openid).select("can_use")).getCanUse();
        return canUse == null || canUse == 0 ? 0 : canUse;
    }

    @Override
    public void updateUserTotal(UserUseTotalEntity userUseTotalEntity) {

        if (gerFreeServiceForUserUseTotalTableDao.update(new UserUseTotalEntity(null, userUseTotalEntity.getOpenid(), userUseTotalEntity.getCanUse(), userUseTotalEntity.getFreeUser(), userUseTotalEntity.getAllUse(), userUseTotalEntity.getFree()), new UpdateWrapper<UserUseTotalEntity>().eq("openid", userUseTotalEntity.getOpenid())) < 0) {
            throw new RuntimeException("更新用户失败");
        }
    }

    @Override
    public void updateShareTotal(Integer newShareTotal, String openid) {
        if (getFreeServiceForShareTotalEntityTableDao.update(null, new UpdateWrapper<ShareTableEntity>().eq("openid", openid).set("share_total", newShareTotal)) < 0) {
            throw new RuntimeException("更新用户失败");
        }
    }

    @Override
    public void initialUserTotal(UserUseTotalEntity userUseTotalEntity) {
        if (gerFreeServiceForUserUseTotalTableDao.insert(userUseTotalEntity) < 0) {
            throw new RuntimeException("初始化用户数据失败");
        }
    }

    @Override
    public boolean continueUser(String openid) {
        return "T".equalsIgnoreCase(String.valueOf(gerFreeServiceForUserUseTotalTableDao.selectOne(new QueryWrapper<UserUseTotalEntity>().eq("openid", openid).select("free_user")).getFreeUser()));

    }

    @Override
    public UserUseTotalEntity getUserUseTotalTableEntityByOpenId(String openid) {
        return gerFreeServiceForUserUseTotalTableDao.selectOne(new QueryWrapper<UserUseTotalEntity>().eq("openid", openid));
    }

    @Override
    public ShareTableEntity getShareTableEntityByOpenId(String openid) {
        return getFreeServiceForShareTotalEntityTableDao.selectOne(new QueryWrapper<ShareTableEntity>().eq("openid", openid));
    }

    @Override
    public boolean notExistsUserUseTotal(String openid) {
        return gerFreeServiceForUserUseTotalTableDao.selectCount(new QueryWrapper<UserUseTotalEntity>().eq("openid", openid)) <= 0;
    }

    @Override
    public boolean getTempUseChance(String openid) {

        return "T".equalsIgnoreCase(String.valueOf(
                gerFreeServiceForUserUseTotalTableDao.selectOne(new QueryWrapper<UserUseTotalEntity>()
                        .eq("openid", openid))
                        .getFree()
        ))
                && gerFreeServiceForUserUseTotalTableDao.
                update(null, new UpdateWrapper<UserUseTotalEntity>()
                        .eq("openid", openid)
                        .set("free", 'F')
                        .set("can_use", 5)) > 0;
    }

    @Override
    public void resetCanUseColumn(Integer canUse) {
        gerFreeServiceForUserUseTotalTableDao.update(null, new UpdateWrapper<UserUseTotalEntity>().set("can_use", canUse));
    }

    @Override
    public List<String> selectUserUseTotalUserOpenIdList() {
        ArrayList<String> openIdList = new ArrayList<>();
        gerFreeServiceForUserUseTotalTableDao.selectList(new QueryWrapper<UserUseTotalEntity>().select("openid")).forEach(item -> openIdList.add(item.getOpenid()));
        return openIdList;
    }

    @Override
    public List<String> selectShareUserOpenIdList() {

        ArrayList<String> openIdList = new ArrayList<>();

        getFreeServiceForShareTotalEntityTableDao.selectList(new QueryWrapper<ShareTableEntity>().select("openid")).forEach(item -> openIdList.add(item.getOpenid()));

        return openIdList;
    }

    @Override
    public int deleteListUser(List<String> lists) {
        return gerFreeServiceForUserUseTotalTableDao.delete(new QueryWrapper<UserUseTotalEntity>().in("openid", lists));
    }
}
