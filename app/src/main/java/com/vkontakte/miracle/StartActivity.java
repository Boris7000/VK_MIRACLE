package com.vkontakte.miracle;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.vkontakte.miracle.engine.util.SettingsUtil;
import com.vkontakte.miracle.login.LoginActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if(SettingsUtil.get().authorized()) {
            Intent intent = new Intent(this, MiracleActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        }
    }
}