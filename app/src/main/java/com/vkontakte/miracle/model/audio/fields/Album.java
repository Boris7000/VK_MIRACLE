package com.vkontakte.miracle.model.audio.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class Album {

    private final String id;
    private final String title;
    private final String ownerId;
    private final String accessKey;
    private Photo thumb;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public Photo getThumb() {
        return thumb;
    }

    public Album(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        title = jsonObject.getString("title");
        ownerId = jsonObject.getString("owner_id");
        accessKey = jsonObject.getString("access_key");

        if(jsonObject.has("thumb")){
            thumb = new Photo(jsonObject.getJSONObject("thumb"));
        }
    }
}
