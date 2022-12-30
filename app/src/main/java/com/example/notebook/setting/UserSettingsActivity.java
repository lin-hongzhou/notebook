package com.example.notebook.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.notebook.BaseActivity;
import com.example.notebook.R;

public class UserSettingsActivity extends BaseActivity {

    private Switch nightMode;
    //储存数据
    private SharedPreferences sharedPreferences;

    private static boolean night_change;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_layout);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Intent intent = getIntent();
        if(intent.getExtras() != null)
            night_change = intent.getBooleanExtra("night_change", false);
        else
            night_change = false;

        initView();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("NIGHT_SWITCH");
                sendBroadcast(intent);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

//        if(isNightMode())
//            myToolbar.setNavigationIcon(getDrawable(R.drawable.ic_settings_white_24dp));
//        else
//            myToolbar.setNavigationIcon(getDrawable(R.drawable.ic_settings_black_24dp));
    }


    /**
     * 黑夜模式
     */
    private void initView(){
        nightMode = findViewById(R.id.nightMode);
        nightMode.setChecked(sharedPreferences.getBoolean("nightMode", false));
        nightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setNightModePref(isChecked);
                setSelfNightMode();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean temp = false;
            }
        });
    }

    private void setNightModePref(boolean night){
        //通过nightMode switch修改pref中的nightMode
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("nightMode", night);
        editor.commit();
    }

    private void setSelfNightMode(){
        //重新赋值并重启本activity
        super.setNightMode();
        Intent intent = new Intent(this, UserSettingsActivity.class);
        intent.putExtra("night_change", !night_change); //重启一次，正负颠倒。最终为正值时重启MainActivity。

        startActivity(new Intent(this, UserSettingsActivity.class));
        overridePendingTransition(R.anim.night_switch, R.anim.night_switch_over);
        finish();
    }

    //返回键保存设置
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            Intent intent = new Intent();
            intent.setAction("NIGHT_SWITCH");
            sendBroadcast(intent);
            finish();
            overridePendingTransition(R.anim.in_lefttoright, R.anim.out_lefttoright);
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    protected void needRefresh() {
        //因为自身的刷新与其他activity不同步，所以此处留白
    }
}
