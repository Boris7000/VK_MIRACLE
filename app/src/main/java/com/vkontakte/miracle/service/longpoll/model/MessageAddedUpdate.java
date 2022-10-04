package com.vkontakte.miracle.service.longpoll.model;

import static com.vkontakte.miracle.engine.util.NetworkUtil.hasFlag;
import static com.vkontakte.miracle.service.longpoll.model.LongPollUpdateStorageTypes.ACTION_MESSAGE_ADDED;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class MessageAddedUpdate implements LongPollUpdate, Serializable {

    public static final int FLAG_UNREAD = 1; //сообщение не прочитано
    public static final int FLAG_OUTBOX = 2; //исходящее сообщение
    public static final int FLAG_REPLIED = 4; //на сообщение был создан ответ
    public static final int FLAG_IMPORTANT = 8; //помеченное сообщение
    public static final int FLAG_FRIENDS = 32; //сообщение отправлено другом
    public static final int FLAG_SPAM = 64; //сообщение помечено как "Спам"
    public static final int FLAG_DELETED = 128; //сообщение удалено (в корзине)
    public static final int FLAG_GROUP_CHAT = 8192;//беседа
    public static final int FLAG_DELETED_FOR_ALL = 131072; //флаг для сообщений, удаленных для получателей

    private final String messageId;
    private final int flags;
    private final String peerId;
    private final long ts;
    private final String text;
    private final String fromId;
    private final boolean hasAttachments;

    private final boolean out;

    //[4,902115,33,320899879,1643612421,"message text",{"title":" ... "},{},0]
    //[4,233,532497,2000000001,1643616485,"message text",{"from":"201000682"},{},0]

    public String getMessageId() {
        return messageId;
    }

    public int getFlags() {
        return flags;
    }

    public String getPeerId() {
        return peerId;
    }

    public long getTs() {
        return ts;
    }

    public String getText() {
        return text;
    }

    public String getFromId() {
        return fromId;
    }

    public boolean hasAttachments() {
        return hasAttachments;
    }

    public boolean isOut() {
        return out;
    }

    public MessageAddedUpdate(JSONArray jsonArray) throws JSONException {
        messageId = jsonArray.getString(1);

        flags = jsonArray.getInt(2);

        out = hasFlag(flags,FLAG_OUTBOX);

        if(jsonArray.getLong(3)>2000000000){
            peerId = String.valueOf(jsonArray.getLong(3)-2000000000);
        } else {
            peerId = jsonArray.getString(3);
        }

        ts = jsonArray.getLong(4);
        text = jsonArray.getString(5);
        JSONObject jsonObject = jsonArray.getJSONObject(6);
        if(jsonObject.has("from")){
            fromId = jsonObject.getString("from");
        } else {
            fromId = peerId;
        }

        hasAttachments = jsonArray.getJSONObject(7).has("attach1_type");
    }

    @Override
    public int getStorageType() {
        return ACTION_MESSAGE_ADDED;
    }
}
