package se;

import org.junit.jupiter.api.Test;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class StringTest {
    @Test
    void t1() {
        String str = "test.pdf_zh_en";
        String step1 = str.replace(str.substring(str.lastIndexOf("_")), "");
        System.out.println(step1.replace(step1.substring(step1.lastIndexOf("_")), ""));


        String oldName = "te_st.pdf_zh_en";

        String[] split = oldName.split("_");
        oldName = oldName.replace(oldName.substring(oldName.lastIndexOf(split[split.length - 2]) - 1), "");
        System.out.println(oldName);

    }
    @Test
    void t2(){
        String from = "kor";
        String str = from.equalsIgnoreCase("zh")
                ? "中文" : from.equalsIgnoreCase("en")
                ? "英文" : from.equalsIgnoreCase("jp")
                ? "日文" : from.equalsIgnoreCase("kor")
                ? "韩文" : "未知";

        System.out.println(str);
    }

    @Test
    void t3(){
        String url = "http://www.jeffrey2971.top";
        String base64Url = Base64.getEncoder().encodeToString(url.getBytes(StandardCharsets.UTF_8));
        System.out.println(base64Url);

    }
}
