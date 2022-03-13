package com.vkontakte.miracle.model.catalog.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class CatalogLayout {

    private final String name;
    private String title;

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public CatalogLayout(JSONObject jsonObject) throws JSONException {

        name = jsonObject.getString("name");

        if(jsonObject.has("title")){
            title = jsonObject.getString("title");
        }
    }
}
