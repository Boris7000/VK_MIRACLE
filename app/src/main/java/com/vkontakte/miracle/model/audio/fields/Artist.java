package com.vkontakte.miracle.model.audio.fields;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Artist implements Serializable {

    private String id;
    private final String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Artist(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("id")) {
            id = jsonObject.getString("id");
        }
        name = jsonObject.getString("name");
    }

}
