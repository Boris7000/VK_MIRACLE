package com.vkontakte.miracle.service.downloads.audio;

import static com.vkontakte.miracle.notification.AppNotificationChannels.AUDIO_DOWNLOAD_CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.vkontakte.miracle.MainActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.async.ExecutorConveyor;
import com.vkontakte.miracle.executors.audio.DownloadAudio;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.notification.AppNotificationChannels;

import java.util.ArrayList;

public class AudioDownloadService extends Service {

    public static final String LOG_TAG = "AudioDownloadService";
    private final IBinder iBinder = new AudioDownloadServiceBinder(this);

    private ExecutorConveyor<Boolean> conveyor = new ExecutorConveyor<>();
    private boolean foregroundStarted = false;
    private boolean destroyed = false;
    private int progress = 0;


    //Notification
    private NotificationManager notificationManager;
    private final int NOTIFICATION_ID = -104;


    private void sendNotification(AudioItem audioItem, int progress){
        Notification notification = createNotification(audioItem, progress);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private Notification createNotification(AudioItem audioItem, int progress){

        int smallIcon=R.drawable.ic_download_28;

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //intent.putExtra("PlayerBottomSheetExpanded", true);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

        ArrayList<AsyncExecutor<Boolean>> asyncExecutors = conveyor.getAsyncExecutors();
        String subtext;

        if(asyncExecutors.size()<=1){
            subtext = "Скачивание";
        } else {
            subtext = "Скачивание (еще "+(asyncExecutors.size()-1)+")";
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, AUDIO_DOWNLOAD_CHANNEL_ID)
                .setSmallIcon(smallIcon)
                .setContentTitle(audioItem.getArtist()+" - "+audioItem.getTitle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setShowWhen(false)
                .setProgress(100, progress,false)
                .setSubText(subtext)
                .setContentIntent(contentIntent)
                .setChannelId(AUDIO_DOWNLOAD_CHANNEL_ID);

        return notificationBuilder.build();
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIncomingAction(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleIncomingAction(Intent playbackAction){

        if (destroyed||conveyor==null||
                playbackAction==null||playbackAction.getAction()==null){
            return;
        }

        String actionString = playbackAction.getAction();

        /*
        switch (actionString){
            case ACTION_PLAY:{
                transportControls.play();
                break;
            }
            case ACTION_PAUSE:{
                transportControls.pause();
                break;
            }
            case ACTION_STOP:{
                transportControls.stop();
                break;
            }
            case ACTION_NEXT:{
                skipToNext();
                break;
            }
            case ACTION_PREVIOUS:{
                skipToPrevious();
                break;
            }
            case ACTION_SEEK_TO:{
                seekTo(playbackAction.getLongExtra("position",0));
                break;
            }
            case ACTION_CHANGE_REPEAT:{
                changeRepeatMode(playbackAction.getIntExtra("repeatMode", Player.REPEAT_MODE_OFF));
                break;
            }
        }

         */
    }

    public void addNewDownload(AudioItem audioItem){
        for (AsyncExecutor<Boolean> asyncExecutor: conveyor.getAsyncExecutors()){
            DownloadAudio downloadAudio = (DownloadAudio) asyncExecutor;
            if(downloadAudio.getAudioItem().equals(audioItem)){
                return;
            }
        }

        DownloadAudio downloadAudio = new DownloadAudio(audioItem);

        if(!foregroundStarted){
            foregroundStarted = true;
            Notification notification = createNotification(audioItem, 0);
            startForeground(NOTIFICATION_ID, notification);
            notificationManager.notify(NOTIFICATION_ID, notification);
        } else {
            downloadAudio.addOnStartListener(asyncExecutor -> {
                progress = 0;
                sendNotification(audioItem, 0);
            });
        }

        downloadAudio.setOnProgressListener(progress -> {
            if(this.progress!=progress) {
                int diff = progress-this.progress;
                if(diff>=20) {
                    this.progress = progress;
                    sendNotification(downloadAudio.getAudioItem(), progress);
                }
            }
        });

        conveyor.addAsyncExecutor(downloadAudio);

        downloadAudio.addOnExecuteListener(asyncExecutor -> {
            if(conveyor.getAsyncExecutors().isEmpty()){
                stopSelf();
            }
        });

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
