package com.vkontakte.miracle.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.vkontakte.miracle.R;

public class AppNotificationChannels {

    public static final String AUDIO_CHANNEL_ID = "audio_channel";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static NotificationChannel getAudioChannel(Context context) {
        String channelName = context.getString(R.string.audio_channel);
        NotificationChannel channel = new NotificationChannel(AUDIO_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW);
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        return channel;
    }

}
