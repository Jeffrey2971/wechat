package com.jeffrey.wechat.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeffrey.wechat.entity.mybatis.UserInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeChatServiceDao extends BaseMapper<UserInfo> {

    /**
     * 查询存在于 basic_user_info 表中的 openid 用户数量
     * @param queryWrapper 封装的查询条件
     * @return 查询行数，为 0 则表示用户不存在于表中
     */
    @Override
    Long selectCount(Wrapper<UserInfo> queryWrapper);

    /**
     * 查询所有存在于 basic_user_info 表中的 openid
     * @param queryWrapper 封装的查询条件
     * @return UserInfo 对象，只有 openid 字段不为空
     */
    @Override
    List<UserInfo> selectList(Wrapper<UserInfo> queryWrapper);
}
