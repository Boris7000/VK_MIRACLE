package com.vkontakte.miracle.longpoll.model;

import static com.vkontakte.miracle.engine.util.NetworkUtil.parseStringArrayList;
import static com.vkontakte.miracle.longpoll.model.LongPollUpdateStorageTypes.ACTION_MESSAGE_TYPING;
import static com.vkontakte.miracle.longpoll.model.LongPollUpdateTypes.ACTION_USER_WRITE_TEXT_IN_DIALOG;
import static com.vkontakte.miracle.longpoll.model.LongPollUpdateTypes.ACTION_USERS_WRITE_AUDIO_IN_CHAT;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;

public class MessageTypingUpdate implements LongPollUpdate, Serializable {

    private final String peerId;
    private final ArrayList<String> fromIds;
    private final boolean isText;

    public String getPeerId() {
        return peerId;
    }

    public ArrayList<String> getFromIds() {
        return fromIds;
    }

    public boolean isText() {
        return isText;
    }

    public MessageTypingUpdate(String user_id) {
        peerId = user_id;
        fromIds = new ArrayList<>();
        fromIds.add(user_id);
        isText = true;
    }

    public MessageTypingUpdate(String user_id, String chat_id) {
        peerId = chat_id;
        fromIds = new ArrayList<>();
        fromIds.add(user_id);
        isText = true;
    }

    public MessageTypingUpdate(JSONArray user_ids, String peer_id, boolean isText) throws JSONException {
        peerId = peer_id;
        fromIds = parseStringArrayList(user_ids);
        this.isText = isText;
    }

    public MessageTypingUpdate(String user_id, String peer_id, boolean isText) {
        peerId = peer_id;
        fromIds = new ArrayList<>();
        fromIds.add(user_id);
        this.isText = isText;
    }

    @Override
    public int getStorageType() {
        return ACTION_MESSAGE_TYPING;
    }
}
