package service;

import com.jeffrey.wechat.WechatApplication;
import com.jeffrey.wechat.utils.GetAccessTokenUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@SpringBootTest(classes = WechatApplication.class)
public class AccessTokenTest {
    @Test
    void token() {
        for (int i = 0; i < 20; i++) {

            System.out.println(GetAccessTokenUtil.getToken());

            if (i % 2 == 0) {
                System.out.println("测试缓存过期");
                GetAccessTokenUtil.expiresTime = GetAccessTokenUtil.expiresTime - (130 * 60) * 1000;
            }
        }
    }
}


