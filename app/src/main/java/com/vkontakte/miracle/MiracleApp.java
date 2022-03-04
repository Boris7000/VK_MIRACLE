package com.vkontakte.miracle;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.engine.util.SettingsUtil;
import com.vkontakte.miracle.engine.util.UIUtil;
import com.vkontakte.miracle.longpoll.LongPollServiceController;
import com.vkontakte.miracle.player.PlayerServiceController;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

public class MiracleApp extends Application {

    private FirebaseApp firebaseApp;
    private LargeDataStorage largeDataStorage;
    private SettingsUtil settingsUtil;
    private PlayerServiceController playerServiceController;
    private LongPollServiceController longPollServiceController;
    private boolean nightMode;
    private int themeRecourseId;

    @Override
    public void onCreate() {

        iniFirebaseApp();

        largeDataStorage = new LargeDataStorage();

        settingsUtil = new SettingsUtil(this);

        int currentNightMode = settingsUtil.nightMode();

        nightMode = currentNightMode!=MODE_NIGHT_NO;

        AppCompatDelegate.setDefaultNightMode(currentNightMode);

        themeRecourseId = UIUtil.getThemeRecourseId(settingsUtil.themeId());
        setTheme(themeRecourseId);

        playerServiceController = new PlayerServiceController(this);

        longPollServiceController = new LongPollServiceController(this);

        super.onCreate();
    }

    private void iniFirebaseApp(){
        firebaseApp = FirebaseApp.initializeApp(this);
    }

    public void getFCMToken(OnCompleteListener<String> onCompleteListener){
        if(firebaseApp==null){
            iniFirebaseApp();
        }
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(onCompleteListener);
    }

    /** Changing UI styles **/
    public boolean nightMode() {
        return nightMode;
    }

    public void swapNightMode(){
        changeNightMode(!nightMode);
    }

    public void changeNightMode(boolean nightMode){
        if(this.nightMode !=nightMode) {
            int MODE_NIGHT = nightMode?MODE_NIGHT_YES:MODE_NIGHT_NO;
            settingsUtil.storeNightMode(MODE_NIGHT);
            this.nightMode = nightMode;
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT);
        }
    }

    public SettingsUtil getSettingsUtil() {
        return settingsUtil;
    }

    public LargeDataStorage getLargeDataStorage(){ return largeDataStorage; }

    public int getThemeRecourseId() {
        return themeRecourseId;
    }

    public void updateThemeRecourseId(int themeId){
        themeRecourseId = UIUtil.getThemeRecourseId(themeId);
        setTheme(themeRecourseId);
    }

    public PlayerServiceController getPlayerServiceController(){
        return playerServiceController;
    }

    public LongPollServiceController getLongPollServiceController() {
        return longPollServiceController;
    }
}
