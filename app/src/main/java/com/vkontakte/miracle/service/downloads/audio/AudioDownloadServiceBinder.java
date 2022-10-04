package com.vkontakte.miracle.service.downloads.audio;

import android.os.Binder;

public class AudioDownloadServiceBinder extends Binder {

    private final AudioDownloadService audioDownloadService;

    public AudioDownloadServiceBinder(AudioDownloadService audioDownloadService){
        this.audioDownloadService = audioDownloadService;
    }

    public AudioDownloadService getService() {
        return audioDownloadService;
    }

}
