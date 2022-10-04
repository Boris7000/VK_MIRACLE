package com.vkontakte.miracle.service.longpoll.listeners;

import com.vkontakte.miracle.service.longpoll.model.MessageTypingUpdate;

import java.util.ArrayList;

public interface OnMessageTypingUpdateListener {
    void onMessageTypingUpdate(ArrayList<MessageTypingUpdate> messageTypingUpdates);
}
