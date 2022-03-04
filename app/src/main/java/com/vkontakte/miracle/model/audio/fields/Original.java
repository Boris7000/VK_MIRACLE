package com.vkontakte.miracle.model.audio.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class Original {
    private final String playlistId;
    private final String ownerId;
    private final String accessKey;

    public String getId() {
        return playlistId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public Original(JSONObject jsonObject) throws JSONException {
        playlistId = jsonObject.getString("playlist_id");
        ownerId = jsonObject.getString("owner_id");
        accessKey = jsonObject.getString("access_key");
    }
}
