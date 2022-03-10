package com.vkontakte.miracle.longpoll;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;
import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Creator.longPoll;
import static com.vkontakte.miracle.network.Creator.message;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.longpoll.listeners.OnNewUpdatesListener;

import org.json.JSONObject;

import java.io.InterruptedIOException;

import retrofit2.Call;
import retrofit2.Response;

public class LongPollService extends Service {

    private final IBinder iBinder = new LongPollServiceBinder(this);
    private LongPollServiceController longPollServiceController;
    private ProfileItem profileItem;
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
                            Response<JSONObject> response;
                            JSONObject jsonObject;

                            if(server==null){
                                response = message().getLongpollServer(1,3,profileItem.getAccessToken(),latest_api_v).execute();
                                jsonObject = validateBody(response).getJSONObject("response");
                                server = jsonObject.getString("server").substring(11);
                                key = jsonObject.getString("key");
                                ts = jsonObject.getString("ts");
                            }

                            Call<JSONObject> call = longPoll().request(server,"a_check",key,ts,30,2 + 8 + 64 + 128,3);

                            response = call.execute();

                            jsonObject = validateBody(response);

                            LongPollUpdates longPollUpdates = new LongPollUpdates(jsonObject.getJSONArray("updates"));

                            ts = jsonObject.getString("ts");

                            StorageUtil.writeMessageAddedLongPollUpdates(longPollUpdates.getMessageAddedUpdates(),getApplicationContext());

                            StorageUtil.writeMessageReadLongPollUpdates(longPollUpdates.getMessageReadUpdates(),getApplicationContext());

                            return longPollUpdates;
                        } catch (InterruptedIOException ignore){
                            return new LongPollUpdates();
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
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
        super.onCreate();
        MiracleApp miracleApp = (MiracleApp) getApplication();
        longPollServiceController = miracleApp.getLongPollServiceController();
        profileItem = StorageUtil.loadUsers(miracleApp).get(0);
    }

    @Override
    public void onDestroy() {
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
