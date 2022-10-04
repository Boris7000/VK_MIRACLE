package com.vkontakte.miracle.service.longpoll.listeners;

import com.vkontakte.miracle.service.longpoll.model.MessageAddedUpdate;

import java.util.ArrayList;

public interface OnMessageAddedUpdateListener {
    void onMessageAddedUpdate(ArrayList<MessageAddedUpdate> messageAddedUpdates);
}
