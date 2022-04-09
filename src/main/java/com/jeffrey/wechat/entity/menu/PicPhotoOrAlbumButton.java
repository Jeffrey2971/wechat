package com.jeffrey.wechat.entity.menu;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class PicPhotoOrAlbumButton extends AbstractButton{
    private String type = "pic_photo_or_album";
    private String key;

    public PicPhotoOrAlbumButton(String name, String key) {
        super(name);
        this.key = key;
    }
}
