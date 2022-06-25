package com.vkontakte.miracle.login;

import static com.vkontakte.miracle.engine.util.DeviceUtil.getDeviceId;
import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Creator.account;

import android.content.Context;
import android.util.Log;

import com.vkontakte.miracle.engine.async.AsyncExecutor;

public class UnregisterDevice extends AsyncExecutor<Boolean> {

    public static final String LOGIN_TAG = "UnregisterDevice";
    private final String token;
    private final Context context;

    public UnregisterDevice(Context context, String token){
        this.context = context;
        this.token = token;
    }
    @Override
    public Boolean inBackground() {
        try {
            account().unregisterDevice(getDeviceId(context), token, latest_api_v).execute();
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
