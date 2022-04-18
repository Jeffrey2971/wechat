package service;

import com.google.gson.Gson;
import com.jeffrey.wechat.WechatApplication;
import com.jeffrey.wechat.config.WeChatAutoConfiguration;
import com.jeffrey.wechat.mapper.ProcessEventMessageDao;
import com.jeffrey.wechat.entity.AccessToken;
import com.jeffrey.wechat.entity.QrCodeResult;
import com.jeffrey.wechat.entity.UploadMediaResult;
import com.jeffrey.wechat.entity.message.customer.CustomerTextMessage;
import com.jeffrey.wechat.entity.mapper.UserInfo;
import com.jeffrey.wechat.entity.translation.TranslationData;
import com.jeffrey.wechat.service.GetFreeService;
import com.jeffrey.wechat.service.WeChatService;
import com.jeffrey.wechat.utils.FileUploadMediaDataUtil;
import com.jeffrey.wechat.utils.GetAccessTokenUtil;
import com.jeffrey.wechat.utils.GetQrCodeUtil;
import com.jeffrey.wechat.utils.RequestUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author jeffrey
 * @since JDK 1.8
 */
@Slf4j
@SpringBootTest(classes = WechatApplication.class)
public class WxTest {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private ProcessEventMessageDao processEventMessageDao;

    @Autowired
    private WeChatAutoConfiguration.WxConfig wxConfig;

    @Autowired
    private GetFreeService getFreeService;

    @Test
    void retain(){

        log.info("重置用户每日使用量");
        getFreeService.resetCanUseColumn(wxConfig.getWxDayCanUse());

        log.info("移除表中无效用户");

        List<String> GFSUserOpenIdList = getFreeService.selectUserUseTotalUserOpenIdList();

        GFSUserOpenIdList.removeAll(weChatService.selectUserOpenIdList());

        GFSUserOpenIdList.forEach(System.out::println);
    }

    @Test
    void selectUserOpenIdList(){
        weChatService.selectUserOpenIdList().forEach(System.out::println);
    }

    @Test
    void deleteMediaData(){
        HashMap<String, Object> httpParams = new HashMap<>();
        httpParams.put("token", GetAccessTokenUtil.getToken());
        RestTemplate restTemplate = new RestTemplate();
        String body = "{\"media_id\": \"wfwZKfV6EApt33eexp66QNKq7tSh1OYInuJum1mgCtPgPY4HJjdNC53IWw8wuX9H\"}";
        System.out.println(body);
        String aaa = restTemplate.postForObject(wxConfig.getWxDeleteMediaDataUrl(), body, String.class, httpParams);
        System.out.println(aaa);

    }

    @Test
    void testGetWxQrCodeUrl() {

        ResponseEntity<QrCodeResult> qrCode = GetQrCodeUtil.getQrCode("QR_STR_SCENE", "asdas", null, 60);
        String styleShareOrCode = GetQrCodeUtil.getStyleShareOrCode(qrCode.getBody().getUrl());
        System.out.println(styleShareOrCode);


    }

    @Test
    void testBaiduTrans() throws Exception {
        File file = new File("/Users/jeffrey/Desktop/pictureTransNodeJsDemo/test.jpg");

        ByteArrayResource byteArrayResource = new ByteArrayResource(FileCopyUtils.copyToByteArray(file)) {
            @Override
            public String getFilename() {
                return "a";
            }
        };


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        LinkedMultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
//        FileSystemResource fileSystemResource = new FileSystemResource(file);

        data.add("image", byteArrayResource);
        data.add("from", "auto");
        data.add("to", "zh");
        data.add("appid", "20200709000515966");
        data.add("salt", 1234567890);
        data.add("cuid", "APICUID");
        data.add("mac", "mac");
        data.add("version", 3);
        data.add("paste", 1);
        // sign: 7c31fde192637b51ed38dd4bff10579d
        // image: 92db74820211513bd471ad591801fb85
        String md5Image = DigestUtils.md5DigestAsHex(FileCopyUtils.copyToByteArray(file));
        data.add("sign", DigestUtils.md5DigestAsHex(("20200709000515966" + md5Image + 1234567890 + "APICUID" + "mac" + "gkV0xq2Wz5zgCgbft68b").getBytes()).toLowerCase());

        HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity<>(data, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<TranslationData> responseEntity = restTemplate.postForEntity("https://fanyi-api.baidu.com/api/trans/sdk/picture", httpEntity, TranslationData.class);

        System.out.println(responseEntity);
    }

    private TranslationData translationData(InputStream is) throws IOException {

        return null;
    }


    @Test
    void testFileUpload() throws IOException {
        UploadMediaResult uploadMediaResult = FileUploadMediaDataUtil.fileUpload(new FileInputStream("/Users/jeffrey/Desktop/photo/文件上传时发送的 HTTP 协议内容.png"), "测试", "png");

        System.out.println(uploadMediaResult);
    }


    @Test
    void testGetUserInfo() throws IllegalAccessException {
        HashMap<String, Object> httpParams = new HashMap<>();
        httpParams.put("token", GetAccessTokenUtil.getToken());
        httpParams.put("openid", "ojJ8V6inNjQh3tvCoO-Kr6d7UcYQ");
        httpParams.put("lang", "zh_CN");
        ResponseEntity<UserInfo> entity = RequestUtil.getEntity("https://api.weixin.qq.com/cgi-bin/user/info?access_token={token}&openid={openid}&lang={lang}", UserInfo.class, httpParams);

        if (entity.getStatusCodeValue() == 200 && entity.getBody() != null) {
            UserInfo userInfo = entity.getBody();
            for (Field field : userInfo.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(userInfo);
                if (value instanceof Integer && (int) value == 0) {
                    field.set(userInfo, -1);
                } else if (value instanceof List) {
                    if (((List<?>) value).size() <= 0) {
                        ArrayList<Integer> lists = new ArrayList<>(1);
                        lists.add(-1);
                        field.set(userInfo, lists.toString());
                    } else {
                        field.set(userInfo, userInfo.getTagid_list().toString());
                    }

                } else if (value instanceof String && !StringUtils.hasText(String.valueOf(value))) {
                    field.set(userInfo, null);
                }
            }

            processEventMessageDao.insert(userInfo);
        }


    }

    @Test
    void sendTestEmail() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo("jeffrey2971@outlook.com");
        simpleMailMessage.setCc("664490254@qq.com");
        simpleMailMessage.setSubject("Test");
        simpleMailMessage.setText("测试测试测试");
        javaMailSender.send(simpleMailMessage);
    }

    @Test
    void testGetWxAccessToken() {
        for (int i = 0; i < 20; i++) {

            System.out.println(GetAccessTokenUtil.getToken());

            if (i % 2 == 0) {
                System.out.println("测试缓存过期");
                GetAccessTokenUtil.expiresTime = GetAccessTokenUtil.expiresTime - (130 * 60) * 1000;
            }
        }
    }

    @Autowired
    private Gson gson;

    @Test
    void testRequestUtil() {
        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token={token}";
        CustomerTextMessage textMessage = new CustomerTextMessage("ojJ8V6inNjQh3tvCoO-Kr6d7UcYQ", new CustomerTextMessage.Text("测试客服响应"));
        String s = gson.toJson(textMessage);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(MediaType.parseMediaTypes(MediaType.APPLICATION_JSON_VALUE));
        HttpEntity<Object> httpEntity = new HttpEntity<>(s, httpHeaders);
        Map<String, Object> map = new HashMap<>();
        map.put("token", GetAccessTokenUtil.getToken());
        ResponseEntity<AccessToken> entity = RequestUtil.postEntity(url, httpEntity, AccessToken.class, map);
        System.out.println(entity);


    }

    public UploadMediaResult fileUpload(@NotNull InputStream is, @NotNull String fileName, @NotNull  String fileSuffix) throws IOException {

        // 请求头
        LinkedMultiValueMap<String, Object> data = new LinkedMultiValueMap<>();

        ByteArrayResource bar = new ByteArrayResource(FileCopyUtils.copyToByteArray(is)) {
            @Override
            public String getFilename() {
                return fileName + "." + fileSuffix;
            }
        };

        data.add("media", bar);

        // 头部分
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.setContentLength(bar.contentLength());

        HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity<>(data, httpHeaders);

        HashMap<String, Object> httpParams = new HashMap<>();
        httpParams.put("token", GetAccessTokenUtil.getToken());
        httpParams.put("type", fileSuffix);


        ResponseEntity<String> resultResponseEntity = RequestUtil.postEntity(wxConfig.getWxUploadMediaDataUrl(), httpEntity, String.class, httpParams);


        if (resultResponseEntity.getStatusCodeValue() == 200 && resultResponseEntity.getBody() != null) {
            return gson.fromJson(resultResponseEntity.getBody(), UploadMediaResult.class);
        }

        return null;

    }


    @Data
    @AllArgsConstructor
    private static class ActionInfo{
        private Scene scene;
    }

    @Data
    @AllArgsConstructor
    private static class Scene{
        private String scene_str;
    }

    @Data
    @AllArgsConstructor
    private static class QrCode{
        private String action_name;
        private ActionInfo action_info;
    }

}


