package com.jeffrey.wechat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeffrey.wechat.dao.GetFreeServiceDaoShareTable;
import com.jeffrey.wechat.entity.mybatis.ShareTableEntity;
import com.jeffrey.wechat.service.GetFreeServiceShareTable;
import org.springframework.stereotype.Service;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
public class GetFreeServiceShareTableImpl extends ServiceImpl<GetFreeServiceDaoShareTable, ShareTableEntity> implements GetFreeServiceShareTable {}
