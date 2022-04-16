package com.jeffrey.wechat.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeffrey.wechat.entity.FeedBack;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface UserQuestionServiceDao extends BaseMapper<FeedBack> {}
