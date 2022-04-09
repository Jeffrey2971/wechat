package com.jeffrey.wechat.entity.user;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量设置用户 openId 获取用户基本信息（UUION ID 机制），userList 数组一次性仅能添加 100 个 UserItem 对象
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Getter
public class SetBatchUserOpenId {

    @SerializedName("user_list")
    private final List<UserItem> userList = new ArrayList<>();

    @Data

    public static class UserItem{
        public UserItem(String openid) {
            this.openid = openid;
        }

        private String openid;
        private String lang = "zh_CN";
    }
}
