package com.vkontakte.miracle.longpoll.model;

import static com.vkontakte.miracle.longpoll.model.LongPollUpdateStorageTypes.ACTION_SET_INPUT_MESSAGES_AS_READ;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;

public class MessageReadUpdate implements LongPollUpdate, Serializable {

    private final String peerId;
    private final String localId;
    private final boolean isOut;

    public String getPeerId() {
        return peerId;
    }

    public String getLocalId() {
        return localId;
    }

    public boolean isOut() {
        return isOut;
    }

    public MessageReadUpdate(boolean isOut, JSONArray jsonArray) throws JSONException {

        if(jsonArray.getLong(1)>2000000000){
            peerId = String.valueOf(jsonArray.getLong(1)-2000000000);
        } else {
            peerId = jsonArray.getString(1);
        }

        localId = jsonArray.getString(2);
        this.isOut = isOut;
    }

    @Override
    public int getStorageType() {
        return ACTION_SET_INPUT_MESSAGES_AS_READ;
    }
}
