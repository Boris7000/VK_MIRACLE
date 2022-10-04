package com.vkontakte.miracle.service.longpoll.listeners;

import com.vkontakte.miracle.service.longpoll.model.MessageReadUpdate;

import java.util.ArrayList;

public interface OnMessageReadUpdateListener {
    void onMessageReadUpdate(ArrayList<MessageReadUpdate> messageReadUpdates);
}
