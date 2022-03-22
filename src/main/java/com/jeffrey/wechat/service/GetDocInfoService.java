package com.jeffrey.wechat.service;

import com.jeffrey.wechat.entity.TransResponseWrapper;
import org.springframework.ui.Model;

import java.util.HashMap;

public interface GetDocInfoService {
    /**
     * 查看是否为一个用户
     * @param openid userid
     * @return 是否为一个用户
     */
    boolean isUser(String openid);

    String prepareData(Integer id, Long wrapper, Model model, HashMap<Long, TransResponseWrapper> userDataItem);

}
