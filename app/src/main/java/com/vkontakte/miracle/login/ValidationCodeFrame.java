package com.vkontakte.miracle.login;

import static com.vkontakte.miracle.engine.util.StringsUtil.getTrimmed;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.TimeUtil;
import com.vkontakte.miracle.engine.view.MiracleButton;

import java.util.Locale;

public class ValidationCodeFrame extends LinearLayout {

    private EditText validationCodeField;
    private MiracleButton sendButton;
    private MiracleButton cancelButton;
    private LinearLayout forceSMSHolder;
    private MiracleButton forceSMSButton;
    private TextView forceSMSTimer;
    private AsyncExecutor<Boolean> timer;

    public ValidationCodeFrame(Context context) {
        super(context);
    }

    public ValidationCodeFrame(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        validationCodeField = findViewById(R.id.validationCodeField);
        sendButton = findViewById(R.id.validationCodeButton);
        cancelButton = findViewById(R.id.validationCodeCancelButton);
        forceSMSHolder = findViewById(R.id.validationCodeForceSMSHolder);
        forceSMSButton = forceSMSHolder.findViewById(R.id.validationCodeForceSMSButton);
        forceSMSTimer = forceSMSHolder.findViewById(R.id.validationCodeForceSMSTimer);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                sendButton.setActive(getTrimmed(validationCodeField).length()>0);
            }
        };
        validationCodeField.addTextChangedListener(textWatcher);

    }

    public void setValues(AuthState authState, LoginActivity loginActivity){

        authState.setValidationCode(null);

        validationCodeField.requestFocus();

        switch (authState.getState()) {
            case AuthState.STATE_NEED_APP_VALIDATION:{
                loginActivity.setText(loginActivity.getString(R.string.validationCodeAPP));
                forceSMSHolder.setVisibility(VISIBLE);
                forceSMSButton.setText(loginActivity.getString(R.string.forceValidationCodeSMS));
                break;
            }
            case AuthState.STATE_NEED_VALIDATION: {
                loginActivity.setText(loginActivity.getString(R.string.validationCodeNeeded));
                forceSMSHolder.setVisibility(VISIBLE);
                forceSMSButton.setText(loginActivity.getString(R.string.forceValidationCodeSMS));
                break;
            }
            case AuthState.STATE_NEED_SMS_VALIDATION: {
                loginActivity.setText(String.format(loginActivity.getString(R.string.validationCodeSMS),
                        authState.getPhoneMask()));
                forceSMSButton.setText(loginActivity.getString(R.string.resendValidationCodeSMS));
                startTimer(authState);
                break;
            }
            case AuthState.STATE_SMS_CODE_HAS_ALREADY_BEEN_RESENT: {
                loginActivity.setText(String.format(loginActivity.getString(R.string.validationCodeSMSHasAlreadyBennResent),
                        authState.getPhoneMask()));
                forceSMSButton.setText(loginActivity.getString(R.string.resendValidationCodeSMS));
                startTimer(authState);
                break;
            }
            case AuthState.STATE_SMS_CODE_RESENDS_LIMIT: {
                loginActivity.setText(loginActivity.getString(R.string.validationCodeSMSLimit));
                forceSMSHolder.setVisibility(GONE);
                break;
            }
        }

        forceSMSButton.setOnClickListener(view -> {
            if(forceSMSButton.isActive()) {
                if (loginActivity.canLogin()) {
                    authState.setValidationCode(null);
                    new Authentication(authState, loginActivity).start();
                }
            }
        });

        sendButton.setOnClickListener(view -> {
            if(sendButton.isActive()) {
                if(loginActivity.canLogin()) {
                    authState.setValidationCode(getTrimmed(validationCodeField));
                    new Authentication(authState, loginActivity).start();
                    loginActivity.setCanLogin(false);
                }
            }else {
                loginActivity.setText(loginActivity.getString(R.string.missingValidationCode));
            }
        });

        cancelButton.setOnClickListener(view -> {
            stopTimer();
            loginActivity.setCanLogin(true);
            loginActivity.setText("");
            loginActivity.hideValidationCodeFrame();
        });

    }

    private void stopTimer(){
        if(timer != null && !timer.workIsDone()) {
            timer.interrupt();
            timer = null;
        }
    }

    private void startTimer(AuthState authState) {
        forceSMSHolder.setVisibility(VISIBLE);
        forceSMSTimer.setVisibility(VISIBLE);
        forceSMSButton.setActive(false);
        final long finalRemain = Math.max(authState.getDelay()*1000L
                -(System.currentTimeMillis()-authState.getResponseTime()),0);
        if(finalRemain>0){
            if(timer == null || timer.workIsDone()) {
                timer = new AsyncExecutor<Boolean>() {
                    @Override
                    public Boolean inBackground() {
                        Locale locale = Locale.getDefault();
                        long remain = finalRemain;
                        while (remain>0){
                            remain-=1000;
                            String timerString = TimeUtil.getDurationStringMills(locale, remain);
                            new Handler(Looper.getMainLooper()).post(() -> forceSMSTimer.setText(timerString));
                            try {
                                Thread.sleep(Math.min(remain,1000L));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                        return true;
                    }
                    @Override
                    public void onExecute(Boolean object) {
                        if(object) {
                            forceSMSTimer.setVisibility(GONE);
                            forceSMSButton.setActive(true);
                        }
                    }
                };
                timer.start();
            }
        } else {
            forceSMSTimer.setVisibility(GONE);
            forceSMSButton.setActive(true);
        }
    }
}
