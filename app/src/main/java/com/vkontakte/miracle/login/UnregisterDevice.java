package com.vkontakte.miracle.login;

import static com.vkontakte.miracle.engine.util.DeviceUtil.getDeviceId;
import static com.vkontakte.miracle.engine.util.NetworkUtil.CheckConnection;
import static com.vkontakte.miracle.network.Constants.defaultHeaders;
import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Creator.account;

import android.content.Context;
import android.util.Log;

import com.vkontakte.miracle.engine.async.AsyncExecutor;

public class UnregisterDevice extends AsyncExecutor<Boolean> {

    public static final String LOGIN_TAG = "UnregisterDevice";
    private final String token;
    private final Context context;
    public UnregisterDevice(String token, Context context){
        this.token = token;
        this.context = context;
    }
    @Override
    public Boolean inBackground() {
        try {
            CheckConnection(3500);
            account().unregisterDevice(getDeviceId(context), token, latest_api_v, defaultHeaders()).execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            String eString = e.toString();
            Log.d(LOGIN_TAG,eString);
        }
        return false;
    }
    @Override
    public void onExecute(Boolean object) { }
}
