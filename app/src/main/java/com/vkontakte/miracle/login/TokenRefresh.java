package com.vkontakte.miracle.login;

import static com.vkontakte.miracle.engine.util.LogTags.LOGIN_TAG;
import static com.vkontakte.miracle.engine.util.NetworkUtil.CheckConnection;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;
import static com.vkontakte.miracle.network.Constants.defaultHeaders;
import static com.vkontakte.miracle.network.Constants.defaultTokenRefreshFields;
import static com.vkontakte.miracle.network.Creator.account;

import android.util.Log;
import android.view.View;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.async.AsyncExecutor;

import org.json.JSONObject;

import retrofit2.Response;

public class TokenRefresh extends AsyncExecutor<Boolean> {

    private final LoginActivity loginActivity;
    private final AuthState authState;

    public TokenRefresh(AuthState authState, LoginActivity loginActivity){
        this.authState = authState;
        this.loginActivity = loginActivity;
        loginActivity.setText(loginActivity.getString(R.string.tokenRefresh));
    }

    @Override
    public Boolean inBackground() {
        try {
            CheckConnection(3500);
            Response<JSONObject> response = account().authRefreshToken(authState.getToken(),
                    authState.getReceipt(), defaultTokenRefreshFields(), defaultHeaders()).execute();
            JSONObject jsonObject = validateBody(response).getJSONObject("response");
            authState.setToken(jsonObject.getString("token"));
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
            new RegisterDevice(authState, loginActivity).start();
        } else {
            loginActivity.setCanLogin(true);
        }
    }
}
