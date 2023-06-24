package com.miracle.engine;

import static android.content.res.Configuration.UI_MODE_NIGHT_MASK;
import static android.content.res.Configuration.UI_MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.miracle.engine.util.LargeDataStorage;
import com.miracle.engine.util.ThemeSettingsUtil;
import com.miracle.engine.util.UIUtil;

public class MiracleApp extends Application {

    private boolean nightMode;
    private int themeRecourseId;
    private int themeId;
    private static MiracleApp instance;
    private ThemeSettingsUtil themeSettingsUtil;

    @Override
    public void onCreate() {

        instance = this;

        themeSettingsUtil = new ThemeSettingsUtil(this);

        int currentNightMode = themeSettingsUtil.nightMode();

        if(currentNightMode==MODE_NIGHT_FOLLOW_SYSTEM){
            nightMode = (getResources().getConfiguration().uiMode & UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES;
        } else {
            nightMode = currentNightMode==MODE_NIGHT_YES;
        }

        AppCompatDelegate.setDefaultNightMode(currentNightMode);
        setTheme(themeRecourseId = UIUtil.getThemeRecourseId(themeId = themeSettingsUtil.themeId()));

        LargeDataStorage.getInstance();

        super.onCreate();
    }

    /** Changing UI styles **/
    public boolean nightMode() {
        return nightMode;
    }

    public void swapNightMode(){
        changeNightMode(!nightMode);
    }

    public void changeNightMode(boolean nightMode){
        if(this.nightMode!=nightMode) {
            this.nightMode = nightMode;
            int NIGHT_MODE = nightMode?MODE_NIGHT_YES:MODE_NIGHT_NO;
            themeSettingsUtil.storeNightMode(NIGHT_MODE);
            AppCompatDelegate.setDefaultNightMode(NIGHT_MODE);
        }
    }

    public int getThemeId() {
        return themeId;
    }

    public int getThemeRecourseId() {
        return themeRecourseId;
    }

    public void updateThemeRecourseId(int themeId){
        themeSettingsUtil.storeThemeId(themeId);
        this.themeId = themeId;
        themeRecourseId = UIUtil.getThemeRecourseId(themeId);
        setTheme(themeRecourseId);
    }

    public static MiracleApp getInstance(){
        MiracleApp localInstance = instance;
        if (localInstance == null) {
            synchronized (MiracleApp.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new MiracleApp();
                }
            }
        }
        return localInstance;
    }

}