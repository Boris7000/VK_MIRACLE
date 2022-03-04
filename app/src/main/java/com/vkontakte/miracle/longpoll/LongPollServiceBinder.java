package com.vkontakte.miracle.longpoll;

import android.os.Binder;

public class LongPollServiceBinder extends Binder {
    private final LongPollService longPollService;

    public LongPollServiceBinder(LongPollService longPollService){
        this.longPollService = longPollService;
    }

    public LongPollService getService() {
        return longPollService;
    }
}
