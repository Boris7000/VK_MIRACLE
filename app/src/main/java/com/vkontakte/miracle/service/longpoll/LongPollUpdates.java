package com.vkontakte.miracle.service.longpoll;

import static com.vkontakte.miracle.service.longpoll.model.LongPollUpdateTypes.ACTION_MESSAGE_ADDED;
import static com.vkontakte.miracle.service.longpoll.model.LongPollUpdateTypes.ACTION_SET_INPUT_MESSAGES_AS_READ;
import static com.vkontakte.miracle.service.longpoll.model.LongPollUpdateTypes.ACTION_SET_OUTPUT_MESSAGES_AS_READ;
import static com.vkontakte.miracle.service.longpoll.model.LongPollUpdateTypes.ACTION_USERS_WRITE_AUDIO_IN_CHAT;
import static com.vkontakte.miracle.service.longpoll.model.LongPollUpdateTypes.ACTION_USERS_WRITE_TEXT_IN_CHAT;
import static com.vkontakte.miracle.service.longpoll.model.LongPollUpdateTypes.ACTION_USER_IS_OFFLINE;
import static com.vkontakte.miracle.service.longpoll.model.LongPollUpdateTypes.ACTION_USER_IS_ONLINE;
import static com.vkontakte.miracle.service.longpoll.model.LongPollUpdateTypes.ACTION_USER_WRITE_TEXT_IN_CHAT;
import static com.vkontakte.miracle.service.longpoll.model.LongPollUpdateTypes.ACTION_USER_WRITE_TEXT_IN_DIALOG;

import com.vkontakte.miracle.service.longpoll.model.MessageAddedUpdate;
import com.vkontakte.miracle.service.longpoll.model.MessageReadUpdate;
import com.vkontakte.miracle.service.longpoll.model.MessageTypingUpdate;
import com.vkontakte.miracle.service.longpoll.model.UserOnlineUpdate;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class LongPollUpdates {

    private final ArrayList<MessageTypingUpdate> messageTypingUpdates = new ArrayList<>();

    public void addMessageWritingUpdate(MessageTypingUpdate messageTypingUpdate){
        messageTypingUpdates.add(messageTypingUpdate);
    }

    public ArrayList<MessageTypingUpdate> getWriteTextInDialogUpdates() {
        return messageTypingUpdates;
    }

    private final ArrayList<UserOnlineUpdate> userOnlineUpdates = new ArrayList<>();

    public void addUserOnlineUpdate(UserOnlineUpdate userOnlineUpdate){
        userOnlineUpdates.add(userOnlineUpdate);
    }

    public ArrayList<UserOnlineUpdate> getUserOnlineUpdates() {
        return userOnlineUpdates;
    }

    private final ArrayList<MessageAddedUpdate> messageAddedUpdates = new ArrayList<>();

    public void addMessageAddedUpdate(MessageAddedUpdate messageAddedUpdate){
        messageAddedUpdates.add(messageAddedUpdate);
    }

    public ArrayList<MessageAddedUpdate> getMessageAddedUpdates() {
        return messageAddedUpdates;
    }


    private final ArrayList<MessageReadUpdate> messageReadUpdates = new ArrayList<>();

    public void addMessageReadUpdate(MessageReadUpdate messageReadUpdate){
        messageReadUpdates.add(messageReadUpdate);
    }

    public ArrayList<MessageReadUpdate> getMessageReadUpdates() {
        return messageReadUpdates;
    }

    public LongPollUpdates(JSONArray updates) throws JSONException {

        for(int i=0;i<updates.length();i++){
            JSONArray ja_update = updates.getJSONArray(i);
            switch (ja_update.getInt(0)){

                case ACTION_MESSAGE_ADDED:{
                    addMessageAddedUpdate(new MessageAddedUpdate(ja_update));
                    break;
                }
                case ACTION_USER_IS_ONLINE:{
                    addUserOnlineUpdate(new UserOnlineUpdate(ja_update,true));
                    break;
                }
                case ACTION_USER_IS_OFFLINE:{
                    addUserOnlineUpdate(new UserOnlineUpdate(ja_update,false));
                    break;
                }
                case ACTION_USER_WRITE_TEXT_IN_DIALOG: {
                    addMessageWritingUpdate(new MessageTypingUpdate(ja_update.getString(1)));
                    break;
                }
                case ACTION_USER_WRITE_TEXT_IN_CHAT: {
                    addMessageWritingUpdate(new MessageTypingUpdate(ja_update.getString(1),ja_update.getString(2)));
                    break;
                }
                case ACTION_USERS_WRITE_TEXT_IN_CHAT: {

                    if(ja_update.get(1) instanceof JSONArray) {
                        addMessageWritingUpdate(new MessageTypingUpdate(ja_update.getJSONArray(1), ja_update.getString(2), true));
                    } else {
                        addMessageWritingUpdate(new MessageTypingUpdate(ja_update.getString(1), ja_update.getString(2), true));
                    }
                    break;
                }
                case ACTION_USERS_WRITE_AUDIO_IN_CHAT:{
                    if(ja_update.get(1) instanceof JSONArray) {
                        addMessageWritingUpdate(new MessageTypingUpdate(ja_update.getJSONArray(1), ja_update.getString(2), false));
                    } else {
                        addMessageWritingUpdate(new MessageTypingUpdate(ja_update.getString(1), ja_update.getString(2), false));
                    }
                    break;
                }
                case ACTION_SET_INPUT_MESSAGES_AS_READ:{
                    addMessageReadUpdate(new MessageReadUpdate(false, ja_update));
                    break;
                }
                case ACTION_SET_OUTPUT_MESSAGES_AS_READ:{
                    addMessageReadUpdate(new MessageReadUpdate(true, ja_update));
                    break;
                }

            }
        }
    }

    public LongPollUpdates(){}

}
