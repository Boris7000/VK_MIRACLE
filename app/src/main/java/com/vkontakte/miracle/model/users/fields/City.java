package com.vkontakte.miracle.model.users.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class City {

    private final String id;
    private final String title;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public City(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        title = jsonObject.getString("title");
    }
}
