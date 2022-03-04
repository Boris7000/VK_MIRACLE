package com.vkontakte.miracle.model.audio.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class Followed {
    private final String playlistId;
    private final String ownerId;

    public String getPlaylistId() {
        return playlistId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Followed(JSONObject jsonObject) throws JSONException {
        playlistId = jsonObject.getString("playlist_id");
        ownerId = jsonObject.getString("owner_id");
    }
}
