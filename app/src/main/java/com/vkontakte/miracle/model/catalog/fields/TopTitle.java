package com.vkontakte.miracle.model.catalog.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class TopTitle {

    private final String icon;
    private final String text;

    public String getIcon() {
        return icon;
    }

    public String getText() {
        return text;
    }

    public TopTitle(JSONObject jsonObject) throws JSONException {
        icon = jsonObject.getString("icon");
        text = jsonObject.getString("text");
    }
}
