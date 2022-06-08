//package openoffice;
//
//import com.jeffrey.wechat.WechatApplication;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.io.File;
//
///**
// * @author jeffrey
// * @since JDK 1.8
// */
//
//@SpringBootTest(classes = WechatApplication.class)
//
//public class Converter {
//
//    private final DocumentConverter documentConverter;
//
//    @Autowired
//    public Converter(DocumentConverter documentConverter) {
//        this.documentConverter = documentConverter;
//    }
//
//
//    @Test
//    void converter() throws OfficeException {
//        documentConverter.convert(new File("/Users/jeffrey/Desktop/tmp/t.xlsx")).to(
//                new File("/Users/jeffrey/Desktop/tmp/t.pdf")
//        ).execute();
//    }
//}
