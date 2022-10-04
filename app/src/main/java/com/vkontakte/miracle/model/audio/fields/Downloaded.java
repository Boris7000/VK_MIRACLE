package com.vkontakte.miracle.model.audio.fields;

import java.io.Serializable;

public class Downloaded implements Serializable {

    private final String path;

    public Downloaded(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
