package com.miracle.engine.util;

import static android.content.Context.MODE_PRIVATE;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static com.miracle.engine.util.UIUtil.THEME_BLUE;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemeSettingsUtil {

    private final SharedPreferences preferences;

    public ThemeSettingsUtil(Context context){
        String STORAGE = "com.miracle.tester.THEME_STORAGE";
        preferences = context.getSharedPreferences(STORAGE,MODE_PRIVATE);
    }

    public void storeNightMode(int nightMode){
        preferences.edit().putInt("nightMode",nightMode).apply();
    }

    public int nightMode(){
        return preferences.getInt("nightMode", MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void exstoreThemeId(int resId){
        preferences.edit().putInt("themeId",resId).apply();
    }

    public int themeId(){
        return preferences.getInt("themeId", THEME_BLUE);
    }

    public void storeThemeId(int resId){
        preferences.edit().putInt("themeId", resId).apply();
    }

}
