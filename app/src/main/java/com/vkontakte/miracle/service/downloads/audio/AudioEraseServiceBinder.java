package com.vkontakte.miracle.service.downloads.audio;

import android.os.Binder;

public class AudioEraseServiceBinder extends Binder {

    private final AudioEraseService audioEraseService;

    public AudioEraseServiceBinder(AudioEraseService audioEraseService){
        this.audioEraseService = audioEraseService;
    }

    public AudioEraseService getService() {
        return audioEraseService;
    }

}
