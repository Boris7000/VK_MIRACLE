package com.vkontakte.miracle;

import static android.content.res.Configuration.UI_MODE_NIGHT_MASK;
import static android.content.res.Configuration.UI_MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.engine.util.SettingsUtil;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.engine.util.UIUtil;
import com.vkontakte.miracle.longpoll.LongPollServiceController;
import com.vkontakte.miracle.player.PlayerServiceController;

public class MiracleApp extends Application {

    private FirebaseApp firebaseApp;
    private boolean nightMode;
    private int themeRecourseId;
    private static MiracleApp instance;

    @Override
    public void onCreate() {

        instance = this;

        iniFirebaseApp();

        SettingsUtil settingsUtil = SettingsUtil.getInstance();

        StorageUtil.getInstance();

        LargeDataStorage.getInstance();

        Picasso.get();

        int currentNightMode = settingsUtil.nightMode();

        if(currentNightMode==MODE_NIGHT_FOLLOW_SYSTEM){
            nightMode = (getResources().getConfiguration().uiMode & UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES;
        } else {
            nightMode = currentNightMode==MODE_NIGHT_YES;
        }

        AppCompatDelegate.setDefaultNightMode(currentNightMode);

        themeRecourseId = UIUtil.getThemeRecourseId(settingsUtil.themeId());
        setTheme(themeRecourseId);

        PlayerServiceController.getInstance();

        LongPollServiceController.getInstance();

        ProcessLifecycleOwner.get().getLifecycle().addObserver(new DefaultLifecycleObserver(){
            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                LongPollServiceController.get().startExecuting();
            }

            @Override
            public void onPause(@NonNull LifecycleOwner owner) {
                LongPollServiceController.get().actionStop();
            }
        });

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
        if(this.nightMode!=nightMode) {
            int NIGHT_MODE = nightMode?MODE_NIGHT_YES:MODE_NIGHT_NO;
            SettingsUtil.get().storeNightMode(NIGHT_MODE);
            this.nightMode = nightMode;
            AppCompatDelegate.setDefaultNightMode(NIGHT_MODE);
        }
    }

    public int getThemeRecourseId() {
        return themeRecourseId;
    }

    public void updateThemeRecourseId(int themeId){
        SettingsUtil.get().storeThemeId(themeId);
        themeRecourseId = UIUtil.getThemeRecourseId(themeId);
        setTheme(themeRecourseId);
    }

    public static MiracleApp getInstance(){
        if (null == instance){
            instance = new MiracleApp();
        }
        return instance;
    }

}
