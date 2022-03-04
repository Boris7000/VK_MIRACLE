package com.vkontakte.miracle.model.users.fields;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class LastSeen implements Serializable {
    private final long time;
    private final int platform;

    public int getPlatform() {
        return platform;
    }

    public long getTime() {
        return time;
    }

    public LastSeen(long time, int platform){
        this.time = time;
        this.platform = platform;
    }

    public LastSeen(JSONObject jsonObject) throws JSONException {
        time = jsonObject.getLong("time");
        if(jsonObject.has("platform")){
            platform = jsonObject.getInt("platform");
        } else {
            platform = 0;
        }
    }
}
