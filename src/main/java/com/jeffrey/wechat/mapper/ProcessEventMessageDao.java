package com.jeffrey.wechat.mapper;

import com.jeffrey.wechat.entity.mybatis.UserInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessEventMessageDao {

    boolean insertUserInfo(UserInfo userInfo);

    @Delete("DELETE FROM `basic_user_info` WHERE openid = #{openid}")
    boolean removeUser(String openid);

    @Select("SELECT COUNT(*) FROM `basic_user_info` WHERE openid = #{openid}")
    int isUser(String openid);

    @Select("SELECT COUNT(*) FROM `thanks`")
    int selectThankedUserTotal();

    @Insert("INSERT INTO `thanks` (openid) VALUE (#{openid});")
    void insertThanksUser(String openid);

    @Select("SELECT COUNT(*) FROM `thanks` WHERE openid = #{openid}")
    boolean thanked(String openid);

    @Select("SELECT * FROM `basic_user_info` WHERE id = #{id}")
    UserInfo selectUserInfoById(Integer id);

    @Select("SELECT COUNT(`qr_scene_str`) FROM `basic_user_info` WHERE `openid` = #{openid}")
    int getShareUserTotalByOpenId(String openid);
}
