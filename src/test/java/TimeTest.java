import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class TimeTest {
    @Test
    public void t(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String format = dtf.format(LocalDateTime.now());
        System.out.println(format);
    }

    @Test
    void tt(){
        System.out.println(new Date(1816252420));
    }
}
