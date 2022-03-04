package com.vkontakte.miracle.player;

import android.os.Binder;

public class PlayerServiceBinder extends Binder {

    private final AudioPlayerService audioPLayerService;

    public PlayerServiceBinder(AudioPlayerService audioPLayerService){
        this.audioPLayerService = audioPLayerService;
    }

    public AudioPlayerService getService() {
        return audioPLayerService;
    }

}
