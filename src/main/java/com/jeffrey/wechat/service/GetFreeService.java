package com.jeffrey.wechat.service;

import com.jeffrey.wechat.entity.mapper.UserUseTotalEntity;
import com.jeffrey.wechat.entity.mapper.ShareTableEntity;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;

@Component
public interface GetFreeService {

    /**
     * 判断是否为一个用户
     *
     * @param openid 用户 openid
     * @return true / false
     */
    boolean isUser(String openid);

    /**
     * 获取用户分享数
     *
     * @param openid 用户 openid
     * @return 用户分享数
     */
    Integer getUserShareTotal(String openid);

    /**
     * 获取分享链接，如果返回 null 则生成并返回
     *
     * @param openid 用户 openid
     * @return 分享链接（存在于微信素材库，服务器只保存链接）
     */
    String getUserShareLink(String openid) throws IOException;

    /**
     * 根据给定的 openid 获取可使用次数
     *
     * @param openid 用户 openid
     * @return 当前用户的分享数
     */
    long getUserTotal(String openid) ;

    /**
     * 根据给定的实体类中的 openid 属性更新用户
     *
     * @param userUseTotalEntity 对应 user_use_total 表中的一条数据
     */
    void updateUserTotal(UserUseTotalEntity userUseTotalEntity);

    /**
     * 更新用户推广数，根据给定的 openid 更新 share 表中的 share_total 字段为给定的 newShareTotal 值
     *
     * @param newShareTotal 覆盖 share 表中 share_total 字段的值
     * @param openid 用户 openid
     */
    void updateShareTotal(Integer newShareTotal, String openid);

    /**
     * 在 user_use_total 表中初始化一条数据
     * @param userUseTotalEntity 往数据库中插入的数据
     */
    void initialUserTotal(UserUseTotalEntity userUseTotalEntity);

    /**
     * 判断是否为免次数使用用户
     *
     * @param openid 用户 openid
     * @return 是否为免次数使用用户
     */
    boolean continueUser(String openid);

    /**
     * 根据给定的 openid 查询对应的信息
     *
     * @param openid 用户 openid
     * @return 相关信息的实体类对象
     */
    UserUseTotalEntity getUserUseTotalTableEntityByOpenId(String openid);

    /**
     * 根据给定的 openid 查询 share 表中对应的一条数据
     * @param openid 用户 openid
     * @return 包含了给定 openid 的相关信息实体类
     */
    ShareTableEntity getShareTableEntityByOpenId(String openid);

    /**
     * 根据给定的 openid 查询是否不在 user_use_total 表中
     * @param openid 用户 openid
     * @return openid 是否不在 user_use_total 表中
     */
    boolean notExistsUserUseTotal(String openid);

    /**
     * 获取用户临时使用次数
     *
     * @param openid 用户 openid
     * @return 是否获取成功
     */
    boolean getTempUseChance(String openid);

    /**
     * 刷新每日使用次数
     *
     * @param canUse 次数
     */
    void resetCanUseColumn(Integer canUse);

    /**
     * 查询 user_use_total 表中的所有的 openid
     *
     * @return 包含了 user_use_total 表中 openid 所有值的集合
     */
    List<String> selectUserUseTotalUserOpenIdList();

    /**
     * 查询 share 表中的所有的 openid
     *
     * @return 包含了 share 表中 openid 所有值的集合
     */
    List<String> selectShareUserOpenIdList();

    /**
     * 移除给定包含了 openid 集合中的数据
     * @param lists 包含了需要被移除 openid 的集合
     * @return 移除条目数
     */
    int deleteListUser(List<String> lists);
}
