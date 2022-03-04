package com.vkontakte.miracle.longpoll.listeners;

import com.vkontakte.miracle.longpoll.model.MessageTypingUpdate;

import java.util.ArrayList;

public interface OnMessageTypingUpdateListener {
    void onMessageTypingUpdate(ArrayList<MessageTypingUpdate> messageTypingUpdates);
}
