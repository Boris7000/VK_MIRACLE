package com.vkontakte.miracle.service.longpoll;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;
import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Creator.longPoll;
import static com.vkontakte.miracle.network.Creator.message;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.service.longpoll.listeners.OnNewUpdatesListener;

import org.json.JSONObject;

import java.io.File;
import java.io.InterruptedIOException;

import retrofit2.Call;
import retrofit2.Response;

public class LongPollService extends Service {

    private final String LOG_TAG = "LongPollService";

    private final IBinder iBinder = new LongPollServiceBinder(this);
    private final LongPollServiceController longPollServiceController = LongPollServiceController.get();
    private final StorageUtil storageUtil = StorageUtil.get();
    private AsyncExecutor<LongPollUpdates> executor;
    private boolean destroyed = false;

    private String server;
    private String key;
    private String ts;

    public void startExecuting(){
        if(!destroyed&&(executor == null || executor.workIsDone())) {
            (executor = new AsyncExecutor<LongPollUpdates>() {
                @Override
                public LongPollUpdates inBackground() {
                    try {
                        try {
                            User user = storageUtil.currentUser();
                            if(user!=null) {
                                Log.d(LOG_TAG, user.getId());
                                Response<JSONObject> response;
                                JSONObject jsonObject;

                                if (server == null) {
                                    response = message().getLongPollServer(1, 3, user.getAccessToken(), latest_api_v).execute();
                                    jsonObject = validateBody(response).getJSONObject("response");
                                    server = jsonObject.getString("server").substring(11);
                                    key = jsonObject.getString("key");
                                    ts = jsonObject.getString("ts");
                                }

                                Call<JSONObject> call = longPoll().request(server, "a_check", key, ts, 30, 2 + 8 + 64 + 128, 3);

                                response = call.execute();

                                jsonObject = validateBody(response);

                                Log.d(LOG_TAG, jsonObject.toString());

                                LongPollUpdates longPollUpdates = new LongPollUpdates(jsonObject.getJSONArray("updates"));

                                ts = jsonObject.getString("ts");

                                File userDirectory = storageUtil.getUserCachesDir(user);

                                storageUtil.writeMessageAddedLongPollUpdates(longPollUpdates.getMessageAddedUpdates(), userDirectory);

                                storageUtil.writeMessageReadLongPollUpdates(longPollUpdates.getMessageReadUpdates(), userDirectory);

                                if (user.equals(storageUtil.currentUser())) {
                                    return longPollUpdates;
                                }
                                Log.d(LOG_TAG, "profiles not equals");
                                destroyed = true;
                            }
                        } catch (InterruptedIOException ignore){ }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Log.d(LOG_TAG, exception.getMessage());
                    }
                    return new LongPollUpdates();
                }

                @Override
                public void onExecute(LongPollUpdates longPollUpdates) {
                    if(!destroyed) {
                        OnNewUpdatesListener onNewUpdatesListener = longPollServiceController.getOnNewUpdatesListener();
                        if (!longPollUpdates.getMessageAddedUpdates().isEmpty()) {
                            onNewUpdatesListener.onMessageAddedUpdates(longPollUpdates.getMessageAddedUpdates());
                        }
                        if (!longPollUpdates.getUserOnlineUpdates().isEmpty()) {
                            onNewUpdatesListener.onUserOnlineUpdates(longPollUpdates.getUserOnlineUpdates());
                        }
                        if (!longPollUpdates.getWriteTextInDialogUpdates().isEmpty()) {
                            onNewUpdatesListener.onMessageTypingUpdates(longPollUpdates.getWriteTextInDialogUpdates());
                        }
                        if (!longPollUpdates.getMessageReadUpdates().isEmpty()) {
                            onNewUpdatesListener.onMessageReadUpdates(longPollUpdates.getMessageReadUpdates());
                        }
                        startExecuting();
                    }
                }
            }).start();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG,"created");
        super.onCreate();
    }

    @Override
    public void onDestroy() {

        Log.d(LOG_TAG,"destroyed");

        super.onDestroy();

        destroyed = true;

        if(executor!=null) {
            executor.interrupt();
            executor = null;
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
