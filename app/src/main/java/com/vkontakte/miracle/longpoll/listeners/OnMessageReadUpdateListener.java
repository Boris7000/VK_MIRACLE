package com.vkontakte.miracle.longpoll.listeners;

import com.vkontakte.miracle.longpoll.model.MessageReadUpdate;

import java.util.ArrayList;

public interface OnMessageReadUpdateListener {
    void onMessageReadUpdate(ArrayList<MessageReadUpdate> messageReadUpdates);
}
