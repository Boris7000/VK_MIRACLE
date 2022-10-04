package com.vkontakte.miracle.service.downloads.audio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.model.audio.AudioItem;

public class AudioEraseServiceController {

    private static AudioEraseServiceController instance;

    private AudioEraseService audioEraseService;

    public AudioEraseServiceController(){
        instance = this;
    }

    public static AudioEraseServiceController getInstance(){
        return new AudioEraseServiceController();
    }

    public void addErase(AudioItem audioItem){
        if(audioEraseService !=null) {
            audioEraseService.addNewErase(audioItem);
        } else {
            startAndErase(audioItem);
        }
    }

    private void startAndErase(AudioItem audioItem){

        MiracleApp miracleApp = MiracleApp.getInstance();

        Intent playerIntent = new Intent(miracleApp, AudioEraseService.class);

        //Check is service is active
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            miracleApp.startForegroundService(playerIntent);
        } else {
            miracleApp.startService(playerIntent);
        }

        miracleApp.bindService(playerIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                audioEraseService = ((AudioEraseServiceBinder) binder).getService();
                if(audioEraseService!=null){
                    audioEraseService.addNewErase(audioItem);
                }
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                audioEraseService = null;
            }

        }, Context.BIND_ABOVE_CLIENT);
    }

    public static AudioEraseServiceController get(){
        if (null == instance){
            instance = AudioEraseServiceController.getInstance();
        }
        return instance;
    }

}
