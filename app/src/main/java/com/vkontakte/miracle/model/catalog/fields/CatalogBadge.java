package com.vkontakte.miracle.model.catalog.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class CatalogBadge {

    private String text="";
    private String type="";

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public CatalogBadge(JSONObject jsonObject) throws JSONException {
        type = jsonObject.getString("type");
        if(jsonObject.has("text")){
            text = jsonObject.getString("text");
        }
    }

}
