package com.vkontakte.miracle.engine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.context.ContextExtractor;
import com.vkontakte.miracle.engine.view.ActivityRootView;

import java.util.ArrayList;

public abstract class MiracleActivity extends AppCompatActivity {

    private ActivityRootView rootView;
    private View decorView;
    private WindowInsetsCompat windowInsets;
    private Bundle notUsedSavedInstanceState;

    private final ArrayList<OnApplyWindowInsetsListener> onApplyWindowInsetsListeners = new ArrayList<>();

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(savedInstanceState==null){
            savedInstanceState = savedInstanceStateCrutch();
        }

        super.onCreate(savedInstanceState);

        if(savedInstanceState!=null&&!savedInstanceState.isEmpty()){
            readSavedInstance(savedInstanceState);
        }

        Bundle arguments = getIntent().getExtras();
        if(arguments!=null&&!arguments.isEmpty()){
            readBundleArguments(arguments);
        }

        MiracleApp miracleApp = ContextExtractor.extractMiracleApp(getApplicationContext());
        if(miracleApp!=null) {
            setTheme(miracleApp.getThemeRecourseId());
        }

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(getRootViewResource());

        decorView = getWindow().getDecorView();

        rootView = findViewById(R.id.rootView);

        findViews(rootView);

        initViews();

        iniWindowInsets();

    }

    public Bundle savedInstanceStateCrutch(){
        return getIntent().getBundleExtra("savedInstanceState");
    }

    public abstract int getRootViewResource();

    public ActivityRootView getRootView() {
        return rootView;
    }

    public void findViews(@NonNull View rootView) {}

    public void initViews() {}

    public void readSavedInstance(Bundle savedInstanceState){}

    public void onNewWindowInsets(WindowInsetsCompat windowInsets){}

    private void iniWindowInsets(){
        ViewCompat.setOnApplyWindowInsetsListener(decorView, (v, windowInsets) -> {
            this.windowInsets = windowInsets;

            rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    rootView.removeOnLayoutChangeListener(this);
                    onNewWindowInsets(windowInsets);
                }
            });

            for (OnApplyWindowInsetsListener onApplyWindowInsetsListener : onApplyWindowInsetsListeners) {
                onApplyWindowInsetsListener.onApplyWindowInsets(v, windowInsets);
            }

            return windowInsets;
        });
    }

    public WindowInsetsCompat getWindowInsets() {
        return windowInsets;
    }

    public void addOnApplyWindowInsetsListener(OnApplyWindowInsetsListener onApplyWindowInsetsListener){
        if(windowInsets !=null) {
            onApplyWindowInsetsListener.onApplyWindowInsets(getWindow().getDecorView(), windowInsets);
        }
        onApplyWindowInsetsListeners.add(onApplyWindowInsetsListener);
    }

    public void removeOnApplyWindowInsetsListener(OnApplyWindowInsetsListener onApplyWindowInsetsListener){
        onApplyWindowInsetsListeners.remove(onApplyWindowInsetsListener);
    }

    public void readBundleArguments(Bundle arguments){}

    @CallSuper
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        getIntent().removeExtra("savedInstanceState");
    }

    @CallSuper
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        notUsedSavedInstanceState = outState;
        super.onSaveInstanceState(outState);
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        if(notUsedSavedInstanceState !=null&&!notUsedSavedInstanceState.isEmpty()) {
            onClearSavedInstance(notUsedSavedInstanceState);
        }
    }

    @CallSuper
    @Override
    public void recreate() {
        Intent intent = getIntent();
        intent.setFlags(0);
        Bundle bundle = new Bundle();
        onSaveInstanceState(bundle);
        intent.putExtra("savedInstanceState", bundle);
        startActivity(intent, bundle);
        finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }
}
