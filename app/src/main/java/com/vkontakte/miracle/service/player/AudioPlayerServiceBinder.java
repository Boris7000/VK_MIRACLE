package com.vkontakte.miracle.service.player;

import android.os.Binder;

public class AudioPlayerServiceBinder extends Binder {

    private final AudioPlayerService audioPLayerService;

    public AudioPlayerServiceBinder(AudioPlayerService audioPLayerService){
        this.audioPLayerService = audioPLayerService;
    }

    public AudioPlayerService getService() {
        return audioPLayerService;
    }

}
