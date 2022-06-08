package upload;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class CheckFileType {
    private static final MimeTypes DEFAULT_MIME_TYPES = MimeTypes.getDefaultMimeTypes();

    public static void main(String[] args) throws IOException, MimeTypeException {
        Tika tika = new Tika();


        FileInputStream fis = new FileInputStream(new File("/Volumes/Data/@Jeffrey.txt"));
        String detect = tika.detect(fis, "@Jeffrey.txt");
        MimeType mimeType = DEFAULT_MIME_TYPES.forName(detect);
        System.out.println(detect);
    }

}
