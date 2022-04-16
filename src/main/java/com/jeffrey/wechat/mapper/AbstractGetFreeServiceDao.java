package com.jeffrey.wechat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * 由于一开始使用 myBatis 而不是用 myBatisPlus，接口继承 BaseMapper 范型只能操作一张表对应的一个实体类对象
 * 所以当前接口重新定义为抽象接口，让操作 user_use_total / basic_user_info / share 三个接口中的方法都定义到当前接口中，
 * 并让三个不同的接口实现当前接口并重写当前方法
 */
@Repository
public abstract interface AbstractGetFreeServiceDao<T> extends BaseMapper<T> {}
