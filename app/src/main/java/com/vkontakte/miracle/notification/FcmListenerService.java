package com.vkontakte.miracle.notification;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FcmListenerService  extends FirebaseMessagingService {

    private static final String TAG = "rijirjgirg";

    @SuppressLint("CheckResult")
    @WorkerThread
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, s);
    }

    @Override
    @WorkerThread
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

    }

}