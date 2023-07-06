package com.vkontakte.miracle.service.player;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;

import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.common.Player.RepeatMode;
import com.vkontakte.miracle.MainApp;

public class AudioPlayerSettingsUtil {

    private static AudioPlayerSettingsUtil instance;

    private final SharedPreferences preferences;

    public AudioPlayerSettingsUtil(Context context) {
        instance = this;
        this.preferences = context.getSharedPreferences(
                "com.vkontakte.miracle.PLAYER_PREFERENCES", MODE_PRIVATE);
    }

    public static AudioPlayerSettingsUtil getInstance(){
        return new AudioPlayerSettingsUtil(MainApp.getInstance());
    }

    public static AudioPlayerSettingsUtil get(){
        AudioPlayerSettingsUtil localInstance = instance;
        if (localInstance == null) {
            synchronized (AudioPlayerSettingsUtil.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = getInstance();
                }
            }
        }
        return localInstance;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void storeRepeatMode(@RepeatMode int repeatMode){
        preferences.edit().putInt("playerRepeatMode", repeatMode).apply();
    }

    public int getRepeatMode(){
        return preferences.getInt("playerRepeatMode", ExoPlayer.REPEAT_MODE_OFF);
    }

    ///////////////////////////////////////////////////////////////////////////////////

    public void storeReturnPlaybackTime(@IntRange(from = 0, to = 15) int returnPlaybackTime){
        preferences.edit().putInt("returnPlaybackTime", returnPlaybackTime).apply();
    }

    public int getReturnPlaybackTime(){
        return preferences.getInt("returnPlaybackTime", 5);
    }

    ///////////////////////////////////////////////////////////////////////////////////

    public void storeChangeWallpapers(boolean enabled){
        preferences.edit().putBoolean("playerChangeWallpapers", enabled).apply();
    }

    public boolean getChangeWallpapers(){
        return preferences.getBoolean("playerChangeWallpapers", true);
    }

    ///////////////////////////////////////////////////////////////////////////////////

    public void storePlaybackStatisticsPercent(@FloatRange(from = 0f, to = 1f) float playbackStatisticsPercent){
        preferences.edit().putFloat("playbackStatisticsPercent", playbackStatisticsPercent).apply();
    }

    public float getPlaybackStatisticsPercent(){
        return preferences.getFloat("playbackStatisticsPercent", 0.5f);
    }

    ///////////////////////////////////////////////////////////////////////////////////


}
