package com.vkontakte.miracle.model.catalog.fields;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Image implements Serializable {

    private final int width;
    private final int height;
    private final String url;

    public Image(JSONObject jsonObject) throws JSONException {
        this.width = jsonObject.getInt("width");
        this.height = jsonObject.getInt("height");
        this.url = jsonObject.getString("url");
    }

    public String getUrl() {
        return url;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
