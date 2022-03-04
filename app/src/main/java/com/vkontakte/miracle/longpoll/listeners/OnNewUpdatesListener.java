package com.vkontakte.miracle.longpoll.listeners;

import com.vkontakte.miracle.longpoll.model.MessageAddedUpdate;
import com.vkontakte.miracle.longpoll.model.MessageReadUpdate;
import com.vkontakte.miracle.longpoll.model.UserOnlineUpdate;
import com.vkontakte.miracle.longpoll.model.MessageTypingUpdate;

import java.util.ArrayList;

public interface OnNewUpdatesListener {
    void onMessageTypingUpdates(ArrayList<MessageTypingUpdate> messageTypingUpdates);
    void onMessageAddedUpdates(ArrayList<MessageAddedUpdate> messageAddedUpdates);
    void onMessageReadUpdates(ArrayList<MessageReadUpdate> messageReadUpdates);
    void onUserOnlineUpdates(ArrayList<UserOnlineUpdate> userOnlineUpdates);
}
