package com.vkontakte.miracle.service.longpoll;

import androidx.collection.ArrayMap;

import com.vkontakte.miracle.service.longpoll.listeners.OnMessageAddedUpdateListener;
import com.vkontakte.miracle.service.longpoll.listeners.OnMessageReadUpdateListener;
import com.vkontakte.miracle.service.longpoll.listeners.OnMessageTypingUpdateListener;
import com.vkontakte.miracle.service.longpoll.listeners.OnNewUpdatesListener;
import com.vkontakte.miracle.service.longpoll.listeners.OnUserOnlineUpdateListener;
import com.vkontakte.miracle.service.longpoll.model.MessageAddedUpdate;
import com.vkontakte.miracle.service.longpoll.model.MessageReadUpdate;
import com.vkontakte.miracle.service.longpoll.model.MessageTypingUpdate;
import com.vkontakte.miracle.service.longpoll.model.UserOnlineUpdate;

import java.util.ArrayList;

public class LongPollServiceController {

    private LongPollService longPollService;
    private final OnNewUpdatesListener onNewUpdatesListener = new OnNewUpdatesListener() {
        @Override
        public void onMessageTypingUpdates(ArrayList<MessageTypingUpdate> messageTypingUpdates) {
            for (OnMessageTypingUpdateListener onMessageTypingUpdateListener : onMessageTypingUpdateListeners) {
                onMessageTypingUpdateListener.onMessageTypingUpdate(messageTypingUpdates);
            }
        }

        @Override
        public void onUserOnlineUpdates(ArrayList<UserOnlineUpdate> userOnlineUpdates) {
            ArrayMap<String,UserOnlineUpdate> map = new ArrayMap<>();
            for (UserOnlineUpdate userOnlineUpdate: userOnlineUpdates) {
                map.put(userOnlineUpdate.getUserId(),userOnlineUpdate);
            }
            for (OnUserOnlineUpdateListener onUserOnlineUpdateListener:onUserOnlineUpdateListeners) {
                onUserOnlineUpdateListener.onUserOnlineUpdate(map);
            }
        }

        @Override
        public void onMessageAddedUpdates(ArrayList<MessageAddedUpdate> messageAddedUpdates) {
            for (OnMessageAddedUpdateListener onMessageAddedUpdateListener:onMessageAddedUpdateListeners) {
                onMessageAddedUpdateListener.onMessageAddedUpdate(messageAddedUpdates);
            }
        }

        @Override
        public void onMessageReadUpdates(ArrayList<MessageReadUpdate> messageReadUpdates) {
            for (OnMessageReadUpdateListener onMessageReadUpdateListener:onMessageReadUpdateListeners) {
                onMessageReadUpdateListener.onMessageReadUpdate(messageReadUpdates);
            }
        }

    };

    public OnNewUpdatesListener getOnNewUpdatesListener() {
        return onNewUpdatesListener;
    }

    private static LongPollServiceController instance;

    public LongPollServiceController(){
        instance = this;
    }

    public static LongPollServiceController getInstance(){
        return new LongPollServiceController();
    }

    public void startExecuting(){
        /*
        if(StorageUtil.get().currentUser()!=null) {
            if (longPollService == null || longPollService.isDestroyed()) {
                MainApp mainApp = MainApp.getInstance();
                Intent intent = new Intent(mainApp, LongPollService.class);
                //Check is service is active
                mainApp.startService(intent);
                mainApp.bindService(intent, new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder binder) {
                        longPollService = ((LongPollServiceBinder) binder).getService();
                        if (longPollService != null) {
                            longPollService.startExecuting();
                        }
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        longPollService = null;
                    }

                }, Context.BIND_ABOVE_CLIENT);
            }
        }

         */
    }

    public void actionStop(){
        if(longPollService!=null){
            longPollService.stopSelf();
        }
    }

    private final ArrayList<OnMessageTypingUpdateListener> onMessageTypingUpdateListeners = new ArrayList<>();

    public void addOnMessageTypingListener(OnMessageTypingUpdateListener onMessageTypingUpdateListener){
        onMessageTypingUpdateListeners.add(onMessageTypingUpdateListener);
    }

    public void removeOnMessageTypingListener(OnMessageTypingUpdateListener onMessageTypingUpdateListener){
        onMessageTypingUpdateListeners.remove(onMessageTypingUpdateListener);
    }

    private final ArrayList<OnUserOnlineUpdateListener> onUserOnlineUpdateListeners = new ArrayList<>();

    public void addOnUserOnlineListener(OnUserOnlineUpdateListener onUserOnlineUpdateListener){
        onUserOnlineUpdateListeners.add(onUserOnlineUpdateListener);
    }

    public void removeOnUserOnlineListener(OnUserOnlineUpdateListener onUserOnlineUpdateListener){
        onUserOnlineUpdateListeners.remove(onUserOnlineUpdateListener);
    }

    private final ArrayList<OnMessageAddedUpdateListener> onMessageAddedUpdateListeners = new ArrayList<>();

    public void addOnMessageAddedUpdateListener(OnMessageAddedUpdateListener onMessageAddedUpdateListener){
        onMessageAddedUpdateListeners.add(onMessageAddedUpdateListener);
    }

    public void removeOnMessageAddedUpdateListener(OnMessageAddedUpdateListener onMessageAddedUpdateListener){
        onMessageAddedUpdateListeners.remove(onMessageAddedUpdateListener);
    }

    private final ArrayList<OnMessageReadUpdateListener> onMessageReadUpdateListeners = new ArrayList<>();

    public void addOnMessageReadUpdateListener(OnMessageReadUpdateListener onMessageReadUpdateListener){
        onMessageReadUpdateListeners.add(onMessageReadUpdateListener);
    }

    public void removeOnMessageReadUpdateListener(OnMessageReadUpdateListener onMessageReadUpdateListener){
        onMessageReadUpdateListeners.remove(onMessageReadUpdateListener);
    }

    public static LongPollServiceController get(){
        if (null == instance){
            instance = LongPollServiceController.getInstance();
        }
        return instance;
    }

}
