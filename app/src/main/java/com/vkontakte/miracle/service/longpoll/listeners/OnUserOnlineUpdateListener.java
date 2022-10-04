package com.vkontakte.miracle.service.longpoll.listeners;

import androidx.collection.ArrayMap;

import com.vkontakte.miracle.service.longpoll.model.UserOnlineUpdate;

public interface OnUserOnlineUpdateListener {
    void onUserOnlineUpdate(ArrayMap<String, UserOnlineUpdate> userOnlineUpdates);
}
