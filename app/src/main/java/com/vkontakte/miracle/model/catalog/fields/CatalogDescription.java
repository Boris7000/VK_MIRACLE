package com.vkontakte.miracle.model.catalog.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class CatalogDescription {
    private final String text;

    public String getText() {
        return text;
    }

    public CatalogDescription(JSONObject jsonObject) throws JSONException {
        text = jsonObject.getString("text");
    }
}
