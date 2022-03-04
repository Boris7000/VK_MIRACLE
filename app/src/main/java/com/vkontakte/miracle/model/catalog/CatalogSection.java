package com.vkontakte.miracle.model.catalog;

import org.json.JSONException;
import org.json.JSONObject;

public class CatalogSection {

    private final String id;
    private String title;

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public CatalogSection(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        if(jsonObject.has("title")){
            title = jsonObject.getString("title");
        }
    }
}
