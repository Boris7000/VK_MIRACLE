package com.vkontakte.miracle.model.messages.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class Peer {
    private final String id;
    private final String type;
    private final String localId;

    public String getId() {
        return id;
    }
    public String getType() {
        return type;
    }
    public String getLocalId() {
        return localId;
    }

    public Peer(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        type = jsonObject.getString("type");
        localId = jsonObject.getString("local_id");
    }
}
