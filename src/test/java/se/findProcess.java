package se;

import org.apache.commons.io.input.ReaderInputStream;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class findProcess {
    @Test
    void find() throws IOException {
        Process exec = Runtime.getRuntime().exec("lsof -i:8012");
        InputStream inputStream = exec.getInputStream();
        System.out.println(inputStream.read());


    }
}
