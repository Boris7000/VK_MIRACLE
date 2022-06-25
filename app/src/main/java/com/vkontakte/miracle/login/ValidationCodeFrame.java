package com.vkontakte.miracle.login;

import static com.vkontakte.miracle.engine.util.StringsUtil.getTrimmed;
import static com.vkontakte.miracle.login.AuthState.VALIDATION_CODE_HAS_ALREADY_BEEN_RESENT;
import static com.vkontakte.miracle.login.AuthState.VALIDATION_CODE_RESENDS_LIMIT;
import static com.vkontakte.miracle.login.AuthState.VALIDATION_TYPE_APP;
import static com.vkontakte.miracle.login.AuthState.VALIDATION_TYPE_CALL;
import static com.vkontakte.miracle.login.AuthState.VALIDATION_TYPE_SMS;

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

import com.miracle.button.TextViewButton;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.TimeUtil;

import java.util.Locale;

public class ValidationCodeFrame extends LinearLayout {

    private EditText validationCodeField;
    private TextViewButton sendButton;
    private TextViewButton cancelButton;
    private LinearLayout forceSMSHolder;
    private TextViewButton forceSMSButton;
    private TextView forceSMSTimer;
    private AsyncExecutor<Boolean> timer;
    private AuthState authState;

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
        sendButton = findViewById(R.id.sendButton);
        cancelButton = findViewById(R.id.cancelButton);
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
                sendButton.setEnabled(!getTrimmed(validationCodeField).isEmpty(), true);
            }
        };
        validationCodeField.addTextChangedListener(textWatcher);

    }

    public void setValues(AuthState authState, LoginActivity loginActivity){

        this.authState = authState;

        authState.setValidationCode(null);

        validationCodeField.requestFocus();

        switch (authState.getValidationType()){
            case VALIDATION_TYPE_CALL:{
                loginActivity.setText(loginActivity.getString(R.string.validationCallDescription));
                forceSMSButton.setText(loginActivity.getString(R.string.forceValidationCall));
                break;
            }
            case VALIDATION_TYPE_SMS:{
                loginActivity.setText(String.format(loginActivity.getString(
                        R.string.validationSMSDescription), authState.getPhoneMask()));
                forceSMSButton.setText(loginActivity.getString(R.string.forceValidationSMS));
                break;
            }
            case VALIDATION_TYPE_APP:{
                loginActivity.setText(loginActivity.getString(R.string.validationAppDecription));
                forceSMSButton.setText(loginActivity.getString(R.string.forceValidationSMS));
                break;
            }
        }

        if(authState.getForceCodeUnableReason()==0) {
            if (authState.getDelay() == 0) {
                forceSMSTimer.setVisibility(GONE);
                forceSMSButton.setEnabled(true);
            } else {
                startTimer();
            }
        } else {
            switch (authState.getForceCodeUnableReason()){
                case VALIDATION_CODE_HAS_ALREADY_BEEN_RESENT:{
                    forceSMSButton.setText(String.format(loginActivity.getString(
                            R.string.validationCodeSMSHasAlreadyBennResent), authState.getPhoneMask()));
                    startTimer();
                    break;
                }
                case VALIDATION_CODE_RESENDS_LIMIT:{
                    forceSMSButton.setText(loginActivity.getString(R.string.validationCodeSMSLimit));
                    forceSMSTimer.setVisibility(GONE);
                    forceSMSButton.setEnabled(false);
                    break;
                }
            }
        }

        forceSMSButton.setOnClickListener(view -> {
            if(forceSMSButton.isEnabled()) {
                if (loginActivity.canLogin()) {
                    authState.setValidationCode(null);
                    new Authentication(authState, loginActivity).start();
                }
            }
        });

        sendButton.setOnClickListener(view -> {
            if(sendButton.isEnabled()) {
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

    public void clearFocus(){
        validationCodeField.clearFocus();
    }

    private void stopTimer(){
        if(timer != null && !timer.workIsDone()) {
            timer.interrupt();
            timer = null;
        }
    }

    private void startTimer() {
        forceSMSHolder.setVisibility(VISIBLE);
        forceSMSTimer.setVisibility(VISIBLE);
        forceSMSButton.setEnabled(false);
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
                                Thread.sleep(Math.max(1,Math.min(remain,1000L)));
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
                            forceSMSButton.setEnabled(true, true);
                        }
                    }
                };
                timer.start();
            }
        } else {
            forceSMSTimer.setVisibility(GONE);
            forceSMSButton.setEnabled(true);
        }
    }
}
