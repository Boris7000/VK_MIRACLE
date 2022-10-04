package com.vkontakte.miracle.service.longpoll.model;

import static com.vkontakte.miracle.service.longpoll.model.LongPollUpdateStorageTypes.ACTION_USER_ONLINE_CHANGE;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;

public class UserOnlineUpdate implements LongPollUpdate, Serializable {

    private final String userId;
    private int flags;
    private int extra;
    private final long ts;

    private final boolean isOnline;

    public String getUserId() {
        return userId;
    }

    public int getFlags() {
        return flags;
    }

    public int getExtra() {
        return extra;
    }

    public long getTs() {
        return ts;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public UserOnlineUpdate(JSONArray jsonArray, boolean isOnline) throws JSONException {

        this.isOnline = isOnline;

        userId = jsonArray.getString(1).substring(1);

        if(isOnline){
            extra = jsonArray.getInt(2);
        } else {
            flags = jsonArray.getInt(2);
        }
        ts = jsonArray.getLong(3);

    }

    @Override
    public int getStorageType() {
        return ACTION_USER_ONLINE_CHANGE;
    }
}
