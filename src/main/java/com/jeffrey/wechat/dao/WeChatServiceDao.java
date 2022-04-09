package com.jeffrey.wechat.dao;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeChatServiceDao {

    /**
     * 查看是否为一个用户
     * @param openid userid
     * @return 是否为一个用户
     */
    @Select("SELECT COUNT(*) FROM `basic_user_info` WHERE openid = #{openid}")
    int isUser(String openid);

    @Select("SELECT `openid` from `basic_user_info`")
    List<String> selectUserOpenIdList();

}
