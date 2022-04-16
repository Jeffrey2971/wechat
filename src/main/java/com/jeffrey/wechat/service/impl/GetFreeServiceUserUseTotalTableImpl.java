package com.jeffrey.wechat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeffrey.wechat.dao.GetFreeServiceDaoUserUseTotalTable;
import com.jeffrey.wechat.entity.mybatis.UserUseTotalEntity;
import com.jeffrey.wechat.service.GetFreeServiceUserUseTotalTable;
import org.springframework.stereotype.Service;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
public class GetFreeServiceUserUseTotalTableImpl extends ServiceImpl<GetFreeServiceDaoUserUseTotalTable, UserUseTotalEntity> implements GetFreeServiceUserUseTotalTable {}
