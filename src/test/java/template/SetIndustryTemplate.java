package template;

import com.google.gson.Gson;
import com.jeffrey.wechat.WechatApplication;
import com.jeffrey.wechat.entity.template.SetIndustry;
import com.jeffrey.wechat.utils.GetAccessTokenUtil;
import com.jeffrey.wechat.utils.RequestUtil;
import com.sun.net.httpserver.HttpsParameters;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@SpringBootTest(classes = WechatApplication.class)
public class SetIndustryTemplate {
    private static final String URL = "https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token=ACCESS_TOKEN";
    @Test
    void setIndustry(){

        SetIndustry setIndustry = new SetIndustry();
        setIndustry.setIndustry_id1(1);
        setIndustry.setIndustry_id2(2);
        System.out.println(setIndustry);

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.postForObject(URL.replace("ACCESS_TOKEN", GetAccessTokenUtil.getToken()), new Gson().toJson(setIndustry), String.class);
        System.out.println(result);
    }
}
