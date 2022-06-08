package template;

import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class Type {
    private static final Tika TIKA = new Tika();
    public static void main(String[] args) throws IOException {
        System.out.println(TIKA.detect(new File("/Users/jeffrey/IdeaProjects/JavaReview/Data/Data/5_每日一考与复习/第1部分：Java基础编程/day06.pptx")));
        // doc --> application/msword
        // txt --> text/plain
        // docx --> application/vnd.openxmlformats-officedocument.wordprocessingml.document
        // pdf --> application/pdf
        // xls --> application/vnd.ms-excel
        // xlsx -- > application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
        // pptx --> application/vnd.openxmlformats-officedocument.presentationml.presentation

    }
}
