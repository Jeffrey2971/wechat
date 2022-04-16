package com.jeffrey.wechat.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeffrey.wechat.entity.FeedBack;
import org.springframework.stereotype.Repository;

@Repository
public interface UserQuestionServiceDao extends BaseMapper<FeedBack> {

    /**
     * 查询给定的 openid 是否在 user_feedback 表中
     * @param queryWrapper 封装的查询条件
     * @return 查询条目数，为 0 则表示不存在数据库中
     */
    @Override
    Long selectCount(Wrapper<FeedBack> queryWrapper);

    /**
     * 存储反馈表单到数据库中
     * @param feedBack 反馈表单项
     * @return 影响行数，为 0 则表示插入失败
     */
    @Override
    int insert(FeedBack feedBack);
}
