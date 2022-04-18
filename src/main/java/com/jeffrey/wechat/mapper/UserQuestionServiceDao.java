package com.jeffrey.wechat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeffrey.wechat.entity.mapper.FeedBack;
import org.springframework.stereotype.Repository;

@Repository
public interface UserQuestionServiceDao extends BaseMapper<FeedBack> {}
