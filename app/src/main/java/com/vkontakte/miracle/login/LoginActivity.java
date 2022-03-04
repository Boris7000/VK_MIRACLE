package com.vkontakte.miracle.login;

import static com.vkontakte.miracle.engine.util.AdapterUtil.getHorizontalLayoutManager;
import static com.vkontakte.miracle.engine.util.StringsUtil.getTrimmed;
import static com.vkontakte.miracle.network.Constants.fake_receipt;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.login.AccountsAdapter;
import com.vkontakte.miracle.engine.util.DimensionsUtil;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.engine.view.MiracleButton;
import com.vkontakte.miracle.model.users.ProfileItem;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private FrameLayout frameContainer;
    private EditText loginField;
    private EditText passField;
    private TextView loginText;
    private ProgressBar loginProgress;
    private LinearLayout loginFrame;
    private ValidationCodeFrame validationCodeFrame;
    private ViewStub validationCodeFrameStub;
    private CaptchaCodeFrame captchaCodeFrame;
    private ViewStub captchaCodeFrameStub;
    private MiracleApp miracleApp;
    private boolean canLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        miracleApp = (MiracleApp) getApplication();

        setTheme(miracleApp.getThemeRecourseId());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ScrollView rootView = findViewById(R.id.rootView);

        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, windowInsets) -> {
            Insets systemBarsInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets imeInsets = windowInsets.getInsets(WindowInsetsCompat.Type.ime());

            int bottomInsets = Math.max(systemBarsInsets.bottom,imeInsets.bottom);

            v.setPadding(systemBarsInsets.left,systemBarsInsets.top,
                    systemBarsInsets.right,bottomInsets);
            return windowInsets;
        });

        ImageView logo = rootView.findViewById(R.id.logo);

        frameContainer = rootView.findViewById(R.id.frameContainer);
        loginFrame = frameContainer.findViewById(R.id.loginFrame);
        MiracleButton loginButton = loginFrame.findViewById(R.id.loginButton);
        loginField = loginFrame.findViewById(R.id.loginField);
        passField = loginFrame.findViewById(R.id.passField);
        validationCodeFrameStub = frameContainer.findViewById(R.id.validationCodeFrameStub);
        captchaCodeFrameStub = frameContainer.findViewById(R.id.captchaCodeFrameStub);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        loginText = rootView.findViewById(R.id.loginText);
        loginProgress = rootView.findViewById(R.id.loginProgress);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                loginButton.setActive(getTrimmed(loginField).length()>0 && getTrimmed(passField).length()>0);
            }
        };
        loginField.addTextChangedListener(textWatcher);
        passField.addTextChangedListener(textWatcher);

        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
            if(isOpen) {
                recyclerView.setVisibility(View.GONE);
                new Handler(getMainLooper()).postDelayed(() ->
                        scaleView(48, logo),200);
            } else{
                loginField.clearFocus();
                passField.clearFocus();
                recyclerView.setVisibility(View.VISIBLE);
                /*int count = recyclerView.getChildCount();
                for(int i=0; i<count; i++){
                    recyclerView.getChildAt(i).requestLayout();
                }*/
                scaleView(180, logo);
            }
        });

        loginButton.setOnClickListener(view -> {
            if(loginButton.isActive()) {
                startLogin(getTrimmed(loginField),getTrimmed(passField));
            }else {
                setText(getString(R.string.missingLoginOrPass));
            }
        });

        StorageUtil.initializeDirectories(this);
        ArrayList<ProfileItem> accounts = StorageUtil.loadUsers(this);
        AccountsAdapter accountsAdapter = new AccountsAdapter(accounts, this);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(getHorizontalLayoutManager(this));
        recyclerView.setAdapter(accountsAdapter);

    }

    private void scaleView(int toScale, View view){
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        ValueAnimator va = ValueAnimator.ofInt(layoutParams.height,(int) DimensionsUtil.dpToPx(toScale,this));
        va.setDuration(300);
        va.setInterpolator(new DecelerateInterpolator());
        va.addUpdateListener(animation ->{
            layoutParams.height = (int) animation.getAnimatedValue();
            view.setLayoutParams(layoutParams);
        });
        va.start();
    }

    private void startLogin(String login, String pass){
        if(canLogin) {
            canLogin = false;
            miracleApp.getFCMToken(task -> {
                String receipt;
                if (task.isSuccessful()) {
                    receipt = task.getResult();
                } else {
                    receipt = fake_receipt;
                }

                new Authentication(AuthState.fromFields(login, pass, receipt),
                        LoginActivity.this).start();
            });
        }
    }

    public boolean canLogin() {
        return canLogin;
    }

    public void setText(String string){
        loginText.post(() -> {
            ViewGroup viewGroup = (ViewGroup) loginText.getParent().getParent().getParent();
            TransitionManager.beginDelayedTransition(viewGroup);
            if(string.isEmpty()){
                if(loginText.getVisibility()!=View.INVISIBLE){
                    loginText.setVisibility(View.INVISIBLE);
                }
            }else {
                if(loginText.getVisibility()!=View.VISIBLE){
                    loginText.setVisibility(View.VISIBLE);
                }
            }
            loginText.setText(string);
        });
    }

    public void setProgressBarVisibility(int visibility){
        if(loginProgress.getVisibility()!=visibility){
            ViewGroup viewGroup = (ViewGroup) loginProgress.getParent().getParent().getParent();
            TransitionManager.beginDelayedTransition(viewGroup);
            loginProgress.post(() -> loginProgress.setVisibility(visibility));
        }
    }

    public void showValidationCodeFrame(AuthState authState){
        if(validationCodeFrame==null) {
            if(validationCodeFrameStub!=null) {
                ViewGroup viewGroup = (ViewGroup) frameContainer.getParent();
                TransitionManager.beginDelayedTransition(viewGroup);
                loginFrame.setVisibility(View.GONE);
                validationCodeFrame = (ValidationCodeFrame) validationCodeFrameStub.inflate();
            } else {
                validationCodeFrame = frameContainer.findViewById(R.id.validationCodeFrame);
            }
        }
        if(validationCodeFrame.getVisibility()!=View.VISIBLE){
            ViewGroup viewGroup = (ViewGroup) frameContainer.getParent();
            TransitionManager.beginDelayedTransition(viewGroup);
            loginFrame.setVisibility(View.GONE);
            validationCodeFrame.setVisibility(View.VISIBLE);
        }
        validationCodeFrame.setValues(authState, this);
    }

    public void hideValidationCodeFrame(){
        if(validationCodeFrame!=null) {
            if (validationCodeFrame.getVisibility() == View.VISIBLE) {
                ViewGroup viewGroup = (ViewGroup) frameContainer.getParent();
                TransitionManager.beginDelayedTransition(viewGroup);
                validationCodeFrame.setVisibility(View.GONE);
                loginFrame.setVisibility(View.VISIBLE);
            }
        }
    }

    public void showCaptchaCodeFrame(AuthState authState){
        if(captchaCodeFrame==null) {
            if(captchaCodeFrameStub!=null) {
                ViewGroup viewGroup = (ViewGroup) frameContainer.getParent();
                TransitionManager.beginDelayedTransition(viewGroup);
                loginFrame.setVisibility(View.GONE);
                captchaCodeFrame = (CaptchaCodeFrame) captchaCodeFrameStub.inflate();
            } else {
                captchaCodeFrame = frameContainer.findViewById(R.id.captchaCodeFrame);
            }
        }
        if(captchaCodeFrame.getVisibility()!=View.VISIBLE){
            ViewGroup viewGroup = (ViewGroup) frameContainer.getParent();
            TransitionManager.beginDelayedTransition(viewGroup);
            loginFrame.setVisibility(View.GONE);
            captchaCodeFrame.setVisibility(View.VISIBLE);
        }
        captchaCodeFrame.setValues(authState, this);
    }

    public void hideCaptchaCodeFrame(){
        if(captchaCodeFrame!=null) {
            if (captchaCodeFrame.getVisibility() == View.VISIBLE) {
                ViewGroup viewGroup = (ViewGroup) frameContainer.getParent();
                TransitionManager.beginDelayedTransition(viewGroup);
                captchaCodeFrame.setVisibility(View.GONE);
                loginFrame.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setCanLogin(boolean canLogin) {
        this.canLogin = canLogin;
    }

    public MiracleApp getMiracleApp() {
        return miracleApp;
    }
}
