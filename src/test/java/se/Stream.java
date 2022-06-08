package se;

import org.apache.tika.Tika;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class Stream {
    public static void main(String[] args) throws IOException {
        FileInputStream fis = new FileInputStream("/Users/jeffrey/Desktop/代测试文档/卓佛教育-广开22春形考成绩导出情况0601.xlsx");
        byte[] bytes = FileCopyUtils.copyToByteArray(fis);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        bais.close();

        FileCopyUtils.copy(bais, new FileOutputStream("/Users/jeffrey/Desktop/tmp/t.xlsx"));


        String detect1 = new Tika().detect(bais);
        System.out.println(detect1);
        String detect2 = new Tika().detect(bais);
        System.out.println(detect2);

    }
}
