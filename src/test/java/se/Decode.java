package se;

import ch.qos.logback.core.encoder.ByteArrayUtil;

import java.util.Base64;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class Decode {
    public static void main(String[] args) {
        byte[] decode = Base64.getDecoder().decode("aHR0cDovLzEyNy4wLjAuMTo4MDEyL2RlbW8vMV%2Flrp7ot7XmiqXlkYrvvIjkvZXmmI7mmJ8pLmRvY3g%3D");
        String s = ByteArrayUtil.toHexString(decode);
        System.out.println(s);
    }
}
