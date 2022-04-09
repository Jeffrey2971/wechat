package com.jeffrey.wechat.entity.user;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class UserOpenIdList {
    private Integer total;
    private Integer count;
    private Data data;
    @SerializedName("next_openid")
    private String nextOpenId;

    @lombok.Data
    public static class Data{
        private List<String> openid;
    }
}
