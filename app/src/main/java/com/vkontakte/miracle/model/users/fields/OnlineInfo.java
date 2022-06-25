package com.vkontakte.miracle.model.users.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class OnlineInfo {
    private long lastSeen;
    private boolean isOnline;
    private boolean isMobile;
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
        visible = jsonObject.getBoolean("visible");
        if(visible) {
            lastSeen = jsonObject.getLong("last_seen");
            isOnline = jsonObject.getBoolean("is_online");
            isMobile = jsonObject.getBoolean("is_mobile");
        }
    }
}
