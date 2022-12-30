package com.example.notebook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    public final String ACTION = "NIGHT_SWITCH";
    protected IntentFilter filter;
    protected BroadcastReceiver receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNightMode();

        filter = new IntentFilter();
        filter.addAction(ACTION);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                needRefresh();
            }
        };

        registerReceiver(receiver, filter);
    }

    public boolean isNightMode(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return sharedPreferences.getBoolean("nightMode", false);
    }

    protected abstract void needRefresh();

    public void setNightMode(){
        if(isNightMode()) this.setTheme(R.style.NightTheme);
        else setTheme(R.style.DayTheme);
    }

}
