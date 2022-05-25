package com.jeffrey.wechat.entity.template;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@AllArgsConstructor
public class TemplateMessage {

    private String touser;
    private String template_id;
    private String url;
    private Data data;


    @lombok.Data
    @AllArgsConstructor
    public static class Data{
        private First first;
        private KeyWord1 keyword1;
        private KeyWord2 keyword2;
        private KeyWord3 keyword3;
        private KeyWord4 keyword4;
        private Remark remark;
    }

    @lombok.Data
    @AllArgsConstructor
    public static class First{
        private String value;
        private String color;
    }

    @lombok.Data
    @AllArgsConstructor
    public static class KeyWord1{
        private String value;
        private String color;
    }

    @lombok.Data
    @AllArgsConstructor
    public static class KeyWord2{
        private String value;
        private String color;
    }

    @lombok.Data
    @AllArgsConstructor
    public static class KeyWord3{
        private String value;
        private String color;
    }

    @lombok.Data
    @AllArgsConstructor
    public static class KeyWord4{
        private String value;
        private String color;
    }

    @lombok.Data
    @AllArgsConstructor
    public static class Remark{
        private String value;
        private String color;
    }
}