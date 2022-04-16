package management;

import com.google.gson.Gson;
import com.jeffrey.wechat.WechatApplication;
import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import com.jeffrey.wechat.mapper.ProcessEventMessageDao;
import com.jeffrey.wechat.entity.user.GetBatchUserOpenIdList;
import com.jeffrey.wechat.entity.user.SetBatchUserOpenId;
import com.jeffrey.wechat.entity.user.UserOpenIdList;
import com.jeffrey.wechat.entity.mybatis.UserInfo;
import com.jeffrey.wechat.utils.GetAccessTokenUtil;
import com.jeffrey.wechat.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 根据用户 openId 获取用户信息或批量获取用户信息
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@SpringBootTest(classes = WechatApplication.class)
@Slf4j
public class GetUserOpenIdList {

    @Autowired
    private WeChatAutoConfiguration.WxConfig wxConfig;

    @Autowired
    private ProcessEventMessageDao processEventMessageDao;

    private static final List<GetBatchUserOpenIdList> userInfoList = new ArrayList<>();


    // 一次最多获取 100 位
    @Test
    void getBatchOpenIdList() {


        HashMap<String, Object> params = new HashMap<>();
        params.put("token", GetAccessTokenUtil.getToken());
        params.put("next_openid", "");

        // 所有用户 openid
        List<String> openidList = RequestUtil.getObject(wxConfig.getWxGetUserOpenIdList(), UserOpenIdList.class, params).getData().getOpenid();
        SetBatchUserOpenId setBatchUserOpenId = new SetBatchUserOpenId();

        if (openidList.size() > 0) {

            int addSize = 0;
            Gson gson = new Gson();

            for (String openid : openidList) {
                ++addSize;
                if (setBatchUserOpenId.getUserList().size() == 100) {
                    request(gson.toJson(setBatchUserOpenId));
                    setBatchUserOpenId.getUserList().clear();
                } else {
                    setBatchUserOpenId.getUserList().add(new SetBatchUserOpenId.UserItem(openid));

                    if (addSize == openidList.size()) {
                        // 封装完成
                        request(gson.toJson(setBatchUserOpenId));
                        System.out.println("添加：" + addSize);
                        break;
                    }
                }

            }
        }
        if (userInfoList.size() > 0) {
            for (GetBatchUserOpenIdList batchUserOpenIdList : userInfoList) {
                for (UserInfo userInfo : batchUserOpenIdList.getUserInfoList()) {
                    System.out.println(userInfo);
                }
            }
        }
    }


    @Test
    void getUserOpenIdList() throws IllegalAccessException {

        HashMap<String, Object> params = new HashMap<>();
        params.put("token", GetAccessTokenUtil.getToken());
        params.put("next_openid", "");

        UserOpenIdList openIdList = RequestUtil.getObject(wxConfig.getWxGetUserOpenIdList(), UserOpenIdList.class, params);

        HashMap<String, Object> httpParams = new HashMap<>();
        httpParams.put("token", GetAccessTokenUtil.getToken());
        httpParams.put("lang", "zh_CN");

        for (String openid : openIdList.getData().getOpenid()) {
            httpParams.put("openid", openid);
            ResponseEntity<UserInfo> entity = RequestUtil.getEntity(wxConfig.getWxGetUserInfoUrl(), UserInfo.class, httpParams);
            if (entity.getStatusCodeValue() == 200 && entity.getBody() != null) {
                UserInfo userInfo = convertType(entity.getBody());
                log.info("savaStatus:{}", processEventMessageDao.insertUserInfo(userInfo));
            }

        }
    }

    private UserInfo convertType(UserInfo userInfo) throws IllegalAccessException {
        for (Field field : userInfo.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(userInfo);
            if (value instanceof Integer && (int) value == 0) {
                field.set(userInfo, -1);
            } else if (value instanceof List) {
                if (((List<?>) value).size() <= 0) {
                    field.set(userInfo, "-1");
                } else {
                    field.set(userInfo, userInfo.getTagid_list().toString());
                }

                // 如果字段类型为 String 且它没有任何有效的字符则重新设置为 null 后封装到对象中
            } else if (value instanceof String && !StringUtils.hasText(String.valueOf(value))) {
                field.set(userInfo, null);
            }
        }
        return userInfo;
    }


    private void request(String requestBody) {

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, null);
        HashMap<String, Object> uriParams = new HashMap<>();
        uriParams.put("token", GetAccessTokenUtil.getToken());

        ResponseEntity<String> responseEntity = RequestUtil.postEntity(wxConfig.getWxGetBatchUserOpenIdList(), httpEntity, String.class, uriParams);

        if (responseEntity.getStatusCodeValue() == 200 && responseEntity.getBody() != null) {
            userInfoList.add(new Gson().fromJson(responseEntity.getBody(), GetBatchUserOpenIdList.class));
            return;
        }

        throw new RuntimeException("导入发生异常");
    }
}
