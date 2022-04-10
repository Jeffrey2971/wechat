package management;

import com.google.gson.Gson;
import com.jeffrey.wechat.WechatApplication;
import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import com.jeffrey.wechat.entity.menu.Button;
import com.jeffrey.wechat.entity.menu.PicPhotoOrAlbumButton;
import com.jeffrey.wechat.entity.menu.ViewButton;
import com.jeffrey.wechat.utils.GetAccessTokenUtil;
import com.jeffrey.wechat.utils.RequestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@SpringBootTest(classes = WechatApplication.class)
public class CreateMenu {

    private static WeChatAutoConfiguration.WxConfig wxConfig;

    @Autowired
    public void setWxConfig(WeChatAutoConfiguration.WxConfig wxConfig) {
        CreateMenu.wxConfig = wxConfig;
    }

    @Test
    void createMenu() {
        Button button = new Button();
        button.getButton().add(new ViewButton("使用方式", "https://mp.weixin.qq.com/s/3ypEPH04Q6CN7SS7CwGFXQ"));
        button.getButton().add(new PicPhotoOrAlbumButton("发送图片", "menu_image_upload"));

        HashMap<String, Object> httpParams = new HashMap<>();
        httpParams.put("token", GetAccessTokenUtil.getToken());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setContentLanguage(Locale.CHINESE);

        HttpEntity<String> httpEntity = new HttpEntity<>(new Gson().toJson(button), httpHeaders);

        ResponseEntity<String> responseEntity = RequestUtil.postEntity(wxConfig.getWxCreateMenuUrl(), httpEntity, String.class, httpParams);

        System.out.println(responseEntity.getBody());
    }
}
