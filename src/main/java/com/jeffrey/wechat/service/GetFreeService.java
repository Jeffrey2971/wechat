package com.jeffrey.wechat.service;

import com.jeffrey.wechat.entity.mybatis.ShareTableEntity;
import com.jeffrey.wechat.entity.mybatis.UserUseTotalEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public interface GetFreeService {
    /**
     * 获取用户分享数
     *
     * @param openid 用户唯一 openid
     * @return int
     *//*
    Integer getUserShareTotal(String openid);

    *//**
     * 获取分享链接，如果返回 null 则生成并返回
     *
     * @param openid 用户唯一 openid
     * @return 分享链接（存在于微信素材库，服务器只保存链接）
     *//*
    String getUserShareLink(String openid) throws IOException;

    *//**
     * 获取可使用次数
     *
     * @param openid
     * @return
     *//*
    int getUserTotal(String openid) ;

    void updateUserTotal(Integer canUse, char freeUser, Integer allUse, char free, String openid);

    void updateShareTotal(Integer newShareTotal, String openid);

    void initialUserTotal(String openid, Integer canUse, char freeUser, Integer allUse, char free);

    boolean continueUser(String openid);

    UserUseTotalEntity getUserUseTotalTableEntityByOpenId(String openid);

    ShareTableEntity getShareTableEntityByOpenId(String openid);

    boolean existsUserUseTotal(String openid);

    boolean getTempUseChance(String openid);

    void resetCanUseColumn(Integer canUse);

    List<String> selectUserUseTotalUserOpenIdList();

    List<String> selectShareUserOpenIdList();

    void deleteListUser(String tName, List<String> lists);*/
}
