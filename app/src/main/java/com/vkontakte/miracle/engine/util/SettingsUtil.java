package com.vkontakte.miracle.engine.util;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_BLUE;

import com.google.android.exoplayer2.Player;
import com.vkontakte.miracle.MiracleApp;


public class SettingsUtil {

    private final SharedPreferences preferences;
    private static SettingsUtil instance;

    public SettingsUtil(Context context){
        instance = this;
        String STORAGE = "com.example.vk_air.music.STORAGE";
        preferences =context.getSharedPreferences(STORAGE,MODE_PRIVATE);
    }

    public static SettingsUtil getInstance(){
        return new SettingsUtil(MiracleApp.getInstance());
    }

    public void storeNightMode(int nightMode){
        preferences.edit().putInt("nightMode",nightMode).apply();
    }

    public int nightMode(){
        return preferences.getInt("nightMode", MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void storeThemeId(int resId){
        preferences.edit().putInt("themeId",resId).apply();
    }

    public int themeId(){
        return preferences.getInt("themeId", THEME_BLUE);
    }

    public boolean authorized(){
        return preferences.getBoolean("authorized", false);
    }

    public void storeAuthorized(boolean authorized){
        preferences.edit().putBoolean("authorized",authorized).apply();
    }

    public void storePlayerRepeatMode(int repeatMode){
        preferences.edit().putInt("repeatMode", repeatMode).apply();
    }

    public int playerRepeatMode(){
        return preferences.getInt("repeatMode", Player.REPEAT_MODE_OFF);
    }

    public void storePlayerChangeWallpapers(int mode){
        preferences.edit().putInt("playerChangeWallpapers", mode).apply();
    }

    public int playerChangeWallpapers(){
        return preferences.getInt("playerChangeWallpapers", 0);
    }

    public static SettingsUtil get(){
        if (null == instance){
            instance = SettingsUtil.getInstance();
        }
        return instance;
    }

}
