package com.vkontakte.miracle.service.downloads.audio;

import static com.vkontakte.miracle.notification.AppNotificationChannels.AUDIO_DOWNLOAD_CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.miracle.engine.async.AsyncExecutor;
import com.miracle.engine.async.ExecutorConveyor;
import com.vkontakte.miracle.executors.audio.DownloadAudio;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.notification.AppNotificationChannels;

public class AudioDownloadService extends Service {

    public static final String LOG_TAG = "AudioDownloadService";
    private final IBinder iBinder = new AudioDownloadServiceBinder(this);

    private ExecutorConveyor<Boolean> conveyor = new ExecutorConveyor<>();
    private boolean foregroundStarted = false;
    private boolean destroyed = false;
    private int progress = 0;


    //Notification
    NotificationCompat.Builder currentBuilder;
    private NotificationManager notificationManager;
    private final int NOTIFICATION_ID = -104;


    private NotificationCompat.Builder createNotificationBuilder(AudioItem audioItem){

        int smallIcon= android.R.drawable.stat_sys_download;

        return new NotificationCompat.Builder(this, AUDIO_DOWNLOAD_CHANNEL_ID)
                .setSmallIcon(smallIcon)
                .setContentTitle(audioItem.getArtist()+" - "+audioItem.getTitle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setShowWhen(false)
                .setChannelId(AUDIO_DOWNLOAD_CHANNEL_ID);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = AppNotificationChannels.getAudioDownloadChannel(this);
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }

    public void addNewDownload(AudioItem audioItem){

        if(destroyed){
            return;
        }

        for (AsyncExecutor<Boolean> asyncExecutor: conveyor.getAsyncExecutors()){
            DownloadAudio downloadAudio = (DownloadAudio) asyncExecutor;
            if(downloadAudio.getAudioItem().equals(audioItem)){
                return;
            }
        }

        DownloadAudio downloadAudio = new DownloadAudio(audioItem);

        NotificationCompat.Builder builder = createNotificationBuilder(audioItem);

        downloadAudio.addOnStartListener(asyncExecutor -> {
            this.progress = 0;
            currentBuilder = builder;
            updateCounter(currentBuilder);
            builder.setProgress(100, 0, false);
            sendNotification(currentBuilder);
        });

        downloadAudio.setOnProgressListener(progress -> {
            if(this.progress!=progress) {
                int diff = progress-this.progress;
                if(diff>=10) {
                    this.progress = progress;
                    builder.setProgress(100, progress, false);
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                }
            }
        });

        conveyor.addAsyncExecutor(downloadAudio);

        if(currentBuilder!=null) {
            updateCounter(currentBuilder);
            sendNotification(currentBuilder);
        } else {
            currentBuilder = builder;
        }

        downloadAudio.addOnExecuteListener(asyncExecutor -> {
            if(conveyor.getAsyncExecutors().isEmpty()){
                stopSelf();
            }
        });

    }

    private void updateCounter(NotificationCompat.Builder builder){
        if (conveyor.getAsyncExecutors().size()>1) {
            builder.setSubText("И еще " + (conveyor.getAsyncExecutors().size() - 1));
        }
    }

    private void sendNotification(NotificationCompat.Builder builder){
        Notification notification = builder.build();
        if(!foregroundStarted){
            foregroundStarted = true;
            startForeground(NOTIFICATION_ID, notification);
        } else {
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        destroyed = true;


        if(conveyor!=null) {
            conveyor.release();
            conveyor = null;
        }

        stopForeground(true);
        notificationManager.cancel(NOTIFICATION_ID);
    }

}
