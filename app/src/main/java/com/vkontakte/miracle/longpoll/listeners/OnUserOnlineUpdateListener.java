package com.vkontakte.miracle.longpoll.listeners;

import androidx.collection.ArrayMap;

import com.vkontakte.miracle.longpoll.model.UserOnlineUpdate;

public interface OnUserOnlineUpdateListener {
    void onUserOnlineUpdate(ArrayMap<String, UserOnlineUpdate> userOnlineUpdates);
}
