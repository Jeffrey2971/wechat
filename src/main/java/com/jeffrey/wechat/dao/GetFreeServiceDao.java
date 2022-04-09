package com.jeffrey.wechat.dao;

import com.jeffrey.wechat.entity.mybatis.ShareTableEntity;
import com.jeffrey.wechat.entity.mybatis.UserUseTotalEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GetFreeServiceDao {

    /**
     * 获取已存储的分享链接，如果返回为 null 说明没有缓存过
     *
     * @param openid 用户唯一 openid
     * @return 已缓存的分享链接地址
     */
    @Select("SELECT `shareLink` FROM `share` WHERE openid = #{openid}")
    String getUserShareLink(String openid);

    /**
     * 缓存生成的二维码分享链接
     *
     * @param openid     用户唯一 openid
     * @param shareLink  分享的二维码链接（位于微信素材库内）
     * @param shareTotal 当前链接的分享数，默认为 0
     * @param mediaId    素材 mediaId
     * @return 是否缓存成功
     */
    @Insert("INSERT INTO `share` (openid, shareLink, shareTotal, media_id) VALUE (#{param1}, #{param2}, #{param3}, #{param4})")
    boolean cacheShareLink(String openid, String shareLink, Integer shareTotal, String mediaId);

    /**
     * 获取用户已分享的次数
     *
     * @param openid 用户唯一 openid
     * @return 分享数
     */
    @Select("SELECT `shareTotal` FROM `share` WHERE openid = #{openid}")
    Integer getShareTotal(String openid);

    @Select("SELECT `can_use` FROM `user_use_total` WHERE `openid` = #{openid}")
    Integer getUseTotal(String openid);

    @Update("UPDATE `user_use_total` SET `can_use` = #{param1}, `free_user` = #{param2}, `all_use` = #{param3}, `free` = #{param4} WHERE `openid` = #{param5}")
    void updateUserTotal(Integer canUse, char freeUser, Integer allUse, char free, String openid);

    @Update("UPDATE `share` SET `shareTotal` = #{param1} WHERE `openid` = #{param2}")
    void updateShareTotal(Integer newShareTotal, String openid);

    @Insert("INSERT INTO `user_use_total` (openid, can_use, free_user, all_use, free) VALUE (#{param1}, #{param2}, #{param3}, #{param4}, #{param5})")
    void initialUserTotal(String openid, Integer canUse, char freeUser, Integer allUse, char free);

    @Select("SELECT `free_user` FROM `user_use_total` WHERE `openid` = #{openid}")
    String continueUser(String openid);

    @Select("SELECT id, openid, can_use as canUse, free_user as freeUser, all_use as allUse, free FROM `user_use_total` WHERE `openid` = #{openid}")
    UserUseTotalEntity getUserUseTotalTableEntityByOpenId(String openid);

    @Select("SELECT * FROM `share` WHERE `openid` = #{openid}")
    ShareTableEntity getShareTableEntityByOpenId(String openid);

    @Select("SELECT COUNT(`openid`) FROM `basic_user_info` WHERE `openid` = #{openid}")
    int isUser(String openid);

    @Select("SELECT COUNT(`openid`) FROM `user_use_total` WHERE `openid` = #{openid}")
    int existsUserUseTotal(String openid);

    @Update("UPDATE `user_use_total` SET `free` = 'F', `can_use` = 5 WHERE `openid` = #{openid}")
    void getTempUseChance(String openid);

    @Update("UPDATE `user_use_total` SET `can_use` = #{canUse}")
    void resetCanUseColumn(Integer canUse);

    @Select("SELECT `openid` FROM `user_use_total`")
    List<String> selectUserUseTotalUserOpenIdList();

    @Select("SELECT `openid` FROM `share`")
    List<String> selectShareUserOpenIdList();

    // 根据 openid 查询用户是否存在
    @Select("SELECT COUNT(*) FROM `basic_user_info` WHERE `openid` = #{openid}")
    int isUserExist(String openid);

    void deleteListUser(String tName, List<String> lists);

}
