package com.vkontakte.miracle.model.users.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class OnlineInfo {
    private final long lastSeen;
    private final boolean isOnline;
    private final boolean isMobile;
    private final boolean visible;

    public long getLastSeen() {
        return lastSeen;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public boolean isMobile() {
        return isMobile;
    }

    public boolean isVisible() {
        return visible;
    }

    public OnlineInfo(JSONObject jsonObject) throws JSONException {
        lastSeen = jsonObject.getLong("last_seen");
        isOnline = jsonObject.getBoolean("is_online");
        isMobile = jsonObject.getBoolean("is_mobile");
        visible = jsonObject.getBoolean("visible");
    }
}
