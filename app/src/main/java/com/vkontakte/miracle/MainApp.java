package com.vkontakte.miracle;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.miracle.engine.MiracleApp;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.engine.util.SettingsUtil;
import com.vkontakte.miracle.engine.util.StatisticsController;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.service.player.AudioPlayerSettingsUtil;
import com.vkontakte.miracle.service.longpoll.LongPollServiceController;
import com.vkontakte.miracle.service.player.AudioPlayerServiceController;

public class MainApp extends MiracleApp {

    private FirebaseApp firebaseApp;
    private static MainApp instance;

    @Override
    public void onCreate() {

        instance = this;

        iniFirebaseApp();

        initInstances();

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

    private void initInstances(){
        SettingsUtil.getInstance();

        StorageUtil.getInstance();

        LargeDataStorage.getInstance();

        Picasso.get();

        StatisticsController.getInstance();

        AudioPlayerSettingsUtil.getInstance();

        AudioPlayerServiceController.getInstance();

        LongPollServiceController.getInstance();
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

    public static MainApp getInstance(){
        MainApp localInstance = instance;
        if (localInstance == null) {
            synchronized (MainApp.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new MainApp();
                }
            }
        }
        return localInstance;
    }

    @Override
    public void updateThemeRecourseId(int themeId) {
        super.updateThemeRecourseId(themeId);
        AudioPlayerServiceController.get().updateTheme();
    }
}
