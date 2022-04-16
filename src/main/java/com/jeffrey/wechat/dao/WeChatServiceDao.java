package com.jeffrey.wechat.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeffrey.wechat.entity.mybatis.UserInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface WeChatServiceDao extends BaseMapper<UserInfo> {}
