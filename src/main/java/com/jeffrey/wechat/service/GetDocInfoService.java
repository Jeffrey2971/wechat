package com.jeffrey.wechat.service;

import com.jeffrey.wechat.entity.TransResponseWrapper;
import org.springframework.ui.Model;

import java.util.HashMap;

public interface GetDocInfoService {

    String prepareData(Integer id, Long wrapper, Model model, HashMap<Long, TransResponseWrapper> userDataItem);

}
