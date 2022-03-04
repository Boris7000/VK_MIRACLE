package com.vkontakte.miracle.login;

import static com.vkontakte.miracle.engine.util.DeviceUtil.getDeviceId;
import static com.vkontakte.miracle.engine.util.LogTags.LOGIN_TAG;
import static com.vkontakte.miracle.engine.util.NetworkUtil.CheckConnection;
import static com.vkontakte.miracle.network.Constants.defaultHeaders;
import static com.vkontakte.miracle.network.Constants.defaultRegisterDeviceFields;
import static com.vkontakte.miracle.network.Creator.account;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.UserDataUtil;
import com.vkontakte.miracle.model.users.ProfileItem;

public class RegisterDevice extends AsyncExecutor<Boolean> {

    private final AuthState authState;
    private final LoginActivity loginActivity;

    public RegisterDevice(AuthState authState, LoginActivity loginActivity){
        this.authState = authState;
        this.loginActivity = loginActivity;
        loginActivity.setText(loginActivity.getString(R.string.registerDevice));
        loginActivity.setProgressBarVisibility(View.VISIBLE);
    }

    @Override
    public Boolean inBackground() {
        try {
            CheckConnection(3500);
            account().registerDevice(authState.getReceipt(),1, getDeviceId(loginActivity),
                    null, authState.getToken(), defaultRegisterDeviceFields(), defaultHeaders()).execute();
            loginActivity.setText(loginActivity.getString(R.string.userDataUpdate));
            ProfileItem profileItem = UserDataUtil.downloadUserData(authState.getUserId(), authState.getToken());
            UserDataUtil.updateUserData(profileItem, loginActivity);
            return true;
        } catch (Exception e) {
            String eString = e.toString();
            loginActivity.setText(eString.substring(eString.indexOf("Exception:")+11));
            loginActivity.setProgressBarVisibility(View.GONE);
            Log.d(LOGIN_TAG,eString);
        }
        return false;
    }

    @Override
    public void onExecute(Boolean object) {
        if(object){
            loginActivity.getMiracleApp().getSettingsUtil().storeAuthorized(true);
            Intent intent = new Intent(loginActivity, MiracleActivity.class);
            loginActivity.startActivity(intent);
            loginActivity.finish();
            loginActivity.overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
        } else {
            loginActivity.setCanLogin(true);
        }
    }
}