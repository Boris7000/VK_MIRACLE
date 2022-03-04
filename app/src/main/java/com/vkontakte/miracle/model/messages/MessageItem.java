package com.vkontakte.miracle.model.messages;

import android.util.ArrayMap;
import android.util.Log;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.longpoll.model.MessageAddedUpdate;
import com.vkontakte.miracle.model.Attachments;
import com.vkontakte.miracle.model.Owner;

import org.json.JSONException;
import org.json.JSONObject;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_MESSAGE_IN;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_MESSAGE_OUT;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MessageItem implements ItemDataHolder {
    private final String id;
    private final long date;
    private final String time;
    private final boolean out;
    private final Owner owner;
    private final String text;
    private Attachments attachments;

    public String getId() {
        return id;
    }
    public long getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }
    public boolean isOut() {
        return out;
    }
    public Owner getOwner() {
        return owner;
    }
    public String getText() {
        return text;
    }
    public Attachments getAttachments() {
        return attachments;
    }

    public MessageItem(JSONObject jsonObject, ArrayMap<String,Owner> ownerArrayMap) throws JSONException {

      Log.d("ABOBA", jsonObject.toString());

        if(jsonObject.has("id")) {
            id = jsonObject.getString("id");
        } else {
            id = jsonObject.getString("conversation_message_id");
        }

        date = jsonObject.getLong("date");
        time = new SimpleDateFormat("H:mm", Locale.getDefault()).format(date*1000);

        owner = ownerArrayMap.get(jsonObject.getString("from_id"));

        if(jsonObject.has("out")) {
            out = jsonObject.getInt("out")==1;
        }else {
            out = false;
        }

        if(jsonObject.has("text")){
            text = jsonObject.getString("text");
        }else {
            text = "";
        }

        if(jsonObject.has("attachments")){
            attachments = new Attachments(jsonObject.getJSONArray("attachments"));
        }

    }

    public MessageItem(MessageAddedUpdate messageAddedUpdate, Owner owner){
        id = messageAddedUpdate.getMessageId();
        date = messageAddedUpdate.getTs();
        time = new SimpleDateFormat("H:mm", Locale.getDefault()).format(date*1000);
        this.owner = owner;
        text = messageAddedUpdate.getText();
        out = messageAddedUpdate.isOut();
    }

    @Override
    public int getViewHolderType() {
        if(out){
            return TYPE_MESSAGE_OUT;
        } else {
            return TYPE_MESSAGE_IN;
        }

    }
}
