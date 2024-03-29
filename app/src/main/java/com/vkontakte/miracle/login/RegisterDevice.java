package com.vkontakte.miracle.login;

import static com.vkontakte.miracle.engine.util.DeviceUtil.getDeviceId;
import static com.vkontakte.miracle.network.Constants.defaultRegisterDeviceFields;
import static com.vkontakte.miracle.network.Creator.account;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.MainActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.util.SettingsUtil;
import com.vkontakte.miracle.engine.util.UserDataUtil;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.service.longpoll.LongPollServiceController;

public class RegisterDevice extends AsyncExecutor<Boolean> {

    public static final String LOGIN_TAG = "RegisterDevice";
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
            account().registerDevice(authState.getReceipt(),1, getDeviceId(loginActivity),
                    null, authState.getToken(), defaultRegisterDeviceFields()).execute();
            loginActivity.setText(loginActivity.getString(R.string.userDataUpdate));

            User user = UserDataUtil.downloadUserData(authState.getUserId(), authState.getToken());
            UserDataUtil.updateUserData(user);

            return true;
        } catch (Exception e) {
            String eString = e.toString();
            loginActivity.setText(eString.substring(eString.indexOf("Exception:")+11));
            loginActivity.setProgressBarVisibility(View.GONE);
            Log.d(LOGIN_TAG,eString);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onExecute(Boolean object) {
        if(object){
            SettingsUtil.get().storeAuthorized(true);
            LongPollServiceController.get().startExecuting();
            Intent intent = new Intent(loginActivity, MainActivity.class);
            loginActivity.startActivity(intent);
            loginActivity.finish();
            loginActivity.overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
        } else {
            loginActivity.setCanLogin(true);
        }
    }
}