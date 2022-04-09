package com.jeffrey.wechat.entity.menu;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class Button {

    private List<AbstractButton> button = new ArrayList<>();

}
