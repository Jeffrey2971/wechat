package com.jeffrey.wechat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeffrey.wechat.entity.mapper.UserInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessEventMessageDao extends BaseMapper<UserInfo> {}
