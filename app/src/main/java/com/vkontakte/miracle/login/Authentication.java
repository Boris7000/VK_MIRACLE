package com.vkontakte.miracle.login;

import static android.view.View.GONE;
import static com.vkontakte.miracle.engine.util.DeviceUtil.getDeviceId;
import static com.vkontakte.miracle.engine.util.LogTags.LOGIN_TAG;
import static com.vkontakte.miracle.engine.util.NetworkUtil.CheckConnection;
import static com.vkontakte.miracle.network.Constants.defaultHeaders;
import static com.vkontakte.miracle.network.Constants.defaultLoginFields;
import static com.vkontakte.miracle.network.Constants.defaultValidatePhoneFields;
import static com.vkontakte.miracle.network.Creator.oauth;
import static com.vkontakte.miracle.network.Creator.oauthMethod;

import android.util.Log;
import android.view.View;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;

import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class Authentication extends AsyncExecutor<Boolean> {


    private final LoginActivity loginActivity;
    private final AuthState authState;

    public Authentication(AuthState authState, LoginActivity loginActivity){
        this.authState = authState;
        this.loginActivity= loginActivity;
    }

    @Override
    public Boolean inBackground() {
        try {
            CheckConnection(3500);

            HashMap<String,String> headers = defaultHeaders();
            Call<JSONObject> call;
            Response<JSONObject> response;

            if(authState.getValidationSid()!=null){
                String validationCode = authState.getValidationCode();
                if(validationCode!=null){
                    loginActivity.setText(loginActivity.getString(R.string.sendingCode));
                    loginActivity.setProgressBarVisibility(View.VISIBLE);
                    HashMap<String,Object> fields = defaultLoginFields();
                    fields.put("code",validationCode);
                    call = oauth().token(authState.getUsername(), authState.getPassword(),
                            getDeviceId(loginActivity), fields, headers);
                } else {
                    loginActivity.setText(loginActivity.getString(R.string.forceSMS));
                    loginActivity.setProgressBarVisibility(View.VISIBLE);
                    HashMap<String,Object> fields = defaultValidatePhoneFields();
                    call = oauthMethod().validatePhone(authState.getValidationSid(), fields, headers);
                }
            } else {
                if(authState.getCaptchaSid()!=null){
                    loginActivity.setText(loginActivity.getString(R.string.sendingCode));
                    loginActivity.setProgressBarVisibility(View.VISIBLE);
                    HashMap<String,Object> fields = defaultLoginFields();
                    fields.put("captcha_key",authState.getCaptchaKey());
                    fields.put("captcha_sid",authState.getCaptchaSid());
                    call = oauth().token(authState.getUsername(), authState.getPassword(),
                            getDeviceId(loginActivity),fields, headers);
                }else {
                    loginActivity.setText(loginActivity.getString(R.string.authentication));
                    loginActivity.setProgressBarVisibility(View.VISIBLE);
                    HashMap<String,Object> fields = defaultLoginFields();
                    call = oauth().token(authState.getUsername(), authState.getPassword(),
                            getDeviceId(loginActivity),fields, headers);
                }
            }

            response = call.execute();

            if(response.errorBody()!=null){
                JSONObject errorObject =  new JSONObject(response.errorBody().string());
                if(errorObject.has("validation_type")){
                    authState.updateValidation(errorObject);
                }else {
                    if(errorObject.has("captcha_sid")){
                        authState.updateCaptcha(errorObject);
                    }else {
                        if(errorObject.has("error_description")){
                            throw new Exception(errorObject.getString("error_description"));
                        }else {
                            throw new Exception(errorObject.getString("error"));
                        }
                    }
                }
            } else {
                JSONObject jsonObject = response.body();
                if(jsonObject!=null){
                    if (jsonObject.has("access_token")){
                        authState.setToken(jsonObject.getString("access_token"));
                        authState.setUserId(jsonObject.getString("user_id"));
                        authState.setState(AuthState.STATE_SUCCESS);
                    }else {
                        if(jsonObject.has("response")){
                            jsonObject = jsonObject.getJSONObject("response");
                            authState.updatePhoneValidation(jsonObject);
                        }else {
                            if(jsonObject.has("error")){
                                JSONObject errorObject = jsonObject.getJSONObject("error");
                                int error_code = errorObject.getInt("error_code");
                                switch (error_code){
                                    case 103:{
                                        authState.updateFakePhoneValidation();
                                        authState.setState(AuthState.STATE_SMS_CODE_RESENDS_LIMIT);
                                        break;
                                    }
                                    case 1112:{
                                        authState.updateFakePhoneValidationAlready();
                                        authState.setState(AuthState.STATE_SMS_CODE_HAS_ALREADY_BEEN_RESENT);
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
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            String eString = e.toString();
            loginActivity.setText(eString.substring(eString.indexOf("Exception:")+11));
            Log.d(LOGIN_TAG,eString);
        }
        return false;
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
                        case AuthState.STATE_NEED_VALIDATION:
                        case AuthState.STATE_NEED_SMS_VALIDATION:
                        case AuthState.STATE_NEED_APP_VALIDATION:
                        case AuthState.STATE_SMS_CODE_HAS_ALREADY_BEEN_RESENT:
                        case AuthState.STATE_SMS_CODE_RESENDS_LIMIT:{
                            loginActivity.showValidationCodeFrame(authState);
                            break;
                        }
                        case AuthState.STATE_NEED_LIBVERIFY_VALIDATION:{
                            new Authentication(authState,loginActivity).start();
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
