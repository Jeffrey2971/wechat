package com.jeffrey.wechat.entity.menu;

import lombok.Data;

import java.util.List;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class SubButton extends AbstractButton{

    private List<AbstractButton> sub_button;

    public SubButton(String name, List<AbstractButton> sub_button) {
        super(name);
        this.sub_button = sub_button;
    }
}
