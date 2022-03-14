package com.jeffrey.wechat.entity.translation;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 封装了翻译的信息
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@Component
public class TranslationData {
    private Integer error_code;
    private String error_msg;
    private DataInfo data;

    @Data
    public static class DataInfo {
        private String from;
        private String to;
        private String sumSrc;
        private String sumDst;
        private String pasteImg;
        private List<SegmentedData> content;

        @Data
        public static class SegmentedData{
            private String src;
            private String dst;
            private String rect;
            private Integer lineCount;
            private List<Point> points;

            @Data
            public static class Point{
                private Integer x;
                private Integer y;
            }
        }
    }
}
