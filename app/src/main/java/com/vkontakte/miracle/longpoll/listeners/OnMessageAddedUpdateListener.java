package com.vkontakte.miracle.longpoll.listeners;

import com.vkontakte.miracle.longpoll.model.MessageAddedUpdate;

import java.util.ArrayList;

public interface OnMessageAddedUpdateListener {
    void onMessageAddedUpdate(ArrayList<MessageAddedUpdate> messageAddedUpdates);
}
