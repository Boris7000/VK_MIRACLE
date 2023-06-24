package com.vkontakte.miracle.engine.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.vkontakte.miracle.MainApp;


public class SettingsUtil {

    private final SharedPreferences preferences;
    private static SettingsUtil instance;

    public SettingsUtil(Context context){
        instance = this;
        //TODO исправить на com.vkontakte.miracle.STORAGE
        String STORAGE = "com.example.vk_air.music.STORAGE";
        preferences = context.getSharedPreferences(STORAGE, MODE_PRIVATE);
    }

    public static SettingsUtil getInstance(){
        return new SettingsUtil(MainApp.getInstance());
    }

    public boolean authorized(){
        return preferences.getBoolean("authorized", false);
    }

    public void storeAuthorized(boolean authorized){
        preferences.edit().putBoolean("authorized",authorized).apply();
    }

    public static SettingsUtil get(){
        SettingsUtil localInstance = instance;
        if (localInstance == null) {
            synchronized (SettingsUtil.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = getInstance();
                }
            }
        }
        return localInstance;
    }

}
