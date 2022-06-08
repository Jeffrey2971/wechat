package se;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class ArrayTest {

    @Test
    void splitTest(){
        String[] split = "2022_06_02_测试文档.pdf_en_zh".split("_");
        System.out.println(split[split.length - 1]);
        System.out.println(split[split.length - 2]);
    }

    @Test
    void reference(){

        HashMap<String, ArrayList<String>> supportDoc = new HashMap<>();

        supportDoc.put("admin", new ArrayList<>(Collections.singletonList("test.pdf")));

        ArrayList<String> lists = supportDoc.get("admin");
        lists.add("stu.xlsx");

        // --- 结果 ---
        System.out.println(supportDoc.get("admin"));

    }
}
