package com.vkontakte.miracle.login;

import static android.view.View.GONE;
import static com.vkontakte.miracle.engine.util.DeviceUtil.getDeviceId;
import static com.vkontakte.miracle.login.AuthState.STATE_NEED_CAPTCHA;
import static com.vkontakte.miracle.login.AuthState.STATE_NEED_VALIDATION;
import static com.vkontakte.miracle.network.Constants.defaultLoginFields;
import static com.vkontakte.miracle.network.Constants.defaultValidatePhoneFields;
import static com.vkontakte.miracle.network.Creator.oauth;
import static com.vkontakte.miracle.network.Creator.oauthMethod;

import android.util.Log;
import android.view.View;

import com.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.R;

import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class Authentication extends AsyncExecutor<Boolean> {

    public static final String LOGIN_TAG = "Authentication";
    private final LoginActivity loginActivity;
    private final AuthState authState;

    public Authentication(AuthState authState, LoginActivity loginActivity){
        this.authState = authState;
        this.loginActivity= loginActivity;
    }

    @Override
    public Boolean inBackground() {
        try {
            process();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            String eString = e.toString();
            loginActivity.setText(eString.substring(eString.indexOf("Exception:")+11));
            Log.d(LOGIN_TAG,eString);
        }
        return false;
    }

    public void process() throws Exception {
        Call<JSONObject> call;
        Response<JSONObject> response;

        call = processCall();

        response = call.execute();

        processResponse(response);
    }

    public Call<JSONObject> processCall(){
        if(authState.getValidationSid()!=null){
            String validationCode = authState.getValidationCode();
            if(validationCode!=null){
                loginActivity.setText(loginActivity.getString(R.string.sendingCode));
                loginActivity.setProgressBarVisibility(View.VISIBLE);
                return sendValidationCode();
            } else {
                loginActivity.setText(loginActivity.getString(R.string.forceCode));
                loginActivity.setProgressBarVisibility(View.VISIBLE);
                return validatePhone();
            }
        } else {
            if(authState.getCaptchaSid()!=null){
                loginActivity.setText(loginActivity.getString(R.string.sendingCaptcha));
                loginActivity.setProgressBarVisibility(View.VISIBLE);
                return sendCaptcha();
            }else {
                loginActivity.setText(loginActivity.getString(R.string.authentication));
                loginActivity.setProgressBarVisibility(View.VISIBLE);
                return requestToken();
            }
        }
    }

    public void processResponse(Response<JSONObject> response) throws Exception {
        if(response.errorBody()!=null){
            checkError(new JSONObject(response.errorBody().string()));
        } else {
            if(response.body()!=null){
                checkResponse(response.body());
            }
        }
    }

    private void checkResponse(JSONObject jsonObject) throws Exception {
        Log.d(LOGIN_TAG, jsonObject.toString());

        if (jsonObject.has("access_token")){
            authState.setToken(jsonObject.getString("access_token"));
            authState.setUserId(jsonObject.getString("user_id"));
            authState.setState(AuthState.STATE_SUCCESS);
        }else {
            if(jsonObject.has("response")){
                jsonObject = jsonObject.getJSONObject("response");
                if(jsonObject.has("validation_type")){
                    authState.updateForceCode(jsonObject);
                }
            } else {
                if(jsonObject.has("error")){
                    JSONObject errorObject = jsonObject.getJSONObject("error");
                    int error_code = errorObject.getInt("error_code");
                    switch (error_code){
                        case 103:{
                            authState.setForceCodeUnableReason(AuthState.VALIDATION_CODE_RESENDS_LIMIT);
                            break;
                        }
                        case 1112:{
                            authState.setForceCodeUnableReason(AuthState.VALIDATION_CODE_HAS_ALREADY_BEEN_RESENT);
                            break;
                        }
                        default:{
                            authState.setState(AuthState.STATE_HAS_ERROR);
                            throw new Exception(jsonObject.getString("error_description"));
                        }
                    }
                }
            }
        }
    }

    private void checkError(JSONObject jsonObject) throws Exception {
        Log.d(LOGIN_TAG, jsonObject.toString());

        if(jsonObject.has("validation_sid")){
            authState.updateValidation(jsonObject);
            authState.setState(STATE_NEED_VALIDATION);
            process();
        }else {
            if(jsonObject.has("captcha_sid")){
                authState.updateCaptcha(jsonObject);
                authState.setState(STATE_NEED_CAPTCHA);
            }else {
                authState.setState(AuthState.STATE_HAS_ERROR);
                if(jsonObject.has("error_description")){
                    throw new Exception(jsonObject.getString("error_description"));
                }else {
                    throw new Exception(jsonObject.getString("error"));
                }
            }
        }
    }

    private Call<JSONObject> validatePhone() {
        HashMap<String,Object> fields = defaultValidatePhoneFields();
        return oauthMethod().validatePhone(
                authState.getValidationSid(),
                getDeviceId(loginActivity.getApplicationContext()),
                fields);
    }

    private Call<JSONObject> sendValidationCode() {
        HashMap<String,Object> fields = defaultLoginFields();
        fields.put("code", authState.getValidationCode());
        return oauth().token(
                authState.getUsername(),
                authState.getPassword(),
                getDeviceId(loginActivity),
                fields);
    }

    private Call<JSONObject> sendCaptcha() {
        HashMap<String,Object> fields = defaultLoginFields();
        fields.put("captcha_key",authState.getCaptchaKey());
        fields.put("captcha_sid",authState.getCaptchaSid());
        return oauth().token(
                authState.getUsername(),
                authState.getPassword(),
                getDeviceId(loginActivity),
                fields);
    }

    private Call<JSONObject> requestToken() {
        HashMap<String,Object> fields = defaultLoginFields();
        return oauth().token(authState.getUsername(), authState.getPassword(),
                getDeviceId(loginActivity),fields);
    }

    @Override
    public void onExecute(Boolean object) {
        if(object){
            int state = authState.getState();
            if(state == AuthState.STATE_SUCCESS){
                new TokenRefresh(authState,loginActivity).start();
            }else {
                loginActivity.setCanLogin(true);
                switch (state){
                    case STATE_NEED_VALIDATION: {
                        loginActivity.showValidationCodeFrame(authState);
                        break;
                    }
                    case AuthState.STATE_NEED_CAPTCHA:{
                        loginActivity.showCaptchaCodeFrame(authState);
                        break;
                    }
                }
                loginActivity.setProgressBarVisibility(GONE);
            }
        }else {
            loginActivity.setCanLogin(true);
            loginActivity.setProgressBarVisibility(GONE);
        }
    }
}
