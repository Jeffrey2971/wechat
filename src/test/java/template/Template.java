package template;

import com.google.gson.Gson;
import com.jeffrey.wechat.WechatApplication;
import com.jeffrey.wechat.entity.template.TemplateMessage;
import com.jeffrey.wechat.utils.GetAccessTokenUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@SpringBootTest(classes = WechatApplication.class)
public class Template {
    private static final String GET_INDUSTRY_URL = "https://api.weixin.qq.com/cgi-bin/template/get_industry?access_token=ACCESS_TOKEN";
    private static final String SEND_INDUSTRY_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
    private static final String TEMPLATE_ID = "ejb4pgFpvEs_BKTipdnSeiPWfB292wUF6lXL8rmcjN4";
    private static final RestTemplate restTemplate = new RestTemplate();

    @Test
    void getIndustry() {
        String access_token = restTemplate.getForObject(GET_INDUSTRY_URL.replace("ACCESS_TOKEN", GetAccessTokenUtil.getToken()), String.class);
        System.out.println(access_token);
    }

    @Test
    void sendTemplateMessage() {
        TemplateMessage templateMessage = new TemplateMessage(
                "ojJ8V6inNjQh3tvCoO-Kr6d7UcYQ",
                "",
                TEMPLATE_ID,
                new TemplateMessage.Data(
                        new TemplateMessage.First("您的文档翻译已统计完成", "#173177"),
                        new TemplateMessage.KeyWord1("2022-5-25", "#173177"),
                        new TemplateMessage.KeyWord2("PDF", "#173177"),
                        new TemplateMessage.KeyWord3("21315", "#173177"),
                        new TemplateMessage.KeyWord4("7.58", "#173177"),
                        new TemplateMessage.Remark("欢迎您的使用", "#173177")));
        String result = new Gson().toJson(templateMessage);
        System.out.println(result);
        String s = restTemplate.postForObject(SEND_INDUSTRY_URL.replace("ACCESS_TOKEN", GetAccessTokenUtil.getToken()), result, String.class);
        System.out.println(s);
    }
}
