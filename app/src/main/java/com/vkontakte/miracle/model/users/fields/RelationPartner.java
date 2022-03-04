package com.vkontakte.miracle.model.users.fields;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class RelationPartner implements Serializable {
    private final String id;
    private final String first_name;
    private final String last_name;

    public String getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public RelationPartner (JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        first_name = jsonObject.getString("first_name");
        last_name = jsonObject.getString("last_name");
    }
}
