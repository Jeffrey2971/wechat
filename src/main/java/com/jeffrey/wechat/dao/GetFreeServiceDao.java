package com.jeffrey.wechat.dao;

public interface GetFreeServiceDao {
    /**
     * 查看是否为一个内测用户
     * @param openid userid
     * @return boolean
     */
    boolean isBetaUser(String openid);

    /**
     * 查看是否已有推广图
     * @return boolean
     */
    boolean hasShareImage(String openid);

    /**
     * 根据 openid 获取对应的推广图片
     * @param openid userid
     * @return imageUrl
     */
    String getImageUrl(String openid);


}
