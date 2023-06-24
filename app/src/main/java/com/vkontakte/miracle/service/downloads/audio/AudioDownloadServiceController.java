package com.vkontakte.miracle.service.downloads.audio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.vkontakte.miracle.MainApp;
import com.vkontakte.miracle.model.audio.AudioItem;

public class AudioDownloadServiceController {

    private static AudioDownloadServiceController instance;

    private AudioDownloadService audioDownloadService;

    public AudioDownloadServiceController(){
        instance = this;
    }

    public static AudioDownloadServiceController getInstance(){
        return new AudioDownloadServiceController();
    }

    public void addDownload(AudioItem audioItem){
        if(audioDownloadService !=null) {
            audioDownloadService.addNewDownload(audioItem);
        } else {
            startAndDownload(audioItem);
        }
    }

    private void startAndDownload(AudioItem audioItem){

        MainApp mainApp = MainApp.getInstance();

        Intent playerIntent = new Intent(mainApp, AudioDownloadService.class);

        //Check is service is active
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mainApp.startForegroundService(playerIntent);
        } else {
            mainApp.startService(playerIntent);
        }

        mainApp.bindService(playerIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                audioDownloadService = ((AudioDownloadServiceBinder) binder).getService();
                if(audioDownloadService!=null){
                    audioDownloadService.addNewDownload(audioItem);
                }
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                audioDownloadService = null;
            }

        }, Context.BIND_ABOVE_CLIENT);
    }

    public static AudioDownloadServiceController get(){
        if (null == instance){
            instance = AudioDownloadServiceController.getInstance();
        }
        return instance;
    }

}
