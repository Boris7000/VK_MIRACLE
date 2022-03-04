package com.vkontakte.miracle.model.messages;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_CONVERSATION;

import android.util.ArrayMap;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.Owner;
import com.vkontakte.miracle.model.messages.fields.CanWrite;
import com.vkontakte.miracle.model.messages.fields.ChatSettings;
import com.vkontakte.miracle.model.messages.fields.Peer;
import com.vkontakte.miracle.model.messages.fields.PushSettings;

import org.json.JSONException;
import org.json.JSONObject;

public class ConversationItem implements ItemDataHolder {

    private int unreadCount = 0;
    private String outRead = "";
    private String inRead = "";
    private final Peer peer;
    private ChatSettings chatSettings;
    private Owner owner;
    private MessageItem lastMessage;
    private final CanWrite canWrite;
    private PushSettings pushSettings;

    public int getUnreadCount() {
        return unreadCount;
    }

    public String getInRead() {
        return inRead;
    }

    public String getOutRead() {
        return outRead;
    }

    public Peer getPeer() {
        return peer;
    }

    public ChatSettings getChatSettings() {
        return chatSettings;
    }

    public Owner getOwner() {
        return owner;
    }

    public MessageItem getLastMessage() {
        return lastMessage;
    }

    public CanWrite getCanWrite() {
        return canWrite;
    }

    public PushSettings getPushSettings() {
        return pushSettings;
    }

    public void setLastMessage(MessageItem lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public void setOutRead(String outRead){
        this.outRead = outRead;
    }

    public void setInRead(String inRead){
        this.inRead = inRead;
    }

    public ConversationItem(JSONObject jsonObject, ArrayMap<String,Owner> ownerArrayMap) throws JSONException {

        canWrite = new CanWrite(jsonObject.getJSONObject("can_write"));

        peer = new Peer(jsonObject.getJSONObject("peer"));

        switch (peer.getType()){
            case "user":{
                owner = ownerArrayMap.get(peer.getLocalId());
                break;
            }
            case "group":{
                owner = ownerArrayMap.get("-"+peer.getLocalId());
                break;
            }
            case "chat":{
                chatSettings = new ChatSettings(jsonObject.getJSONObject("chat_settings"));
                break;
            }
        }

        if(jsonObject.has("push_settings")){
            pushSettings = new PushSettings(jsonObject.getJSONObject("push_settings"));
        }

        if(jsonObject.has("unread_count")){
            unreadCount = jsonObject.getInt("unread_count");
        }

        if(jsonObject.has("in_read")){
            inRead = jsonObject.getString("in_read");
        }

        if(jsonObject.has("out_read")){
            outRead = jsonObject.getString("out_read");
        }
    }

    @Override
    public int getViewHolderType() {
        return TYPE_CONVERSATION;
    }
}
