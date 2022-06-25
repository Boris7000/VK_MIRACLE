package com.vkontakte.miracle.login;

import static com.vkontakte.miracle.engine.util.StringsUtil.getTrimmed;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.miracle.button.TextViewButton;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;

public class CaptchaCodeFrame extends LinearLayout {

    private EditText validationCodeField;
    private TextViewButton sendButton;
    private TextViewButton cancelButton;
    private ImageView captchaImage;

    public CaptchaCodeFrame(Context context) {
        super(context);
    }

    public CaptchaCodeFrame(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        validationCodeField = findViewById(R.id.captchaCodeField);
        sendButton = findViewById(R.id.sendButton);
        cancelButton = findViewById(R.id.cancelButton);
        captchaImage = findViewById(R.id.captchaImage);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                sendButton.setEnabled(getTrimmed(validationCodeField).length()>0, true);
            }
        };
        validationCodeField.addTextChangedListener(textWatcher);

    }

    public void setValues(AuthState authState, LoginActivity loginActivity){

        authState.setCaptchaKey(null);

        validationCodeField.requestFocus();

        loginActivity.setText(loginActivity.getString(R.string.captchaNeeded));

        sendButton.setOnClickListener(view -> {
            if(sendButton.isEnabled()) {
                if(loginActivity.canLogin()) {
                    authState.setCaptchaKey(getTrimmed(validationCodeField));
                    new Authentication(authState, loginActivity).start();
                    loginActivity.setCanLogin(false);
                }
            }else {
                loginActivity.setText(loginActivity.getString(R.string.missingCode));
            }
        });

        cancelButton.setOnClickListener(view -> {
            loginActivity.setCanLogin(true);
            loginActivity.setText("");
            loginActivity.hideCaptchaCodeFrame();
        });

        Picasso.get().load(authState.getCaptchaImg()).into(captchaImage);
    }

    public void clearFocus(){
        validationCodeField.clearFocus();
    }
}
