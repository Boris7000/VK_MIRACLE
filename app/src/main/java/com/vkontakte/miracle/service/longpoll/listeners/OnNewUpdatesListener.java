package com.vkontakte.miracle.service.longpoll.listeners;

import com.vkontakte.miracle.service.longpoll.model.MessageAddedUpdate;
import com.vkontakte.miracle.service.longpoll.model.MessageReadUpdate;
import com.vkontakte.miracle.service.longpoll.model.UserOnlineUpdate;
import com.vkontakte.miracle.service.longpoll.model.MessageTypingUpdate;

import java.util.ArrayList;

public interface OnNewUpdatesListener {
    void onMessageTypingUpdates(ArrayList<MessageTypingUpdate> messageTypingUpdates);
    void onMessageAddedUpdates(ArrayList<MessageAddedUpdate> messageAddedUpdates);
    void onMessageReadUpdates(ArrayList<MessageReadUpdate> messageReadUpdates);
    void onUserOnlineUpdates(ArrayList<UserOnlineUpdate> userOnlineUpdates);
}
