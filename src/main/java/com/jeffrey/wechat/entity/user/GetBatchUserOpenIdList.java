package com.jeffrey.wechat.entity.user;

import com.google.gson.annotations.SerializedName;
import com.jeffrey.wechat.entity.mapper.UserInfo;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Data
public class GetBatchUserOpenIdList {

    @SerializedName("user_info_list")
    private List<UserInfo> userInfoList;
}
