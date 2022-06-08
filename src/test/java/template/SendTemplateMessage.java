package template;

import com.google.gson.Gson;
import com.jeffrey.wechat.entity.template.TemplateMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class SendTemplateMessage {
    public static void main(String[] args) {
        String requestBody = new Gson().toJson(new TemplateMessage(
                "ojJ8V6tk2_vssiSqI5seK_ljRSOg",
                "3WIuYvL81lgPorkDyUP7-biX173P8oX0QsO3BzzJHR8",
                null,
                new TemplateMessage.Data(
                        new TemplateMessage.First("您上传的文档有新的动态", "#173177"),
                        new TemplateMessage.KeyWord1("努力.pdf", "#173177"),
                        new TemplateMessage.KeyWord2("英文 -> 中文", "#173177"),
                        new TemplateMessage.KeyWord3("成功", "#173177"),
                        new TemplateMessage.KeyWord4(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(LocalDateTime.now()), "#173177"),
                        new TemplateMessage.Remark("点击翻译文档，感谢您的使用！", "#173177")))
        );
        System.out.println(requestBody);
    }
}
