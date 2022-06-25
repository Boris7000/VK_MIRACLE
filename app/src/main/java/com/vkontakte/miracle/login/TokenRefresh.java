package com.vkontakte.miracle.login;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;
import static com.vkontakte.miracle.network.Constants.defaultTokenRefreshFields;
import static com.vkontakte.miracle.network.Constants.defaultTokenRefreshFields2;
import static com.vkontakte.miracle.network.Constants.fake_receipt;
import static com.vkontakte.miracle.network.Creator.account;

import android.util.Log;
import android.view.View;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.async.AsyncExecutor;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Response;

public class TokenRefresh extends AsyncExecutor<Boolean> {

    public static final String LOGIN_TAG = "TokenRefresh";
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

            Response<JSONObject> response;

            TokenOfficialVK tokenOfficialVK = new TokenOfficialVK();

            List<String> gms = tokenOfficialVK.requestToken();

            long timestamp = System.currentTimeMillis();

            if(gms!=null&&gms.size()==2) {

                Log.d(LOGIN_TAG, gms.get(0));
                Log.d(LOGIN_TAG, gms.get(1));

                response = account().authRefreshToken(authState.getToken(),
                        gms.get(0), gms.get(1), tokenOfficialVK.getNonce(timestamp), timestamp,
                        defaultTokenRefreshFields2()).execute();
            } else {
                response = account().authRefreshToken(authState.getToken(),
                        fake_receipt, defaultTokenRefreshFields()).execute();
            }

            JSONObject jsonObject = validateBody(response).getJSONObject("response");
            Log.d(LOGIN_TAG, authState.getToken());
            authState.setToken(jsonObject.getString("token"));
            Log.d(LOGIN_TAG, authState.getToken());
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
